package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.WorkoutStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkoutRepositoryImpl implements WorkoutRepository {
    private List<Workout> testWorkouts;
    private final SharedPrefStorage sharedPrefStorage;

    public WorkoutRepositoryImpl(SharedPrefStorage sharedPrefStorage) {
        this.sharedPrefStorage = sharedPrefStorage;
        initializeTestData();
    }

    private void initializeTestData() {
        testWorkouts = new ArrayList<>(Arrays.asList(
                new Workout("1", Workout.WorkoutType.CARDIO, 45, 350,
                        getDate(2024, Calendar.JANUARY, 15), "Утренняя пробежка в парке"),
                new Workout("2", Workout.WorkoutType.STRENGTH, 60, 400,
                        getDate(2024, Calendar.JANUARY, 16), "Тренировка с гантелями")
        ));
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    @Override
    public void saveWorkout(Workout workout) {
        testWorkouts.add(workout);
        WorkoutStorage workoutStorage = mapToStorage(workout);
        sharedPrefStorage.saveWorkout(workoutStorage);
    }

    @Override
    public List<Workout> getWorkoutHistory() {
        WorkoutStorage storedWorkout = sharedPrefStorage.getWorkout();
        if (storedWorkout != null) {
            testWorkouts.add(mapToDomain(storedWorkout));
        }
        return new ArrayList<>(testWorkouts);
    }

    @Override
    public Workout getWorkoutById(String id) {
        for (Workout workout : testWorkouts) {
            if (workout.getId().equals(id)) {
                return workout;
            }
        }
        return null;
    }

    @Override
    public List<Workout> getWorkoutsByType(Workout.WorkoutType type) {
        List<Workout> result = new ArrayList<>();
        for (Workout workout : testWorkouts) {
            if (workout.getType() == type) {
                result.add(workout);
            }
        }
        return result;
    }

    private WorkoutStorage mapToStorage(Workout workout) {
        return new WorkoutStorage(
                workout.getId(),
                workout.getType().name(),
                workout.getDuration(),
                workout.getCalories(),
                workout.getDate(),
                workout.getDescription(),
                new Date()
        );
    }

    private Workout mapToDomain(WorkoutStorage storage) {
        return new Workout(
                storage.getId(),
                Workout.WorkoutType.valueOf(storage.getType()),
                storage.getDuration(),
                storage.getCalories(),
                storage.getDate(),
                storage.getDescription()
        );
    }
}