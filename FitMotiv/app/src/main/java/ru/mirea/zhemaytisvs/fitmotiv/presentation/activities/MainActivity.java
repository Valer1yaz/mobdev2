package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ExerciseAnalysis;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.AuthRepositoryImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ViewModel для взаимодействия со слоем domain
    private MainViewModel viewModel;

    // UI Components
    private TextView tvQuote;
    private TextView tvWorkoutCount;
    private TextView tvGoalInfo;
    private TextView tvUserInfo;
    private RecyclerView rvProgressPhotos;
    private ProgressPhotoAdapter progressPhotoAdapter;
    private Button btnAddWorkout;
    private Button btnViewWorkouts;
    private Button btnGetQuote;
    private Button btnSetGoal;
    private Button btnAnalyzeExercise;
    private Button btnLogout;
    private Button btnAddPhoto;

    // Текущий пользователь
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ПЕРВОЕ: Инициализация ViewModel (должна быть до использования)
        initializeViewModel();

        // ВТОРОЕ: Получаем пользователя из Intent или проверяем аутентификацию
        initializeUser();

        // ТРЕТЬЕ: Инициализация UI и наблюдателей
        initializeUI();
        setupLiveDataObservers();

        // ЧЕТВЕРТОЕ: Задержка для инициализации пользователя перед setupUserMode
        new android.os.Handler().postDelayed(() -> {
            loadInitialData();
            setupEventListeners();
            setupUserMode(); // Вызываем здесь с задержкой
        }, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем данные при возврате на экран
        viewModel.loadProgressPhotos();
        viewModel.loadWorkouts();
        viewModel.checkGoalProgress();
    }

    // Инициализация ViewModel
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    // Инициализация пользователя
    private void initializeUser() {
        // Получаем пользователя из Intent (из LoginActivity)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("isGuest")) {
            boolean isGuest = intent.getBooleanExtra("isGuest", false);
            String userId = intent.getStringExtra("user");

            if (isGuest) {
                currentUser = User.createGuestUser();
                Log.d("MainActivity", "Guest user initialized");
            } else {
                // Для зарегистрированных пользователей загружаем через ViewModel
                viewModel.loadCurrentUser();
                Log.d("MainActivity", "Loading registered user from ViewModel");
            }
        } else {
            // Если Intent пустой, проверяем аутентификацию
            checkUserAuthentication();
        }
    }

    // Проверка аутентификации
    private void checkUserAuthentication() {
        viewModel.loadCurrentUser();
        
        // Наблюдаем за пользователем через LiveData
        viewModel.getCurrentUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) {
                    // Если пользователь не авторизован, переходим на экран логина
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    currentUser = user;
                    setupUserMode();
                }
            }
        });
    }

    // МЕТОД: Настройка режима пользователя
    private void setupUserMode() {
        Log.d("MainActivity", "setupUserMode called, currentUser: " + (currentUser != null ? currentUser.getUid() : "null"));

        if (currentUser == null) {
            Log.e("MainActivity", "Current user is null - delaying setup");
            // Попробуем получить пользователя из ViewModel
            User vmUser = viewModel.getCurrentUserLiveData().getValue();
            if (vmUser != null) {
                currentUser = vmUser;
                Log.d("MainActivity", "Retrieved user from ViewModel: " + currentUser.getUid());
            } else {
                Log.e("MainActivity", "Cannot setup user mode - user is null");
                return;
            }
        }

        if (currentUser.isGuest()) {
            setupGuestMode();
        } else {
            setupFullMode();
        }

        // Отображаем информацию о пользователе
        if (tvUserInfo != null) {
            String userInfo = currentUser.isGuest() ?
                    "Гостевой режим" :
                    "Пользователь: " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail());
            tvUserInfo.setText(userInfo);
        }
    }


    // МЕТОД: Настройка гостевого режима
    private void setupGuestMode() {
        // Отключаем или скрываем функционал для гостя
        if (btnAddWorkout != null) {
            btnAddWorkout.setEnabled(false);
            btnAddWorkout.setAlpha(0.5f);
            btnAddWorkout.setText("Добавить тренировку (только для зарегистрированных)");
        }

        if (btnSetGoal != null) {
            btnSetGoal.setEnabled(false);
            btnSetGoal.setAlpha(0.5f);
            btnSetGoal.setText("Установить цель (только для зарегистрированных)");
        }

        if (btnAnalyzeExercise != null) {
            btnAnalyzeExercise.setEnabled(false);
            btnAnalyzeExercise.setAlpha(0.5f);
            btnAnalyzeExercise.setText("Анализ упражнений (только для зарегистрированных)");
        }

        Toast.makeText(this,
                "Вы в гостевом режиме. Для полного доступа к функциям зарегистрируйтесь.",
                Toast.LENGTH_LONG).show();
    }

    // МЕТОД: Настройка полного режима
    private void setupFullMode() {
        // Весь функционал доступен
        if (btnAddWorkout != null) {
            btnAddWorkout.setEnabled(true);
            btnAddWorkout.setAlpha(1f);
            btnAddWorkout.setText("Добавить тренировку");
        }

        if (btnSetGoal != null) {
            btnSetGoal.setEnabled(true);
            btnSetGoal.setAlpha(1f);
            btnSetGoal.setText("Установить цель");
        }

        if (btnAnalyzeExercise != null) {
            btnAnalyzeExercise.setEnabled(true);
            btnAnalyzeExercise.setAlpha(1f);
            btnAnalyzeExercise.setText("Анализ упражнений");
        }

        Toast.makeText(this,
                "Добро пожаловать, " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail()) + "!",
                Toast.LENGTH_SHORT).show();
    }

    // Настройка наблюдателей для LiveData
    private void setupLiveDataObservers() {
        // Наблюдатель для цитат
        viewModel.getQuoteLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String quote) {
                if (quote != null && tvQuote != null) {
                    tvQuote.setText("\"" + quote + "\"");
                }
            }
        });

        // Наблюдатель для статистики тренировок
        viewModel.getWorkoutStatisticsLiveData().observe(this, new Observer<MainViewModel.WorkoutStatistics>() {
            @Override
            public void onChanged(MainViewModel.WorkoutStatistics stats) {
                if (stats != null && tvWorkoutCount != null) {
                    String statsText = String.format("Тренировок: %d\nСожжено калорий: %d", 
                            stats.getTotalWorkouts(), stats.getTotalCalories());
                    tvWorkoutCount.setText(statsText);
                }
            }
        });

        // Наблюдатель для целей (список)
        viewModel.getGoalsLiveData().observe(this, new Observer<List<UserGoal>>() {
            @Override
            public void onChanged(List<UserGoal> goals) {
                if (goals != null && !goals.isEmpty() && tvGoalInfo != null) {
                    int completedCount = 0;
                    for (UserGoal goal : goals) {
                        if (goal.isCompleted()) completedCount++;
                    }
                    String goalInfo = String.format("Целей: %d\nВыполнено: %d\nНе выполнено: %d",
                            goals.size(), completedCount, goals.size() - completedCount);
                    tvGoalInfo.setText(goalInfo);
                } else if (tvGoalInfo != null) {
                    tvGoalInfo.setText("Цели не установлены");
                }
            }
        });

        // Наблюдатель для фото прогресса
        viewModel.getProgressPhotosLiveData().observe(this, new Observer<List<ProgressPhoto>>() {
            @Override
            public void onChanged(List<ProgressPhoto> photos) {
                if (photos != null && progressPhotoAdapter != null) {
                    progressPhotoAdapter.updateData(photos);
                }
            }
        });

        // Наблюдатель для пользователя
        viewModel.getCurrentUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d("MainActivity", "User LiveData updated: " + (user != null ? user.getUid() : "null"));
                if (user != null) {
                    currentUser = user;
                    // ВЫЗЫВАЕМ setupUserMode() ЗДЕСЬ, когда пользователь точно загружен
                    setupUserMode();

                    if (tvUserInfo != null) {
                        String userInfo = user.isGuest() ?
                                "Гостевой режим" :
                                "Пользователь: " + (user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
                        tvUserInfo.setText(userInfo);
                    }
                    // Загружаем данные после загрузки пользователя
                    if (!user.isGuest()) {
                        viewModel.loadWorkouts();
                        viewModel.loadProgressPhotos();
                        viewModel.loadGoals();
                        viewModel.checkGoalProgress();
                    }
                } else {
                    Log.e("MainActivity", "User LiveData returned null");
                }
            }
        });

        // Наблюдатель для анализа упражнений
        viewModel.getExerciseAnalysisLiveData().observe(this, new Observer<ExerciseAnalysis>() {
            @Override
            public void onChanged(ExerciseAnalysis analysis) {
                if (analysis != null) {
                    String result = String.format(
                            "Упражнение: %s\nОценка: %.1f%%\n%s",
                            analysis.getExerciseName(),
                            analysis.getCorrectnessScore(),
                            analysis.getFeedback()
                    );
                    showToast("Анализ завершен:\n" + result);
                }
            }
        });
    }

    private void initializeUI() {
        // Находим все View компоненты
        tvQuote = findViewById(R.id.tvQuote);
        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);
        tvGoalInfo = findViewById(R.id.tvGoalInfo);
        tvUserInfo = findViewById(R.id.tvUserInfo); // ДОБАВЛЕНО
        rvProgressPhotos = findViewById(R.id.rvProgressPhotos);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        btnViewWorkouts = findViewById(R.id.btnViewWorkouts);
        btnGetQuote = findViewById(R.id.btnGetQuote);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        btnAnalyzeExercise = findViewById(R.id.btnAnalyzeExercise);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);

        // Настраиваем RecyclerView для фото прогресса
        progressPhotoAdapter = new ProgressPhotoAdapter(new ArrayList<>());
        progressPhotoAdapter.setOnPhotoClickListener(new ProgressPhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(ProgressPhoto photo) {
                openPhotoDetail(photo);
            }
        });
        rvProgressPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProgressPhotos.setAdapter(progressPhotoAdapter);
    }

    private void loadInitialData() {
        // Оптимизированная загрузка: сначала критичные данные, затем остальные
        // Загружаем пользователя в первую очередь (если нужно)
        if (currentUser == null) {
            viewModel.loadCurrentUser();
        }
        
        // Загружаем цитату (быстрая операция)
        viewModel.loadMotivationalQuote();
        
        // Загружаем данные для текущего пользователя (если он уже загружен)
        if (currentUser != null && !currentUser.isGuest()) {
            viewModel.loadWorkouts();
            viewModel.loadProgressPhotos();
            viewModel.loadGoals();
            viewModel.checkGoalProgress();
        }
    }

    private void setupEventListeners() {
        // Кнопка добавления тренировки
        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewWorkout();
            }
        });

        // Кнопка просмотра всех тренировок
        btnViewWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WorkoutListActivity.class);
                // ДОБАВЛЕНО: Передаем информацию о пользователе
                User currentUser = viewModel.getCurrentUserLiveData().getValue();
                if (currentUser != null) {
                    intent.putExtra("user_id", currentUser.getUid());
                }
                startActivity(intent);
            }
        });

        // Кнопка получения новой цитаты
        btnGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.loadMotivationalQuote();
            }
        });

        // Кнопка установки цели
        btnSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем Activity со списком целей
                Intent intent = new Intent(MainActivity.this, GoalsListActivity.class);
                startActivity(intent);
            }
        });

        // Кнопка анализа упражнения
        btnAnalyzeExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeExercise();
            }
        });

        // Кнопка выхода
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Кнопка добавления фото
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null || currentUser.isGuest()) {
                    showToast("Для добавления фото необходимо зарегистрироваться");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, AddPhotoActivity.class);
                startActivity(intent);
            }
        });
    }

    // МЕТОД: Выход из аккаунта
    private void logout() {
        AuthRepository authRepository = new AuthRepositoryImpl();
        authRepository.logout();

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        // Переходим на экран логина
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void addNewWorkout() {
        Log.d("MainActivity", "=== ADD NEW WORKOUT DIALOG REQUESTED ===");

        // Проверяем currentUser и пользователя из ViewModel
        User user = currentUser;
        if (user == null) {
            user = viewModel.getCurrentUserLiveData().getValue();
            if (user != null) {
                currentUser = user;
                Log.d("MainActivity", "Retrieved user from ViewModel for workout: " + user.getUid());
            }
        }

        Log.d("MainActivity", "Current user for workout: " + (user != null ? user.getUid() : "null"));

        if (user == null || user.isGuest()) {
            Log.d("MainActivity", "User is guest or null, showing toast");
            showToast("Для добавления тренировки необходимо зарегистрироваться");
            return;
        }

        AddWorkoutDialog dialog = new AddWorkoutDialog();
        dialog.setOnWorkoutAddedListener(new AddWorkoutDialog.OnWorkoutAddedListener() {
            @Override
            public void onWorkoutAdded(Workout workout) {
                Log.d("MainActivity", "Workout added via dialog: " + workout.getDescription());
                viewModel.addWorkout(workout);
                showToast("Тренировка добавлена!");
                }
        });
        dialog.show(getSupportFragmentManager(), "AddWorkoutDialog");
    }


    private void analyzeExercise() {
        // Открываем Activity для анализа упражнений
        Intent intent = new Intent(this, ExerciseAnalysisActivity.class);
        startActivity(intent);
    }

    private void openPhotoDetail(ProgressPhoto photo) {
        Intent intent = new Intent(this, ProgressPhotoDetailActivity.class);
        intent.putExtra("photo_id", photo.getId());
        intent.putExtra("image_url", photo.getImageUrl());
        intent.putExtra("description", photo.getDescription());
        intent.putExtra("date", photo.getDate().getTime());
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}