package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import java.util.List;

public class GetWorkoutHistoryUseCase {
    private final WorkoutRepository workoutRepository;

    public GetWorkoutHistoryUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public List<Workout> execute() {
        return workoutRepository.getWorkoutHistory();
    }
}