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
        loadInitialData();
        setupEventListeners();

        // ЧЕТВЕРТОЕ: Настройка режима пользователя
        setupUserMode();
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
            } else {
                // Для зарегистрированных пользователей загружаем через ViewModel
                viewModel.loadCurrentUser();
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

    // ИСПРАВЛЕННЫЙ МЕТОД: Настройка режима пользователя
    private void setupUserMode() {
        // ДОБАВЛЕНА ПРОВЕРКА НА NULL
        if (currentUser == null) {
            Log.e("MainActivity", "Current user is null in setupUserMode");
            return;
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


    // ДОБАВЛЕННЫЙ МЕТОД: Настройка гостевого режима
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

    // ДОБАВЛЕННЫЙ МЕТОД: Настройка полного режима
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

        // Наблюдатель для целей
        viewModel.getGoalLiveData().observe(this, new Observer<UserGoal>() {
            @Override
            public void onChanged(UserGoal goal) {
                if (goal != null && tvGoalInfo != null) {
                    String goalInfo = String.format("Цель: %s\nТренировок в неделю: %d",
                            goal.getDescription(), goal.getWorkoutsPerWeek());
                    tvGoalInfo.setText(goalInfo);
                } else if (tvGoalInfo != null) {
                    tvGoalInfo.setText("Цель не установлена");
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
                if (user != null) {
                    currentUser = user;
                    if (tvUserInfo != null) {
                        String userInfo = user.isGuest() ?
                                "Гостевой режим" :
                                "Пользователь: " + (user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
                        tvUserInfo.setText(userInfo);
                    }
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

        // Настраиваем RecyclerView для фото прогресса
        progressPhotoAdapter = new ProgressPhotoAdapter(new ArrayList<>());
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
        
        // Загружаем тренировки (может быть долго из-за сети)
        viewModel.loadWorkouts();
        
        // Ленивая загрузка остальных данных (можно загружать по требованию)
        // viewModel.loadProgressPhotos();
        // viewModel.loadGoal();
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
                setNewGoal();
            }
        });

        // Кнопка анализа упражнения
        btnAnalyzeExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeExercise();
            }
        });

        // ДОБАВЛЕНО: Кнопка выхода
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    // ДОБАВЛЕННЫЙ МЕТОД: Выход из аккаунта
    private void logout() {
        AuthRepository authRepository = new AuthRepositoryImpl();
        authRepository.logout();

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        // Переходим на экран логина
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void addNewWorkout() {
        // Создаем новую тестовую тренировку
        Workout newWorkout = new Workout(
                String.valueOf(System.currentTimeMillis()),
                Workout.WorkoutType.STRENGTH,
                60, // длительность в минутах
                450, // калории
                new Date(),
                "Силовая тренировка с штангой"
        );

        // Добавляем тренировку через ViewModel
        viewModel.addWorkout(newWorkout);
        showToast("Тренировка добавлена!");
    }

    private void setNewGoal() {
        // Создаем новую цель
        UserGoal newGoal = new UserGoal(
                String.valueOf(System.currentTimeMillis()),
                70, // целевой вес
                5,  // тренировок в неделю
                "Набрать мышечную массу",
                false
        );

        // Устанавливаем цель через ViewModel
        viewModel.setGoal(newGoal);
        showToast("Новая цель установлена!");
    }

    private void analyzeExercise() {
        // Имитируем данные изображения для анализа
        byte[] mockImageData = new byte[1024];

        // Анализируем упражнение через ViewModel
        viewModel.analyzeExercise("Приседания", mockImageData);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}