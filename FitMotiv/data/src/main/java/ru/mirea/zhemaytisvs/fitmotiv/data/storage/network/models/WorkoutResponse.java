package ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models;

import java.util.Date;

public class WorkoutResponse {
    private String id;
    private String type;
    private int duration;
    private int calories;
    private Date date;
    private String description;

    public WorkoutResponse(String id, String type, int duration, int calories, Date date, String description) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.calories = calories;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
