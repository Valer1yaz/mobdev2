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
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimpleImageClassifier {

    private static final String TAG = "SimpleImageClassifier";
    private static final String LABEL_FILE = "labels.txt";

    private List<String> labels;
    private Random random = new Random();

    public SimpleImageClassifier(Context context) {
        try {
            // Загружаем метки из файла
            labels = loadLabels(context);
            Log.d(TAG, "Labels loaded: " + labels.size());
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

        List<ImageClassifierHelper.ExerciseClassification> results = new ArrayList<>();

        // Имитация работы ML модели для демонстрации
        // Используем загруженные метки или дефолтные
        List<String> exerciseList = (labels != null && !labels.isEmpty()) ? labels : getDefaultLabels();

        // Генерируем случайные результаты для демонстрации
        for (int i = 0; i < Math.min(3, exerciseList.size()); i++) {
            float confidence = 0.7f + random.nextFloat() * 0.3f; // 0.7-1.0
            results.add(new ImageClassifierHelper.ExerciseClassification(exerciseList.get(i), confidence));
        }

        // Сортировка по убыванию confidence
        results.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));

        return results;
    }
}