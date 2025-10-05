package ru.mirea.zhemaytisvs.fitmotiv.data.models;

import java.util.Date;

public class ProgressPhotoEntity {
    private String id;
    private String imageUrl;
    private String description;
    private Date date;
    private int likes;

    public ProgressPhotoEntity(String id, String imageUrl, String description, Date date, int likes) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.description = description;
        this.date = date;
        this.likes = likes;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public Date getDate() { return date; }
    public int getLikes() { return likes; }
}