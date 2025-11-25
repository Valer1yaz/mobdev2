package ru.mirea.zhemaytisvs.fitmotiv.data.storage.models;

public class ProgressPhotoStorage {
    private String id;
    private String userId;
    private String imageUrl;
    private String description;
    private long date; // timestamp в миллисекундах
    private long savedAt;

    public ProgressPhotoStorage(String id, String userId, String imageUrl, String description, long date, long savedAt) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
        this.savedAt = savedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public long getDate() { return date; }
    public long getSavedAt() { return savedAt; }
}

