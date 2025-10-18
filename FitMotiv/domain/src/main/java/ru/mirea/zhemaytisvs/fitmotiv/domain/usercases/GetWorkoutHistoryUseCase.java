package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;

public class GetWorkoutHistoryUseCase {
    private final WorkoutRepository workoutRepository;

    public GetWorkoutHistoryUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public List<Workout> execute() {
        return workoutRepository.getWorkoutHistory();
    }
}