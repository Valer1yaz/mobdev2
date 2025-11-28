package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import java.util.List;

public interface WorkoutRepository {
    void saveWorkout(Workout workout, String userId);
    List<Workout> getWorkoutHistory(String userId);
    Workout getWorkoutById(String id, String userId);
    List<Workout> getWorkoutsByType(Workout.WorkoutType type, String userId);
    void deleteWorkout(String id, String userId);
}