package ru.mirea.zhemaytisvs.fitmotiv.data.storage.models;

public class UserGoalStorage {
    private String id;
    private int targetWeight;
    private int workoutsPerWeek;
    private String description;
    private boolean completed;
    private long savedAt;

    public UserGoalStorage(String id, int targetWeight, int workoutsPerWeek, String description, boolean completed, long savedAt) {
        this.id = id;
        this.targetWeight = targetWeight;
        this.workoutsPerWeek = workoutsPerWeek;
        this.description = description;
        this.completed = completed;
        this.savedAt = savedAt;
    }

    // Getters
    public String getId() { return id; }
    public int getTargetWeight() { return targetWeight; }
    public int getWorkoutsPerWeek() { return workoutsPerWeek; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public long getSavedAt() { return savedAt; }
}
