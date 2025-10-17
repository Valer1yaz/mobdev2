package ru.mirea.zhemaytisvs.domain.usecases;

import ru.mirea.zhemaytisvs.domain.models.MovieDomain;
import ru.mirea.zhemaytisvs.domain.repository.MovieRepository;

public class GetFavoriteFilmUseCase {
    private MovieRepository movieRepository;

    public GetFavoriteFilmUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieDomain execute() {
        return movieRepository.getMovie();
    }
}