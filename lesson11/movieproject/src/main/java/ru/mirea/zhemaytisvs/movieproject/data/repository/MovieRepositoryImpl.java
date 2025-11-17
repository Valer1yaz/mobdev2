package ru.mirea.zhemaytisvs.movieproject.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import ru.mirea.zhemaytisvs.movieproject.domain.models.Movie;
import ru.mirea.zhemaytisvs.movieproject.domain.repository.MovieRepository;

public class MovieRepositoryImpl implements MovieRepository {

    private static final String PREFERENCES_NAME = "movie_prefs";
    private static final String KEY_MOVIE_NAME = "favorite_movie_name";

    private final SharedPreferences sharedPreferences;

    public MovieRepositoryImpl(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean saveMovie(Movie movie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MOVIE_NAME, movie.getName());
        return editor.commit();
    }

    @Override
    public Movie getMovie() {
        String movieName = sharedPreferences.getString(KEY_MOVIE_NAME, "Вы не сохраняли фильм");
        return new Movie(1, movieName);
    }
}
