package ru.mirea.zhemaytisvs.movieproject.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.zhemaytisvs.movieproject.R;
import ru.mirea.zhemaytisvs.movieproject.data.repository.MovieRepositoryImpl;
import ru.mirea.zhemaytisvs.movieproject.domain.repository.MovieRepository;
import ru.mirea.zhemaytisvs.movieproject.domain.usecases.GetFavoriteFilmUseCase;
import ru.mirea.zhemaytisvs.movieproject.domain.usecases.SaveMovieToFavoriteUseCase;
import ru.mirea.zhemaytisvs.movieproject.domain.models.Movie;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText text = findViewById(R.id.editTextMovie);
        TextView textView = findViewById(R.id.textViewMovie);
        MovieRepository movieRepository = new MovieRepositoryImpl(this);

        findViewById(R.id.buttonSaveMovie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean result = new
                        SaveMovieToFavoriteUseCase(movieRepository).execute(new Movie(2,
                        text.getText().toString()));
                textView.setText(String.format("Сохранено: %s", result));
            }
        });
        findViewById(R.id.buttonGetMovie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie moview = new GetFavoriteFilmUseCase(movieRepository).execute();
                textView.setText(String.format("Сохранено: %s", moview.getName()));
            }
        });
    }
}