package ru.mirea.zhemaytisvs.fitmotiv.presentation.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.activities.LoginActivity;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private ImageView ivProfile;
    private TextView tvEmail, tvName, tvTotalTrainings, tvTotalCalories;
    private Button btnChangePhoto, btnTakePhoto, btnLogout;

    private static final int REQUEST_PICK_PHOTO = 100;
    private static final int REQUEST_TAKE_PHOTO = 101;

    private FirebaseStorage storage;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализируем Firebase Storage
        storage = FirebaseStorage.getInstance();

        initializeViewModel();
        initializeUI(view);
        setupLiveDataObservers();
        setupEventListeners();
        loadUserData();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    private void initializeUI(View view) {
        ivProfile = view.findViewById(R.id.ivProfile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvName = view.findViewById(R.id.tvName);
        tvTotalTrainings = view.findViewById(R.id.tvTotalTrainings);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupLiveDataObservers() {
        // Наблюдатель для пользователя
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUserInfo(user);
            }
        });

        // Наблюдатель для статистики тренировок
        viewModel.getWorkoutStatisticsLiveData().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                if (tvTotalTrainings != null) {
                    tvTotalTrainings.setText("Всего тренировок: " + stats.getTotalWorkouts());
                }
                if (tvTotalCalories != null) {
                    tvTotalCalories.setText("Всего калорий: " + stats.getTotalCalories());
                }
            }
        });
    }

    private void updateUserInfo(User user) {
        tvEmail.setText(user.getEmail());
        tvName.setText(user.getDisplayName() != null ?
                user.getDisplayName() :
                user.getEmail().split("@")[0]); // Берем часть до @ как имя

        // Загрузка аватарки с помощью Glide
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(ivProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(ivProfile);
        }
    }

    private void setupEventListeners() {
        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(v -> openImagePicker());
        }

        if (btnTakePhoto != null) {
            btnTakePhoto.setOnClickListener(v -> takePhoto());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> handleLogout());
        }
    }

    private void handleLogout() {
        User currentUser = viewModel.getUserLiveData().getValue();

        if (currentUser != null && currentUser.isGuest()) {
            // Если пользователь гость, просто переходим на экран авторизации
            navigateToLoginActivity();
        } else {
            // Для зарегистрированного пользователя выполняем выход
            Toast.makeText(requireContext(), "Выход из аккаунта...", Toast.LENGTH_SHORT).show();

            viewModel.logout(new ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository.LogoutCallback() {
                @Override
                public void onSuccess() {
                    // После успешного выхода переходим на экран авторизации
                    requireActivity().runOnUiThread(() -> {
                        navigateToLoginActivity();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Ошибка выхода: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }

    private void navigateToLoginActivity() {
        try {
            Intent intent = new Intent(requireContext(), LoginActivity.class);

            // Очищаем историю стека, чтобы нельзя было вернуться назад
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Можно передать флаг, что это выход
            intent.putExtra("from_logout", true);

            startActivity(intent);

            // Завершаем текущую Activity
            requireActivity().finish();

        } catch (Exception e) {
            Log.e("ProfileFragment", "Error navigating to LoginActivity", e);
            Toast.makeText(requireContext(), "Ошибка перехода на экран входа", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(requireContext(), "Камера не найдена", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {

            if (requestCode == REQUEST_PICK_PHOTO) {
                // Фото из галереи
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    uploadImageToFirebase(selectedImageUri);
                }

            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                // Фото с камеры
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        // Сохраняем временный файл и загружаем
                        Uri tempUri = saveBitmapToTempFile(imageBitmap);
                        if (tempUri != null) {
                            uploadImageToFirebase(tempUri);
                        }
                    }
                }
            }
        }
    }

    private Uri saveBitmapToTempFile(Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("profile_photo", ".jpg", requireContext().getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return Uri.fromFile(tempFile);
        } catch (IOException e) {
            Log.e("ProfileFragment", "Error saving temp file", e);
            return null;
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null) return;

        try {
            // Показываем прогресс
            Toast.makeText(requireContext(), "Загрузка фото...", Toast.LENGTH_SHORT).show();

            // Получаем текущего пользователя
            User currentUser = viewModel.getUserLiveData().getValue();
            if (currentUser == null || currentUser.isGuest()) {
                Toast.makeText(requireContext(), "Только зарегистрированные пользователи могут загружать фото", Toast.LENGTH_SHORT).show();
                return;
            }

            // Генерируем уникальное имя файла
            String fileName = "profile_" + currentUser.getUid() + "_" + UUID.randomUUID().toString() + ".jpg";
            StorageReference storageRef = storage.getReference().child("profile_photos").child(fileName);

            // Загружаем изображение
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Получаем URL загруженного изображения
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photoUrl = uri.toString();
                    Log.d("ProfileFragment", "Photo uploaded: " + photoUrl);

                    // Обновляем фото профиля в Firebase Auth
                    updateProfilePhoto(photoUrl);

                }).addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error getting download URL", e);
                    Toast.makeText(requireContext(), "Ошибка получения ссылки на фото", Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                Log.e("ProfileFragment", "Error uploading photo", e);
                Toast.makeText(requireContext(), "Ошибка загрузки фото: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Log.e("ProfileFragment", "Error in uploadImageToFirebase", e);
            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfilePhoto(String photoUrl) {
        viewModel.updateProfilePhoto(photoUrl, new ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                // Обновляем изображение локально
                Glide.with(ProfileFragment.this)
                        .load(photoUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(ivProfile);

                Toast.makeText(requireContext(), "Фото профиля обновлено!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Ошибка обновления фото: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        viewModel.loadUserData();
    }
}