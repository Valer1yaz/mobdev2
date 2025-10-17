package ru.mirea.zhemaytisvs.domain.repository;

import ru.mirea.zhemaytisvs.domain.models.MovieDomain;

public interface MovieRepository {
    public boolean saveMovie(MovieDomain movie);
    public MovieDomain getMovie();
}