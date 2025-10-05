package ru.mirea.zhemaytisvs.fitmotiv.data.models;

import java.util.Date;

public class WorkoutEntity {
    private String id;
    private String type;
    private int duration;
    private int calories;
    private Date date;
    private String description;

    public WorkoutEntity(String id, String type, int duration, int calories, Date date, String description) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.calories = calories;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
    public int getCalories() { return calories; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }
}