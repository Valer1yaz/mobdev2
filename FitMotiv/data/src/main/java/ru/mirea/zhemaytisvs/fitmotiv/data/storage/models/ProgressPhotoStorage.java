package ru.mirea.zhemaytisvs.fitmotiv.data.storage.models;

import java.util.Date;

public class ProgressPhotoStorage {
    private String id;
    private String imageUrl;
    private String description;
    private Date date;
    private int likes;
    private Date savedAt;

    public ProgressPhotoStorage(String id, String imageUrl, String description, Date date, int likes, Date savedAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
        this.likes = likes;
        this.savedAt = savedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }
    public int getLikes() { return likes; }
    public Date getSavedAt() { return savedAt; }
}
