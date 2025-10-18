package ru.mirea.zhemaytisvs.fitmotiv.data.storage.models;

import java.util.Date;

public class WorkoutStorage {
    private String id;
    private String type;
    private int duration;
    private int calories;
    private Date date;
    private String description;
    private Date savedAt;

    public WorkoutStorage(String id, String type, int duration, int calories,
                          Date date, String description, Date savedAt) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.calories = calories;
        this.date = date;
        this.description = description;
        this.savedAt = savedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
    public int getCalories() { return calories; }
    public Date getDate() { return date; }
    public String getDescription() { return description; }
    public Date getSavedAt() { return savedAt; }
}