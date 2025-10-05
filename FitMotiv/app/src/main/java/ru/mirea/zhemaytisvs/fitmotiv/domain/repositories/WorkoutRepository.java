package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import java.util.List;

public interface WorkoutRepository {
    void saveWorkout(Workout workout);
    List<Workout> getWorkoutHistory();
    Workout getWorkoutById(String id);
    List<Workout> getWorkoutsByType(Workout.WorkoutType type);
}