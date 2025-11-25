package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Упрощенная Activity: объединяет список тренировок и детали в одном экране
 */
public class WorkoutListActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private RecyclerView rvWorkouts;
    private WorkoutAdapter workoutAdapter;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();
        loadWorkouts();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initializeUI() {
        rvWorkouts = findViewById(R.id.rvWorkouts);
        btnBack = findViewById(R.id.btnBack);

        workoutAdapter = new WorkoutAdapter(new ArrayList<>(), new WorkoutAdapter.OnWorkoutClickListener() {
            @Override
            public void onWorkoutClick(Workout workout) {
                showWorkoutDetail(workout);
            }
        });

        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));
        rvWorkouts.setAdapter(workoutAdapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupLiveDataObservers() {
        viewModel.getWorkoutsLiveData().observe(this, new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workouts) {
                if (workouts != null) {
                    workoutAdapter.updateData(workouts);
                }
            }
        });
    }

    private void loadWorkouts() {
        viewModel.loadWorkouts();
    }

    /**
     * Упрощенный метод: показывает детали тренировки в диалоге вместо отдельной Activity
     */
    private void showWorkoutDetail(Workout workout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Детали тренировки");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_workout_detail, null);
        
        TextView tvType = dialogView.findViewById(R.id.tvDialogType);
        TextView tvDescription = dialogView.findViewById(R.id.tvDialogDescription);
        TextView tvDate = dialogView.findViewById(R.id.tvDialogDate);
        TextView tvDuration = dialogView.findViewById(R.id.tvDialogDuration);
        TextView tvCalories = dialogView.findViewById(R.id.tvDialogCalories);
        
        tvType.setText(getTypeString(workout.getType()));
        tvDescription.setText(workout.getDescription());
        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(workout.getDate()));
        tvDuration.setText(workout.getDuration() + " минут");
        tvCalories.setText(workout.getCalories() + " ккал");
        
        builder.setView(dialogView);
        builder.setPositiveButton("Закрыть", null);
        builder.show();
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

