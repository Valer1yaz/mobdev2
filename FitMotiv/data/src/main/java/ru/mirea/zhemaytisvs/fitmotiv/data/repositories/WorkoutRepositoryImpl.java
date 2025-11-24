package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.FitnessDatabase;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.entities.WorkoutEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRepositoryImpl implements WorkoutRepository {
    private final FitnessDatabase database;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public WorkoutRepositoryImpl(Context context) {
        this.database = FitnessDatabase.getDatabase(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        // Инициализация тестовых данных при первом запуске
        initializeTestDataIfNeeded();
    }

    private void initializeTestDataIfNeeded() {
        executorService.execute(() -> {
            List<WorkoutEntity> existingWorkouts = database.workoutDao().getAllWorkoutsSync();
            if (existingWorkouts.isEmpty()) {
                // Добавляем тестовые данные только если БД пуста
                List<WorkoutEntity> testWorkouts = new ArrayList<>();
                testWorkouts.add(new WorkoutEntity(
                        "1",
                        "CARDIO",
                        45,
                        350,
                        getDate(2024, Calendar.JANUARY, 15),
                        "Утренняя пробежка в парке"
                ));
                testWorkouts.add(new WorkoutEntity(
                        "2",
                        "STRENGTH",
                        60,
                        400,
                        getDate(2024, Calendar.JANUARY, 16),
                        "Тренировка с гантелями"
                ));
                database.workoutDao().insertWorkouts(testWorkouts);
            }
        });
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    @Override
    public void saveWorkout(Workout workout) {
        executorService.execute(() -> {
            WorkoutEntity entity = mapToEntity(workout);
            database.workoutDao().insertWorkout(entity);
        });
    }

    @Override
    public List<Workout> getWorkoutHistory() {
        // Синхронный вызов для совместимости с существующим кодом
        // В идеале нужно использовать LiveData, но это потребует изменения интерфейса
        try {
            List<WorkoutEntity> entities = database.workoutDao().getAllWorkoutsSync();
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
    public Workout getWorkoutById(String id) {
        try {
            WorkoutEntity entity = database.workoutDao().getWorkoutById(id);
            return entity != null ? mapToDomain(entity) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Workout> getWorkoutsByType(Workout.WorkoutType type) {
        List<Workout> allWorkouts = getWorkoutHistory();
        List<Workout> result = new ArrayList<>();
        for (Workout workout : allWorkouts) {
            if (workout.getType() == type) {
                result.add(workout);
            }
        }
        return result;
    }

    private WorkoutEntity mapToEntity(Workout workout) {
        return new WorkoutEntity(
                workout.getId(),
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
                Workout.WorkoutType.valueOf(entity.type),
                entity.duration,
                entity.calories,
                entity.date,
                entity.description
        );
    }
}