package ru.mirea.zhemaitisvs.recyclerviewapp;

public class GarbageType {
    private String eventName;
    private String description;
    private String imageName;
    private int year;

    public GarbageType(String eventName, String description, String imageName, int year) {
        this.eventName = eventName;
        this.description = description;
        this.imageName = imageName;
        this.year = year;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return this.eventName + " (" + this.year + ")";
    }
}