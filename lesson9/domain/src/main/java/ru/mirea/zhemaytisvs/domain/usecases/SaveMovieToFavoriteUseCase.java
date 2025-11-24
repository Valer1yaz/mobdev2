package ru.mirea.zhemaytisvs.domain.usecases;

import ru.mirea.zhemaytisvs.domain.models.MovieDomain;
import ru.mirea.zhemaytisvs.domain.repository.MovieRepository;

public class SaveMovieToFavoriteUseCase {
    private MovieRepository movieRepository;
    public SaveMovieToFavoriteUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    public boolean execute(MovieDomain movie){
        return movieRepository.saveMovie(movie);
    }
}