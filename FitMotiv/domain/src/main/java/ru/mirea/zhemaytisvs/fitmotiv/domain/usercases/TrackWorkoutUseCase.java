package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;

public class TrackWorkoutUseCase {
    private final WorkoutRepository workoutRepository;

    public TrackWorkoutUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public void execute(Workout workout, String userId) {
        workoutRepository.saveWorkout(workout, userId);
    }
}