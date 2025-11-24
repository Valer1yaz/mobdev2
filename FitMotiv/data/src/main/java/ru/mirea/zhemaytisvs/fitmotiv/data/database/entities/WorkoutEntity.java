package ru.mirea.zhemaytisvs.fitmotiv.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.converters.DateConverter;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.converters.WorkoutTypeConverter;

@Entity(tableName = "workouts")
@TypeConverters({DateConverter.class, WorkoutTypeConverter.class})
public class WorkoutEntity {
    @PrimaryKey
    @NonNull
    public String id;
    
    public String type; // CARDIO, STRENGTH, YOGA, SWIMMING
    public int duration; // в минутах
    public int calories;
    public Date date;
    public String description;
    
    public WorkoutEntity() {}
    
    @Ignore
    public WorkoutEntity(String id, String type, int duration, int calories, Date date, String description) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.calories = calories;
        this.date = date;
        this.description = description;
    }
}
