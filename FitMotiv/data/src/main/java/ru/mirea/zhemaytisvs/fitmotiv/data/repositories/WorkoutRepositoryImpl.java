package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.content.Context;
import android.util.Log;

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
            try {
                Log.d("WorkoutRepository", "=== SAVE WORKOUT START ===");
                Log.d("WorkoutRepository", "Workout: " + workout.getId() + ", Type: " + workout.getType());
                Log.d("WorkoutRepository", "User ID: " + userId);
                Log.d("WorkoutRepository", "Workout user ID before: " + workout.getUserId());

                // Устанавливаем userId
                workout.setUserId(userId);
                Log.d("WorkoutRepository", "Workout user ID after: " + workout.getUserId());

                WorkoutEntity entity = mapToEntity(workout, userId);
                Log.d("WorkoutRepository", "Entity: " + entity.id + ", User: " + entity.userId);

                database.workoutDao().insertWorkout(entity);
                Log.d("WorkoutRepository", "Workout saved to database");

                // ПРОВЕРКА: сразу прочитаем обратно
                List<WorkoutEntity> allWorkouts = database.workoutDao().getAllWorkoutsSync();
                Log.d("WorkoutRepository", "Total workouts in DB after save: " + allWorkouts.size());
                for (WorkoutEntity e : allWorkouts) {
                    Log.d("WorkoutRepository", "DB Entry - ID: " + e.id + ", User: " + e.userId + ", Type: " + e.type);
                }

                Log.d("WorkoutRepository", "=== SAVE WORKOUT END ===");

            } catch (Exception e) {
                Log.e("WorkoutRepository", "Error saving workout", e);
            }
        });
    }

    @Override
    public List<Workout> getWorkoutHistory(String userId) {
        Log.d("WorkoutRepository", "=== GET WORKOUT HISTORY START ===");
        Log.d("WorkoutRepository", "Querying for user ID: " + userId);

        try {
            // ПРОВЕРКА 1: сколько всего записей в базе
            List<WorkoutEntity> allEntities = database.workoutDao().getAllWorkoutsSync();
            Log.d("WorkoutRepository", "Total workouts in DB: " + allEntities.size());
            for (WorkoutEntity e : allEntities) {
                Log.d("WorkoutRepository", "All DB - ID: " + e.id + ", User: " + e.userId + ", Type: " + e.type);
            }

            // ПРОВЕРКА 2: запрос по конкретному пользователю
            List<WorkoutEntity> entities = database.workoutDao().getWorkoutsByUserIdSync(userId);
            Log.d("WorkoutRepository", "Found " + entities.size() + " entities for user " + userId);

            List<Workout> workouts = new ArrayList<>();
            for (WorkoutEntity entity : entities) {
                Log.d("WorkoutRepository", "Processing entity: " + entity.id + ", User: " + entity.userId);
                Workout workout = mapToDomain(entity);
                workouts.add(workout);
                Log.d("WorkoutRepository", "Mapped to workout: " + workout.getId() + ", User: " + workout.getUserId());
            }

            Log.d("WorkoutRepository", "Returning " + workouts.size() + " workouts");
            Log.d("WorkoutRepository", "=== GET WORKOUT HISTORY END ===");
            return workouts;

        } catch (Exception e) {
            Log.e("WorkoutRepository", "Error getting workout history", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Workout getWorkoutById(String id, String userId) {
        try {
            WorkoutEntity entity = database.workoutDao().getWorkoutById(id, userId);
            return entity != null ? mapToDomain(entity) : null;
        } catch (Exception e) {
            Log.e("WorkoutRepository", "Error getting workout by id", e);
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

    @Override
    public void deleteWorkout(String id, String userId) {
        executorService.execute(() -> {
            try {
                database.workoutDao().deleteWorkout(id, userId);
                Log.d("WorkoutRepository", "Workout deleted: " + id);
            } catch (Exception e) {
                Log.e("WorkoutRepository", "Error deleting workout", e);
            }
        });
    }

    private WorkoutEntity mapToEntity(Workout workout, String userId) {
        Log.d("WorkoutRepository", "Mapping workout to entity - Workout user: " + workout.getUserId() + ", Param user: " + userId);
        return new WorkoutEntity(
                workout.getId(),
                userId, // Используем переданный userId
                workout.getType().name(),
                workout.getDuration(),
                workout.getCalories(),
                workout.getDate(),
                workout.getDescription()
        );
    }

    private Workout mapToDomain(WorkoutEntity entity) {
        Log.d("WorkoutRepository", "Mapping entity to domain - Entity user: " + entity.userId);
        Workout workout = new Workout(
                entity.id,
                Workout.WorkoutType.valueOf(entity.type),
                entity.duration,
                entity.calories,
                entity.date,
                entity.description
        );
        workout.setUserId(entity.userId);
        Log.d("WorkoutRepository", "Domain workout user after mapping: " + workout.getUserId());
        return workout;
    }
}