package ru.mirea.zhemaytisvs.fitmotiv.presentation.ml;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class FallbackImageClassifier {

    public List<ImageClassifierHelper.ExerciseClassification> classifyImage(Bitmap bitmap) {
        List<ImageClassifierHelper.ExerciseClassification> results = new ArrayList<>();

        // Заглушка для демонстрации, когда модель недоступна
        results.add(new ImageClassifierHelper.ExerciseClassification("Отжимания", 0.85f));
        results.add(new ImageClassifierHelper.ExerciseClassification("Приседания", 0.72f));
        results.add(new ImageClassifierHelper.ExerciseClassification("Планка", 0.65f));

        return results;
    }
}