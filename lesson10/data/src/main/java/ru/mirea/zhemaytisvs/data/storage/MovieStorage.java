package ru.mirea.zhemaytisvs.data.storage;

import ru.mirea.zhemaytisvs.data.storage.models.Movie;

public interface MovieStorage {
    public Movie get();
    public boolean save(Movie movie);
}
