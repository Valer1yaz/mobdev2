package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
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
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        Log.d("WorkoutListActivity", "=== ACTIVITY CREATED ===");

        // Создаем пользователя из Intent
        initializeUserFromIntent();
        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();

        // Загружаем тренировки
        loadWorkouts();
    }
    private void initializeUserFromIntent() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra("user_id");
        String email = intent.getStringExtra("user_email");
        String displayName = intent.getStringExtra("user_display_name");
        boolean isGuest = intent.getBooleanExtra("is_guest", true);

        if (userId != null) {
            currentUser = new User(userId, email, displayName, isGuest);
            Log.d("WorkoutListActivity", "User created from Intent: " + userId);
        } else {
            Log.e("WorkoutListActivity", "No user data in Intent");
            currentUser = User.createGuestUser();
        }
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Устанавливаем пользователя в ViewModel
        if (currentUser != null) {
            // Создаем временный MutableLiveData для установки пользователя
            MutableLiveData<User> userLiveData = new MutableLiveData<>();
            userLiveData.setValue(currentUser);

            // Устанавливаем пользователя в ViewModel через рефлексию (временно)
            try {
                java.lang.reflect.Field field = MainViewModel.class.getDeclaredField("currentUserLiveData");
                field.setAccessible(true);
                MutableLiveData<User> vmUserLiveData = (MutableLiveData<User>) field.get(viewModel);
                vmUserLiveData.setValue(currentUser);
                Log.d("WorkoutListActivity", "User set in ViewModel: " + currentUser.getUid());
            } catch (Exception e) {
                Log.e("WorkoutListActivity", "Error setting user in ViewModel", e);
            }
        }
        Log.d("WorkoutListActivity", "ViewModel initialized");
    }

    private void loadWorkouts() {
        Log.d("WorkoutListActivity", "Loading workouts for user: " + (currentUser != null ? currentUser.getUid() : "null"));

        if (currentUser == null || currentUser.isGuest()) {
            Log.d("WorkoutListActivity", "User is null or guest - showing empty state");
            showEmptyState("Гостевой режим. Тренировки недоступны.");
            return;
        }

        viewModel.loadWorkouts();
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

    private void setupLiveDataObservers() {
        // ИСПРАВЛЕНО: Сначала проверяем пользователя, потом загружаем тренировки
        viewModel.getCurrentUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d("WorkoutListActivity", "User updated: " + (user != null ? user.getUid() : "null"));

                if (user == null) {
                    Log.d("WorkoutListActivity", "User is null, showing empty state");
                    showEmptyState("Пользователь не найден");
                    return;
                }

                if (user.isGuest()) {
                    Log.d("WorkoutListActivity", "User is guest, showing empty state");
                    showEmptyState("Гостевой режим. Тренировки недоступны.");
                    return;
                }

                // Пользователь загружен и не гость - загружаем тренировки
                Log.d("WorkoutListActivity", "Valid user loaded, checking existing workouts");

                // Проверяем, есть ли уже данные в ViewModel
                List<Workout> currentWorkouts = viewModel.getWorkoutsLiveData().getValue();
                if (currentWorkouts != null && !currentWorkouts.isEmpty()) {
                    Log.d("WorkoutListActivity", "Using existing workouts: " + currentWorkouts.size());
                    updateUIWithWorkouts(currentWorkouts);
                } else {
                    Log.d("WorkoutListActivity", "No existing workouts, loading from database");
                    viewModel.loadWorkouts();
                }
            }
        });

        // Наблюдатель для тренировок
        viewModel.getWorkoutsLiveData().observe(this, new Observer<List<Workout>>() {
            @Override
            public void onChanged(List<Workout> workouts) {
                Log.d("WorkoutListActivity", "=== WORKOUTS LIVE DATA UPDATED ===");
                Log.d("WorkoutListActivity", "Received " + (workouts != null ? workouts.size() : 0) + " workouts");

                updateUIWithWorkouts(workouts);
            }
        });
    }

    // ИСПРАВЛЕНО: Вынес логику обновления UI в отдельный метод
    private void updateUIWithWorkouts(List<Workout> workouts) {
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

    private void checkUIState() {
        Log.d("WorkoutListActivity", "=== UI STATE CHECK ===");
        Log.d("WorkoutListActivity", "tvEmpty: " + (tvEmpty != null ?
                "visible=" + (tvEmpty.getVisibility() == View.VISIBLE) : "null"));
        Log.d("WorkoutListActivity", "rvWorkouts: " + (rvWorkouts != null ?
                "visible=" + (rvWorkouts.getVisibility() == View.VISIBLE) : "null"));
        Log.d("WorkoutListActivity", "Adapter item count: " + workoutAdapter.getItemCount());
    }

    private void showEmptyState(String message) {
        runOnUiThread(() -> {
            if (tvEmpty != null) {
                tvEmpty.setText(message);
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (rvWorkouts != null) {
                rvWorkouts.setVisibility(View.GONE);
            }
        });
    }

    private void hideEmptyState() {
        runOnUiThread(() -> {
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.GONE);
            }
            if (rvWorkouts != null) {
                rvWorkouts.setVisibility(View.VISIBLE);
            }
        });
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