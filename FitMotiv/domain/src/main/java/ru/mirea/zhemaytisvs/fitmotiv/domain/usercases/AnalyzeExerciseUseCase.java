package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ExerciseAnalysis;

/**
 * Упрощенный UseCase для анализа упражнений
 * TensorFlow Lite логика вынесена в presentation слой для упрощения архитектуры
 */
public class AnalyzeExerciseUseCase {
    public AnalyzeExerciseUseCase() {
        // Упрощенная версия без зависимости от репозитория
    }

    /**
     * Анализирует упражнение (упрощенная версия без TensorFlow Lite в domain слое)
     * @param exerciseName название упражнения
     * @param imageData данные изображения
     * @param mlScore оценка от ML модели (0.0-1.0), передается из presentation слоя
     * @return результат анализа
     */
    public ExerciseAnalysis execute(String exerciseName, byte[] imageData, float mlScore) {
        // Проверка входных данных
        if (imageData == null || imageData.length == 0) {
            return new ExerciseAnalysis(
                    exerciseName,
                    0.0,
                    "Ошибка: изображение не загружено",
                    false
            );
        }
        
        // Используем оценку от ML модели или fallback
        float score = mlScore > 0 ? mlScore : (float) getBaseScoreForExercise(exerciseName) / 100.0f;
        
        // Преобразуем оценку в проценты (0-100)
        double finalScore = score * 100.0;
        
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