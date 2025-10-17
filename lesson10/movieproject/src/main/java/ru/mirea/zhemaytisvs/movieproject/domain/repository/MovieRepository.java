package ru.mirea.zhemaytisvs.movieproject.domain.repository;

import ru.mirea.zhemaytisvs.movieproject.domain.models.Movie;

public interface MovieRepository {
    public boolean saveMovie(Movie movie);
    public Movie getMovie();
}
