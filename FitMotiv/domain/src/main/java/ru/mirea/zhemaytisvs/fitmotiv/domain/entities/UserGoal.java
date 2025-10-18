package ru.mirea.zhemaytisvs.fitmotiv.domain.entities;

public class UserGoal {
    private String id;
    private int targetWeight;
    private int workoutsPerWeek;
    private String description;
    private boolean completed;

    public UserGoal(String id, int targetWeight, int workoutsPerWeek, String description, boolean completed) {
        this.id = id;
        this.targetWeight = targetWeight;
        this.workoutsPerWeek = workoutsPerWeek;
        this.description = description;
        this.completed = completed;
    }

    // Getters and Setters
    public String getId() { return id; }
    public int getTargetWeight() { return targetWeight; }
    public int getWorkoutsPerWeek() { return workoutsPerWeek; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
}