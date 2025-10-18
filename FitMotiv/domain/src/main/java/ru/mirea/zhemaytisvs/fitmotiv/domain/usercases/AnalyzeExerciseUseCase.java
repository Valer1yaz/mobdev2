package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ExerciseAnalysis;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;

public class AnalyzeExerciseUseCase {
    private final WorkoutRepository workoutRepository;

    public AnalyzeExerciseUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public ExerciseAnalysis execute(String exerciseName, byte[] imageData) {
        // В реальном приложении здесь будет вызов ML модели
        // Пока возвращаем тестовые данные
        return new ExerciseAnalysis(
                exerciseName,
                85.5,
                "Хорошая техника! Обратите внимание на положение спины.",
                true
        );
    }
}