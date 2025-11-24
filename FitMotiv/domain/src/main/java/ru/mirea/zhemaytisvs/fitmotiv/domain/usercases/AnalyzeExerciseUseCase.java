package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ExerciseAnalysis;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;

import java.util.Random;

public class AnalyzeExerciseUseCase {
    private final WorkoutRepository workoutRepository;
    private final Random random = new Random();

    public AnalyzeExerciseUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public ExerciseAnalysis execute(String exerciseName, byte[] imageData) {
        // Имитация анализа изображения с помощью ML модели
        // В реальном приложении здесь будет:
        // 1. Предобработка изображения (resize, normalize)
        // 2. Загрузка обученной модели (TensorFlow Lite, ML Kit)
        // 3. Выполнение инференса
        // 4. Постобработка результатов
        
        // Имитация обработки изображения
        if (imageData == null || imageData.length == 0) {
            return new ExerciseAnalysis(
                    exerciseName,
                    0.0,
                    "Ошибка: изображение не загружено",
                    false
            );
        }
        
        // Имитация анализа с учетом типа упражнения
        double baseScore = getBaseScoreForExercise(exerciseName);
        double variation = (random.nextDouble() - 0.5) * 20; // ±10%
        double finalScore = Math.max(0, Math.min(100, baseScore + variation));
        
        String feedback = generateFeedback(exerciseName, finalScore);
        boolean isCorrect = finalScore >= 70;
        
        return new ExerciseAnalysis(
                exerciseName,
                finalScore,
                feedback,
                isCorrect
        );
    }
    
    private double getBaseScoreForExercise(String exerciseName) {
        // Разные базовые оценки для разных упражнений
        String name = exerciseName.toLowerCase();
        if (name.contains("присед")) return 80.0;
        if (name.contains("отжимание")) return 75.0;
        if (name.contains("планка")) return 85.0;
        if (name.contains("подтягивание")) return 70.0;
        return 75.0; // По умолчанию
    }
    
    private String generateFeedback(String exerciseName, double score) {
        if (score >= 90) {
            return "Отличная техника! Выполнение упражнения идеально.";
        } else if (score >= 80) {
            return "Хорошая техника! Небольшие замечания по положению тела.";
        } else if (score >= 70) {
            return "Приемлемая техника. Обратите внимание на правильность выполнения.";
        } else if (score >= 60) {
            return "Техника требует улучшения. Рекомендуется консультация с тренером.";
        } else {
            return "Техника неверна. Остановитесь и изучите правильное выполнение упражнения.";
        }
    }
}