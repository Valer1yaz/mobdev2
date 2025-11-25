package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.content.Context;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.FitnessDatabase;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.entities.WorkoutEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRepositoryImpl implements WorkoutRepository {
    private final FitnessDatabase database;
    private final ExecutorService executorService;

    public WorkoutRepositoryImpl(Context context) {
        this.database = FitnessDatabase.getDatabase(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void saveWorkout(Workout workout, String userId) {
        executorService.execute(() -> {
            // Устанавливаем userId перед сохранением
            workout.setUserId(userId);
            WorkoutEntity entity = mapToEntity(workout, userId);
            database.workoutDao().insertWorkout(entity);
        });
    }

    @Override
    public List<Workout> getWorkoutHistory(String userId) {
        // Синхронный вызов для совместимости с существующим кодом
        // Фильтруем по userId
        try {
            List<WorkoutEntity> entities = database.workoutDao().getWorkoutsByUserIdSync(userId);
            List<Workout> workouts = new ArrayList<>();
            for (WorkoutEntity entity : entities) {
                workouts.add(mapToDomain(entity));
            }
            return workouts;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Workout getWorkoutById(String id, String userId) {
        try {
            WorkoutEntity entity = database.workoutDao().getWorkoutById(id, userId);
            return entity != null ? mapToDomain(entity) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Workout> getWorkoutsByType(Workout.WorkoutType type, String userId) {
        List<Workout> allWorkouts = getWorkoutHistory(userId);
        List<Workout> result = new ArrayList<>();
        for (Workout workout : allWorkouts) {
            if (workout.getType() == type) {
                result.add(workout);
            }
        }
        return result;
    }

    private WorkoutEntity mapToEntity(Workout workout, String userId) {
        return new WorkoutEntity(
                workout.getId(),
                userId,
                workout.getType().name(),
                workout.getDuration(),
                workout.getCalories(),
                workout.getDate(),
                workout.getDescription()
        );
    }

    private Workout mapToDomain(WorkoutEntity entity) {
        return new Workout(
                entity.id,
                entity.userId,
                Workout.WorkoutType.valueOf(entity.type),
                entity.duration,
                entity.calories,
                entity.date,
                entity.description
        );
    }
}