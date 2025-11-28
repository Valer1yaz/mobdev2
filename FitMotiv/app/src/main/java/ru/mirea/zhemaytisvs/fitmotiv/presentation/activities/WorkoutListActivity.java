package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkoutListActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private RecyclerView rvWorkouts;
    private WorkoutAdapter workoutAdapter;
    private Button btnBack;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        Log.d("WorkoutListActivity", "=== ACTIVITY CREATED ===");

        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();

        // Принудительная загрузка данных
        loadWorkoutsImmediately();

        // Проверяем начальное состояние UI
        checkUIState();
        // Ждем загрузки пользователя перед загрузкой тренировок
        waitForUserAndLoadWorkouts();
    }

    private void initializeViewModel() {
        // ИСПРАВЛЕНО: Используем ViewModel от Activity, а не создаем новый
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Log.d("WorkoutListActivity", "ViewModel initialized");
    }

    private void initializeUI() {
        rvWorkouts = findViewById(R.id.rvWorkouts);
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        Log.d("WorkoutListActivity", "UI initialized - " +
                "rvWorkouts: " + (rvWorkouts != null) + ", " +
                "btnBack: " + (btnBack != null) + ", " +
                "tvEmpty: " + (tvEmpty != null));

        workoutAdapter = new WorkoutAdapter(new ArrayList<>(), new WorkoutAdapter.OnWorkoutClickListener() {
            @Override
            public void onWorkoutClick(Workout workout) {
                showWorkoutDetail(workout);
            }
        });

        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));
        rvWorkouts.setAdapter(workoutAdapter);
        Log.d("WorkoutListActivity", "RecyclerView and adapter setup completed");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadWorkoutsImmediately() {
        Log.d("WorkoutListActivity", "Loading workouts immediately");

        // Проверяем, есть ли уже данные в ViewModel
        List<Workout> currentWorkouts = viewModel.getWorkoutsLiveData().getValue();
        Log.d("WorkoutListActivity", "Current workouts in ViewModel: " + (currentWorkouts != null ? currentWorkouts.size() : "null"));

        if (currentWorkouts == null || currentWorkouts.isEmpty()) {
            Log.d("WorkoutListActivity", "No workouts in ViewModel, loading from database");
            viewModel.loadWorkouts();
        } else {
            Log.d("WorkoutListActivity", "Workouts already in ViewModel: " + currentWorkouts.size());
            // Обновляем адаптер с существующими данными
            workoutAdapter.updateData(currentWorkouts);
            hideEmptyState();
        }
    }

    private void waitForUserAndLoadWorkouts() {
        viewModel.getCurrentUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null && !user.isGuest()) {
                    Log.d("WorkoutListActivity", "User loaded, now loading workouts");
                    loadWorkouts();
                    // Убираем наблюдателя после первой успешной загрузки
                    viewModel.getCurrentUserLiveData().removeObserver(this);
                } else if (user != null && user.isGuest()) {
                    Log.d("WorkoutListActivity", "User is guest, showing empty state");
                    showEmptyState("Гостевой режим. Тренировки недоступны.");
                } else {
                    Log.d("WorkoutListActivity", "User is null, showing empty state");
                    showEmptyState("Пользователь не найден");
                }
            }
        });
    }

    private void setupLiveDataObservers() {
        viewModel.getWorkoutsLiveData().observe(this, new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workouts) {
                Log.d("WorkoutListActivity", "=== WORKOUTS LIVE DATA UPDATED ===");
                Log.d("WorkoutListActivity", "Received " + (workouts != null ? workouts.size() : 0) + " workouts");

                if (workouts != null && !workouts.isEmpty()) {
                    Log.d("WorkoutListActivity", "First workout: " + workouts.get(0).getType() + " - " + workouts.get(0).getDescription());
                } else {
                    Log.d("WorkoutListActivity", "Workouts list is empty or null");
                }

                if (workouts != null) {
                    workoutAdapter.updateData(workouts);
                    Log.d("WorkoutListActivity", "Adapter updated with " + workouts.size() + " items");

                    if (workouts.isEmpty()) {
                        Log.d("WorkoutListActivity", "Showing empty state");
                        showEmptyState("Тренировок пока нет");
                    } else {
                        Log.d("WorkoutListActivity", "Hiding empty state, showing RecyclerView");
                        hideEmptyState();
                    }
                } else {
                    Log.d("WorkoutListActivity", "Workouts list is null");
                    showEmptyState("Ошибка загрузки тренировок");
                }

                checkUIState();
            }
        });

        // ДОБАВЛЕНО: Наблюдатель для пользователя
        viewModel.getCurrentUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d("WorkoutListActivity", "User updated: " + (user != null ? user.getUid() : "null"));
                if (user != null && !user.isGuest()) {
                    // Если пользователь загружен, но тренировок нет - загружаем
                    List<Workout> currentWorkouts = viewModel.getWorkoutsLiveData().getValue();
                    if (currentWorkouts == null || currentWorkouts.isEmpty()) {
                        Log.d("WorkoutListActivity", "User loaded but no workouts, loading...");
                        viewModel.loadWorkouts();
                    }
                }
            }
        });
    }
    private void checkUIState() {
        Log.d("WorkoutListActivity", "=== UI STATE CHECK ===");
        Log.d("WorkoutListActivity", "tvEmpty: " + (tvEmpty != null ?
                "visible=" + (tvEmpty.getVisibility() == View.VISIBLE) : "null"));
        Log.d("WorkoutListActivity", "rvWorkouts: " + (rvWorkouts != null ?
                "visible=" + (rvWorkouts.getVisibility() == View.VISIBLE) : "null"));
        Log.d("WorkoutListActivity", "Adapter item count: " + workoutAdapter.getItemCount());
    }

    private void loadWorkouts() {
        Log.d("WorkoutListActivity", "Loading workouts...");
        viewModel.loadWorkouts();
    }

    private void showEmptyState(String message) {
        if (tvEmpty != null) {
            tvEmpty.setText(message);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        if (rvWorkouts != null) {
            rvWorkouts.setVisibility(View.GONE);
        }
    }

    private void hideEmptyState() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
        if (rvWorkouts != null) {
            rvWorkouts.setVisibility(View.VISIBLE);
        }
    }

    /**
     * метод: показывает детали тренировки в диалоге вместо отдельной Activity
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
        builder.setNegativeButton("Удалить", (dialog, which) -> {
            // Подтверждение удаления
            new AlertDialog.Builder(this)
                    .setTitle("Удаление тренировки")
                    .setMessage("Вы уверены, что хотите удалить эту тренировку?")
                    .setPositiveButton("Удалить", (d, w) -> {
                        viewModel.deleteWorkout(workout.getId());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
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