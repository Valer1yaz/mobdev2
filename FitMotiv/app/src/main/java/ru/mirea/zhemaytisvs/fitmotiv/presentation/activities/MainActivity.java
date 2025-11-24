package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.UserRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository; // ДОБАВЛЕНО
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.*;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.*;

// ИМПОРТЫ ДЛЯ АУТЕНТИФИКАЦИИ
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.AuthRepositoryImpl;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.GetCurrentUserUseCase;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorageImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Use Cases
    private TrackWorkoutUseCase trackWorkoutUseCase;
    private GetWorkoutHistoryUseCase getWorkoutHistoryUseCase;
    private GetMotivationalQuoteUseCase getMotivationalQuoteUseCase;
    private SetGoalUseCase setGoalUseCase;
    private GetProgressPhotosUseCase getProgressPhotosUseCase;
    private AnalyzeExerciseUseCase analyzeExerciseUseCase;

    // ДОБАВЛЕНО: Use Case для аутентификации
    private GetCurrentUserUseCase getCurrentUserUseCase;

    // UI Components
    private TextView tvQuote;
    private TextView tvWorkoutCount;
    private TextView tvGoalInfo;
    private TextView tvUserInfo; // ДОБАВЛЕНО: для отображения информации о пользователе
    private RecyclerView rvProgressPhotos;
    private ProgressPhotoAdapter progressPhotoAdapter;
    private Button btnAddWorkout;
    private Button btnGetQuote;
    private Button btnSetGoal;
    private Button btnAnalyzeExercise;
    private Button btnLogout; // ДОБАВЛЕНО: кнопка выхода

    // ДОБАВЛЕНО: Текущий пользователь
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ПЕРВОЕ: Получаем пользователя из Intent или проверяем аутентификацию
        initializeUser();

        setContentView(R.layout.activity_main);

        // ВТОРОЕ: Инициализация репозиториев и UI
        initializeRepositories();
        initializeUI();
        loadInitialData();
        setupEventListeners();

        // ТРЕТЬЕ: Настройка режима пользователя
        setupUserMode();
    }

    // ИСПРАВЛЕННЫЙ МЕТОД: Инициализация пользователя
    private void initializeUser() {
        // Получаем пользователя из Intent (из LoginActivity)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("isGuest")) {
            boolean isGuest = intent.getBooleanExtra("isGuest", false);
            String userId = intent.getStringExtra("user");

            if (isGuest) {
                currentUser = User.createGuestUser();
            } else {
                // Для зарегистрированных пользователей получаем из репозитория
                AuthRepository authRepository = new AuthRepositoryImpl();
                GetCurrentUserUseCase getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
                currentUser = getCurrentUserUseCase.execute();
            }
        } else {
            // Если Intent пустой, проверяем аутентификацию
            checkUserAuthentication();
        }
    }

    // ИСПРАВЛЕННЫЙ МЕТОД: Проверка аутентификации
    private void checkUserAuthentication() {
        AuthRepository authRepository = new AuthRepositoryImpl();
        GetCurrentUserUseCase getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        currentUser = getCurrentUserUseCase.execute();

        if (currentUser == null) {
            // Если пользователь не авторизован, переходим на экран логина
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
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

    private void initializeRepositories() {
        // Инициализация storage и репозиториев
        SharedPrefStorage sharedPrefStorage = new SharedPrefStorageImpl(this);
        WorkoutRepository workoutRepository = new WorkoutRepositoryImpl(sharedPrefStorage);
        QuoteRepository quoteRepository = new QuoteRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        ProgressRepository progressRepository = new ProgressRepositoryImpl();

        // Инициализация Use Cases
        trackWorkoutUseCase = new TrackWorkoutUseCase(workoutRepository);
        getWorkoutHistoryUseCase = new GetWorkoutHistoryUseCase(workoutRepository);
        getMotivationalQuoteUseCase = new GetMotivationalQuoteUseCase(quoteRepository);
        setGoalUseCase = new SetGoalUseCase(userRepository);
        getProgressPhotosUseCase = new GetProgressPhotosUseCase(progressRepository);
        analyzeExerciseUseCase = new AnalyzeExerciseUseCase(workoutRepository);
    }

    private void initializeUI() {
        // Находим все View компоненты
        tvQuote = findViewById(R.id.tvQuote);
        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);
        tvGoalInfo = findViewById(R.id.tvGoalInfo);
        tvUserInfo = findViewById(R.id.tvUserInfo); // ДОБАВЛЕНО
        rvProgressPhotos = findViewById(R.id.rvProgressPhotos);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        btnGetQuote = findViewById(R.id.btnGetQuote);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        btnAnalyzeExercise = findViewById(R.id.btnAnalyzeExercise);
        btnLogout = findViewById(R.id.btnLogout); // ДОБАВЛЕНО

        // Настраиваем RecyclerView для фото прогресса
        progressPhotoAdapter = new ProgressPhotoAdapter(new ArrayList<>());
        rvProgressPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvProgressPhotos.setAdapter(progressPhotoAdapter);
    }

    private void loadInitialData() {
        // Загружаем мотивационную цитату при старте
        loadMotivationalQuote();

        // Загружаем статистику тренировок
        loadWorkoutStatistics();

        // Загружаем фото прогресса
        loadProgressPhotos();

        // Загружаем информацию о целях
        loadGoalInfo();
    }

    private void setupEventListeners() {
        // Кнопка добавления тренировки
        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewWorkout();
            }
        });

        // Кнопка получения новой цитаты
        btnGetQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMotivationalQuote();
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

    // Остальные методы без изменений...
    private void loadMotivationalQuote() {
        try {
            String quote = getMotivationalQuoteUseCase.execute();
            tvQuote.setText("\"" + quote + "\"");
        } catch (Exception e) {
            tvQuote.setText("Великие дела требуют великих усилий!");
            showToast("Ошибка загрузки цитаты");
        }
    }

    private void loadWorkoutStatistics() {
        try {
            List<Workout> workouts = getWorkoutHistoryUseCase.execute();
            int totalWorkouts = workouts.size();
            int totalCalories = 0;

            for (Workout workout : workouts) {
                totalCalories += workout.getCalories();
            }

            String stats = String.format("Тренировок: %d\nСожжено калорий: %d", totalWorkouts, totalCalories);
            tvWorkoutCount.setText(stats);
        } catch (Exception e) {
            tvWorkoutCount.setText("Тренировок: 0\nСожжено калорий: 0");
            showToast("Ошибка загрузки статистики");
        }
    }

    private void loadProgressPhotos() {
        try {
            List<ProgressPhoto> photos = getProgressPhotosUseCase.execute();
            progressPhotoAdapter.updateData(photos);
        } catch (Exception e) {
            showToast("Ошибка загрузки фото прогресса");
        }
    }

    private void loadGoalInfo() {
        try {
            // Для демонстрации создаем тестовую цель
            UserGoal goal = new UserGoal("1", 75, 4, "Похудеть на 5 кг за месяц", false);
            setGoalUseCase.execute(goal);

            String goalInfo = String.format("Цель: %s\nТренировок в неделю: %d",
                    goal.getDescription(), goal.getWorkoutsPerWeek());
            tvGoalInfo.setText(goalInfo);
        } catch (Exception e) {
            tvGoalInfo.setText("Цель не установлена");
            showToast("Ошибка загрузки целей");
        }
    }

    private void addNewWorkout() {
        try {
            // Создаем новую тестовую тренировку
            Workout newWorkout = new Workout(
                    String.valueOf(System.currentTimeMillis()),
                    Workout.WorkoutType.STRENGTH,
                    60, // длительность в минутах
                    450, // калории
                    new Date(),
                    "Силовая тренировка с штангой"
            );

            trackWorkoutUseCase.execute(newWorkout);
            showToast("Тренировка добавлена!");

            // Обновляем статистику
            loadWorkoutStatistics();

        } catch (Exception e) {
            showToast("Ошибка добавления тренировки");
        }
    }

    private void setNewGoal() {
        try {
            // Создаем новую цель
            UserGoal newGoal = new UserGoal(
                    String.valueOf(System.currentTimeMillis()),
                    70, // целевой вес
                    5,  // тренировок в неделю
                    "Набрать мышечную массу",
                    false
            );

            setGoalUseCase.execute(newGoal);
            showToast("Новая цель установлена!");

            // Обновляем отображение цели
            loadGoalInfo();

        } catch (Exception e) {
            showToast("Ошибка установки цели");
        }
    }

    private void analyzeExercise() {
        try {
            // Имитируем данные изображения для анализа
            byte[] mockImageData = new byte[1024];

            // Анализируем упражнение
            var analysis = analyzeExerciseUseCase.execute("Приседания", mockImageData);

            String result = String.format(
                    "Упражнение: %s\nОценка: %.1f%%\n%s",
                    analysis.getExerciseName(),
                    analysis.getCorrectnessScore(),
                    analysis.getFeedback()
            );

            showToast("Анализ завершен:\n" + result);

        } catch (Exception e) {
            showToast("Ошибка анализа упражнения");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}