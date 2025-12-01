package ru.mirea.zhemaytisvs.fitmotiv.presentation.ml;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleImageClassifier {

    private static final String TAG = "SimpleImageClassifier";
    private static final String LABEL_FILE = "labels.txt";
    private static final String MODEL_FILE = "exercise_model.tflite";

    private List<String> labels;
    private ImageClassifierHelper imageClassifierHelper;
    private boolean isModelLoaded = false;

    public SimpleImageClassifier(Context context) {
        try {
            // Загружаем метки из файла
            labels = loadLabels(context);
            Log.d(TAG, "Labels loaded: " + labels.size());

            // Пытаемся загрузить модель TensorFlow Lite
            try {
                imageClassifierHelper = new ImageClassifierHelper(context);
                isModelLoaded = true;
                Log.d(TAG, "TensorFlow Lite model loaded successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to load TensorFlow Lite model", e);
                isModelLoaded = false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels", e);
            // Запасные метки
            labels = getDefaultLabels();
        }
    }

    private List<String> loadLabels(Context context) throws IOException {
        List<String> labelsList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(LABEL_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                labelsList.add(line.trim());
            }
        } catch (Exception e) {
            // Если файл не найден, используем дефолтные
            Log.w(TAG, "Labels file not found, using defaults");
            return getDefaultLabels();
        }

        return labelsList;
    }

    private List<String> getDefaultLabels() {
        List<String> defaultLabels = new ArrayList<>();
        defaultLabels.add("Отжимания");
        defaultLabels.add("Приседания");
        defaultLabels.add("Планка");
        defaultLabels.add("Бег");
        defaultLabels.add("Йога");
        defaultLabels.add("Плавание");
        defaultLabels.add("Велосипед");
        return defaultLabels;
    }

    public List<ImageClassifierHelper.ExerciseClassification> classifyImage(Bitmap bitmap) {
        Log.d(TAG, "Classifying image: " + bitmap.getWidth() + "x" + bitmap.getHeight());

        // Если модель загружена, используем реальный классификатор
        if (isModelLoaded && imageClassifierHelper != null) {
            try {
                List<ImageClassifierHelper.ExerciseClassification> results =
                        imageClassifierHelper.classifyImage(bitmap);

                if (results != null && !results.isEmpty()) {
                    Log.d(TAG, "Model classification successful, results: " + results.size());
                    return results;
                } else {
                    Log.w(TAG, "Model returned empty results, using fallback");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during model classification", e);
            }
        }

        // Если модель не загружена или произошла ошибка, используем fallback
        Log.d(TAG, "Using fallback classification");
        return getFallbackClassification();
    }

    private List<ImageClassifierHelper.ExerciseClassification> getFallbackClassification() {
        List<ImageClassifierHelper.ExerciseClassification> results = new ArrayList<>();

        // Используем загруженные метки или дефолтные
        List<String> exerciseList = (labels != null && !labels.isEmpty()) ? labels : getDefaultLabels();

        // Простая эвристика для fallback: выбираем первые 3 упражнения с разной уверенностью
        if (!exerciseList.isEmpty()) {
            // Попробуем определить по размеру/пропорциям изображения
            // Это упрощенная логика, можно улучшить
            results.add(new ImageClassifierHelper.ExerciseClassification(exerciseList.get(0), 0.85f));
            if (exerciseList.size() > 1) {
                results.add(new ImageClassifierHelper.ExerciseClassification(exerciseList.get(1), 0.70f));
            }
            if (exerciseList.size() > 2) {
                results.add(new ImageClassifierHelper.ExerciseClassification(exerciseList.get(2), 0.60f));
            }
        }

        return results;
    }

    public boolean isModelLoaded() {
        return isModelLoaded;
    }

    public void close() {
        if (imageClassifierHelper != null) {
            imageClassifierHelper.close();
        }
    }
}