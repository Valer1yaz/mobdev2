package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkoutRepositoryImpl implements WorkoutRepository {
    private List<Workout> testWorkouts;

    public WorkoutRepositoryImpl() {
        initializeTestData();
    }

    private void initializeTestData() {
        testWorkouts = new ArrayList<>(Arrays.asList(
                new Workout("1", Workout.WorkoutType.CARDIO, 45, 350,
                        getDate(2024, Calendar.JANUARY, 15), "Утренняя пробежка в парке"),
                new Workout("2", Workout.WorkoutType.STRENGTH, 60, 400,
                        getDate(2024, Calendar.JANUARY, 16), "Тренировка с гантелями"),
                new Workout("3", Workout.WorkoutType.YOGA, 30, 150,
                        getDate(2024, Calendar.JANUARY, 17), "Вечерняя йога для расслабления"),
                new Workout("4", Workout.WorkoutType.SWIMMING, 40, 300,
                        getDate(2024, Calendar.JANUARY, 18), "Плавание в бассейне")
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
    }

    @Override
    public List<Workout> getWorkoutHistory() {
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
}