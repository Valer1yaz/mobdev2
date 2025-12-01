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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.ml.ImageClassifierHelper;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.ml.SimpleImageClassifier;

public class ExerciseAnalysisFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageClassifierHelper imageClassifier;
    private SimpleImageClassifier simpleClassifier;
    private ExecutorService executorService;

    private ImageView ivExercise;
    private TextView tvResult;
    private Button btnSelectImage, btnAnalyze, btnTakePhoto, btnBack;
    private ProgressBar progressBar;

    private Bitmap selectedBitmap;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeClassifier();
        initializeUI(view);
        setupEventListeners();
    }

    private void initializeClassifier() {
        try {
            // Пробуем использовать реальный классификатор
            imageClassifier = new ImageClassifierHelper(requireContext());
            Log.d("ExerciseAnalysis", "TensorFlow Lite инициализирован");
        } catch (Exception e) {
            Log.e("ExerciseAnalysis", "TensorFlow Lite ошибка, используем упрощенный классификатор", e);
            simpleClassifier = new SimpleImageClassifier(requireContext());
            if (tvResult != null) {
                tvResult.setText("Используется демонстрационный режим анализа");
            }
        }
        executorService = Executors.newSingleThreadExecutor();
    }

    private void initializeUI(View view) {
        ivExercise = view.findViewById(R.id.ivExercise);
        tvResult = view.findViewById(R.id.tvResult);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnAnalyze = view.findViewById(R.id.btnAnalyze);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnBack = view.findViewById(R.id.btnBack);
        progressBar = view.findViewById(R.id.progressBar);

        // Если нет кнопки для фото, скрываем её
        if (btnTakePhoto != null) {
            // Проверяем, есть ли камера на устройстве
            try {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    btnTakePhoto.setVisibility(View.VISIBLE);
                    btnTakePhoto.setOnClickListener(v -> takePhoto());
                } else {
                    btnTakePhoto.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                btnTakePhoto.setVisibility(View.GONE);
            }
        }

        // Кнопка назад
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        // Устанавливаем placeholder изображение
        Glide.with(this)
                .load(R.drawable.ic_placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivExercise);
    }

    private void setupEventListeners() {
        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
        btnAnalyze.setOnClickListener(v -> analyzeExercise());
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 2);
        } else {
            Toast.makeText(requireContext(), "Камера не найдена", Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeExercise() {
        if (selectedBitmap == null) {
            tvResult.setText("Сначала выберите изображение");
            return;
        }

        showLoading(true);

        executorService.execute(() -> {
            try {
                List<?> results;

                if (imageClassifier != null) {
                    results = imageClassifier.classifyImage(selectedBitmap);
                } else if (simpleClassifier != null) {
                    results = simpleClassifier.classifyImage(selectedBitmap);
                } else {
                    results = java.util.Collections.emptyList();
                }

                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    displayResults((List<ImageClassifierHelper.ExerciseClassification>) results);
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    tvResult.setText("Ошибка анализа: " + e.getMessage());
                    Log.e("ExerciseAnalysis", "Анализ не удался", e);
                });
            }
        });
    }

    private void displayResults(List<ImageClassifierHelper.ExerciseClassification> results) {
        if (results == null || results.isEmpty()) {
            tvResult.setText("Не удалось определить упражнение\nПопробуйте другое изображение");
            return;
        }

        StringBuilder resultText = new StringBuilder();
        resultText.append("🎯 Результаты анализа:\n\n");

        for (int i = 0; i < Math.min(3, results.size()); i++) {
            ImageClassifierHelper.ExerciseClassification classification = results.get(i);
            String label = classification.getLabel();
            float confidence = classification.getConfidence();

            // Форматируем вывод
            String formattedLabel = formatExerciseLabel(label);
            String percentage = String.format("%.1f%%", confidence * 100);

            resultText.append((i + 1)).append(". ").append(formattedLabel)
                    .append(" - ").append(percentage).append("\n");

            // Добавляем рекомендации для наиболее вероятного результата
            if (i == 0) {
                resultText.append("\n💡 Рекомендация: ")
                        .append(getExerciseAdvice(label))
                        .append("\n");
            }
        }

        tvResult.setText(resultText.toString());
    }

    private String formatExerciseLabel(String label) {
        if (label == null) return "Неизвестно";

        String lowerLabel = label.toLowerCase();
        switch (lowerLabel) {
            case "pushup":
            case "push_up":
                return "Отжимания";
            case "squat":
                return "Приседания";
            case "plank":
                return "Планка";
            case "running":
                return "Бег";
            case "yoga":
                return "Йога";
            case "swimming":
                return "Плавание";
            case "cycling":
                return "Велосипед";
            default:
                return label;
        }
    }

    private String getExerciseAdvice(String label) {
        if (label == null) return "Выполняйте упражнение с правильной техникой";

        String lowerLabel = label.toLowerCase();
        switch (lowerLabel) {
            case "pushup":
            case "push_up":
                return "Следите за прямой спиной и полным разгибанием рук";
            case "squat":
                return "Колени не должны выходить за носки, спина прямая";
            case "plank":
                return "Держите тело прямым, не прогибайте поясницу";
            case "running":
                return "Контролируйте дыхание и темп";
            case "yoga":
                return "Сосредоточьтесь на дыхании и балансе";
            case "swimming":
                return "Следите за техникой гребка и дыханием";
            case "cycling":
                return "Регулируйте сопротивление и следите за осанкой";
            default:
                return "Выполняйте упражнение с правильной техникой";
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnAnalyze.setEnabled(!isLoading);
        btnSelectImage.setEnabled(!isLoading);

        if (btnTakePhoto != null) {
            btnTakePhoto.setEnabled(!isLoading);
        }

        if (isLoading) {
            tvResult.setText("Анализ изображения...");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {

            if (requestCode == PICK_IMAGE_REQUEST) {
                // Загрузка изображения из галереи
                selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    try {
                        // Загружаем изображение
                        InputStream inputStream = requireContext().getContentResolver()
                                .openInputStream(selectedImageUri);

                        // Оптимизируем для анализа
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2; // Уменьшаем размер

                        selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);

                        if (inputStream != null) {
                            inputStream.close();
                        }

                        if (selectedBitmap != null) {
                            // Отображаем изображение
                            Glide.with(this)
                                    .load(selectedBitmap)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(ivExercise);

                            tvResult.setText("Изображение загружено. Нажмите 'Анализировать'");
                        }

                    } catch (IOException e) {
                        tvResult.setText("Ошибка загрузки изображения");
                    }
                }

            } else if (requestCode == 2) { // Фото с камеры
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedBitmap = (Bitmap) extras.get("data");

                    if (selectedBitmap != null) {
                        ivExercise.setImageBitmap(selectedBitmap);
                        tvResult.setText("Фото сделано. Нажмите 'Анализировать'");
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageClassifier != null) {
            imageClassifier.close();
        }
        if (executorService != null) {
            executorService.shutdown();
        }

        // Освобождаем память от bitmap
        if (selectedBitmap != null && !selectedBitmap.isRecycled()) {
            selectedBitmap.recycle();
            selectedBitmap = null;
        }
    }
}