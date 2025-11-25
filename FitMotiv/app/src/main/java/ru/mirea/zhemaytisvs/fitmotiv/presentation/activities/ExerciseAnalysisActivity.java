package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.FileNotFoundException;
import java.io.InputStream;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ExerciseAnalysis;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

public class ExerciseAnalysisActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private MainViewModel viewModel;
    private ImageView ivExerciseImage;
    private Spinner spinnerExerciseType;
    private Button btnSelectImage;
    private Button btnTakePhoto;
    private Button btnAnalyze;
    private Button btnBack;
    private TextView tvAnalysisResult;
    private ProgressBar progressBar;
    
    private Bitmap selectedBitmap;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_analysis);

        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initializeUI() {
        ivExerciseImage = findViewById(R.id.ivExerciseImage);
        spinnerExerciseType = findViewById(R.id.spinnerExerciseType);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnBack = findViewById(R.id.btnBack);
        tvAnalysisResult = findViewById(R.id.tvAnalysisResult);
        progressBar = findViewById(R.id.progressBar);

        // Настройка Spinner для типов упражнений
        String[] exercises = {"Приседания", "Отжимания", "Планка", "Бег", "Йога"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                exercises
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExerciseType.setAdapter(adapter);

        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
        btnTakePhoto.setOnClickListener(v -> takePhoto());
        btnAnalyze.setOnClickListener(v -> analyzeExercise());
        btnBack.setOnClickListener(v -> finish());

        // Изначально кнопка анализа неактивна
        btnAnalyze.setEnabled(false);
    }

    private void setupLiveDataObservers() {
        viewModel.getExerciseAnalysisLiveData().observe(this, new Observer<ExerciseAnalysis>() {
            @Override
            public void onChanged(ExerciseAnalysis analysis) {
                progressBar.setVisibility(View.GONE);
                btnAnalyze.setEnabled(true);
                
                if (analysis != null) {
                    displayAnalysisResult(analysis);
                } else {
                    tvAnalysisResult.setText("Ошибка при анализе упражнения");
                }
            }
        });
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    selectedBitmap = BitmapFactory.decodeStream(inputStream);
                    displaySelectedImage();
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedBitmap = (Bitmap) extras.get("data");
                    displaySelectedImage();
                }
            }
        }
    }

    private void displaySelectedImage() {
        if (selectedBitmap != null) {
            ivExerciseImage.setImageBitmap(selectedBitmap);
            btnAnalyze.setEnabled(true);
            tvAnalysisResult.setText("");
        }
    }

    private void analyzeExercise() {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Пожалуйста, выберите изображение", Toast.LENGTH_SHORT).show();
            return;
        }

        String exerciseName = spinnerExerciseType.getSelectedItem().toString();
        
        // Конвертируем Bitmap в byte[]
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageData = stream.toByteArray();

        // Показываем прогресс
        progressBar.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);
        tvAnalysisResult.setText("Анализ выполняется...");

        // Запускаем анализ через ViewModel
        viewModel.analyzeExercise(exerciseName, imageData);
    }

    private void displayAnalysisResult(ExerciseAnalysis analysis) {
        if (analysis != null) {
            String result = String.format(
                    "Упражнение: %s\n\n" +
                    "Оценка правильности: %.1f%%\n\n" +
                    "Рекомендации:\n%s",
                    analysis.getExerciseName(),
                    analysis.getCorrectnessScore(),
                    analysis.getFeedback()
            );
            tvAnalysisResult.setText(result);
        } else {
            tvAnalysisResult.setText("Не удалось проанализировать упражнение");
        }
    }
}

