package ru.mirea.zhemaytisvs.fitmotiv.data.database.converters;

import androidx.room.TypeConverter;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;

public class WorkoutTypeConverter {
    @TypeConverter
    public static String fromWorkoutType(Workout.WorkoutType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static Workout.WorkoutType toWorkoutType(String type) {
        return type == null ? null : Workout.WorkoutType.valueOf(type);
    }
}

