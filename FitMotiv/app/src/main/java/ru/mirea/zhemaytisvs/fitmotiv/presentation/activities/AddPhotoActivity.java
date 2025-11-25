package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AddPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    
    private ImageView ivPreview;
    private EditText etDescription;
    private Button btnSelectPhoto;
    private Button btnTakePhoto;
    private Button btnSave;
    private Button btnCancel;
    
    private Uri selectedImageUri;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        initializeUI();
        initializeViewModel();
    }

    private void initializeViewModel() {
        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initializeUI() {
        ivPreview = findViewById(R.id.ivPhotoPreview);
        etDescription = findViewById(R.id.etPhotoDescription);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSave = findViewById(R.id.btnSavePhoto);
        btnCancel = findViewById(R.id.btnCancelPhoto);

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoFromGallery();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void selectPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void takePhoto() {
        // Проверяем разрешение на камеру
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешение
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return;
        }
        
        // Если разрешение есть, открываем камеру
        openCamera();
    }
    
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "ru.mirea.zhemaytisvs.fitmotiv.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Ошибка при создании файла: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, открываем камеру
                openCamera();
            } else {
                Toast.makeText(this, "Для использования камеры необходимо предоставить разрешение", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        selectedImageUri = Uri.fromFile(image);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                selectedImageUri = data.getData();
                displayImage(selectedImageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                displayImage(selectedImageUri);
            }
        }
    }

    private void displayImage(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .centerCrop()
                    .into(ivPreview);
        }
    }

    private void savePhoto() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Выберите или сделайте фото", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = etDescription.getText().toString().trim();
        if (description.isEmpty()) {
            description = "Фото прогресса";
        }

        // Создаем ProgressPhoto
        String id = String.valueOf(System.currentTimeMillis());
        String imageUrl = selectedImageUri.toString();
        ProgressPhoto photo = new ProgressPhoto(id, imageUrl, description, new Date());

        // Сохраняем через ViewModel
        viewModel.addProgressPhoto(photo);
        
        Toast.makeText(this, "Фото добавлено!", Toast.LENGTH_SHORT).show();
        
        // Обновляем список фото после небольшой задержки
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        handler.postDelayed(() -> viewModel.loadProgressPhotos(), 500);
        
        finish();
    }
}

