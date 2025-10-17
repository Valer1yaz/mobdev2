package ru.mirea.zhemaytisvs.domain.models;

public class MovieDomain {
    private int id;
    private String name;
    public MovieDomain(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
}
