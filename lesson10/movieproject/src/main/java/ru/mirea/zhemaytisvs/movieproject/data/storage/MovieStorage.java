package ru.mirea.zhemaytisvs.movieproject.data.storage;

public interface MovieStorage {
    public Movie get();
    public boolean save(Movie movie);
}
