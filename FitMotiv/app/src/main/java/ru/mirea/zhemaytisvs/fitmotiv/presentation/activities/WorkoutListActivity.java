package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

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
                openWorkoutDetail(workout);
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

    private void openWorkoutDetail(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("workout_id", workout.getId());
        startActivity(intent);
    }
}

