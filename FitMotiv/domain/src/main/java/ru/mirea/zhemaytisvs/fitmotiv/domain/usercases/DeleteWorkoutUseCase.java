package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;

public class DeleteWorkoutUseCase {
    private final WorkoutRepository workoutRepository;

    public DeleteWorkoutUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public void execute(String workoutId, String userId) {
        workoutRepository.deleteWorkout(workoutId, userId);
    }
}

