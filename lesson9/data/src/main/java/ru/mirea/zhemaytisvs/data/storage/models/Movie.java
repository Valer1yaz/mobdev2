package ru.mirea.zhemaytisvs.data.storage.models;

public class Movie {
    private int id;
    private String name;
    private String localDate;
    public Movie(int id, String name, String localDate) {
        this.id = id;
        this.name = name;
        this.localDate = localDate;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public String getLocalDate() {
        return localDate;
    }
}
