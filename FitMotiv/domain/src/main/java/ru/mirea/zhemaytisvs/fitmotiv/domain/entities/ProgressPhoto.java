package ru.mirea.zhemaytisvs.fitmotiv.domain.entities;

import java.util.Date;

public class ProgressPhoto {
    private String id;
    private String userId; // ID пользователя, которому принадлежит фото
    private String imageUrl;
    private String description;
    private Date date;

    public ProgressPhoto(String id, String imageUrl, String description, Date date) {
        this.id = id;
        this.userId = null; // По умолчанию null, устанавливается при сохранении
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
    }

    public ProgressPhoto(String id, String userId, String imageUrl, String description, Date date) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}