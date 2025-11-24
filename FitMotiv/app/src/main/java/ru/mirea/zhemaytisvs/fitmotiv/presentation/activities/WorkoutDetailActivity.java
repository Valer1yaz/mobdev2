package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.WorkoutRepositoryImpl;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class WorkoutDetailActivity extends AppCompatActivity {
    private TextView tvType;
    private TextView tvDescription;
    private TextView tvDate;
    private TextView tvDuration;
    private TextView tvCalories;
    private Button btnBack;
    private WorkoutRepository workoutRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        workoutRepository = new WorkoutRepositoryImpl(this);
        initializeUI();
        loadWorkout();
    }

    private void initializeUI() {
        tvType = findViewById(R.id.tvDetailType);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvDate = findViewById(R.id.tvDetailDate);
        tvDuration = findViewById(R.id.tvDetailDuration);
        tvCalories = findViewById(R.id.tvDetailCalories);
        btnBack = findViewById(R.id.btnDetailBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadWorkout() {
        String workoutId = getIntent().getStringExtra("workout_id");
        if (workoutId != null) {
            Workout workout = workoutRepository.getWorkoutById(workoutId);
            if (workout != null) {
                displayWorkout(workout);
            }
        }
    }

    private void displayWorkout(Workout workout) {
        tvType.setText(getTypeString(workout.getType()));
        tvDescription.setText(workout.getDescription());
        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(workout.getDate()));
        tvDuration.setText(workout.getDuration() + " минут");
        tvCalories.setText(workout.getCalories() + " ккал");
    }

    private String getTypeString(Workout.WorkoutType type) {
        switch (type) {
            case CARDIO: return "Кардио тренировка";
            case STRENGTH: return "Силовая тренировка";
            case YOGA: return "Йога";
            case SWIMMING: return "Плавание";
            default: return "Тренировка";
        }
    }
}

