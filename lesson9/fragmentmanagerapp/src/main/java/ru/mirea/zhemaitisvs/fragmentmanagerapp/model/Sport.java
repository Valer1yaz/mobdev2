package ru.mirea.zhemaitisvs.fragmentmanagerapp.model;

public class Sport {
    private String name;
    private String country;
    private String description;

    public Sport(String name, String country, String description) {
        this.name = name;
        this.country = country;
        this.description = description;
    }

    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getDescription() { return description; }

    // метод для правильного отображения в ListView
    @Override
    public String toString() {
        return name;
    }
}
