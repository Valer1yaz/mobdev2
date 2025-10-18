package ru.mirea.zhemaytisvs.fitmotiv.domain.entities;

import java.util.Date;

public class Workout {
    private String id;
    private WorkoutType type;
    private int duration;
    private int calories;
    private Date date;
    private String description;

    public enum WorkoutType {
        CARDIO, STRENGTH, YOGA, SWIMMING
    }

    public Workout(String id, WorkoutType type, int duration, int calories, Date date, String description) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.calories = calories;
        this.date = date;
        this.description = description;
    }

    // Getters
    public String getId() { return id; }
    public WorkoutType getType() { return type; }
    public int getDuration() { return duration; }
    public int getCalories() { return calories; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }
}