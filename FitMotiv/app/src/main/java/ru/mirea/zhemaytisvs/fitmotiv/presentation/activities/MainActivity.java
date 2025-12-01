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

import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.AuthRepositoryImpl;

public class MainActivity extends AppCompatActivity {

    // ViewModel для взаимодействия со слоем domain
    private MainViewModel viewModel;

    // UI Components
    private TextView tvQuote;
    private TextView tvWorkoutCount;
    private TextView tvUserInfo;
    private Button btnAddWorkout;
    private Button btnViewWorkouts;
    private Button btnGetQuote;
    private Button btnLogout;

    // Текущий пользователь
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ПЕРВОЕ: Инициализация ViewModel
        initializeViewModel();

        // ВТОРОЕ: Проверяем аутентификацию и перенаправляем если нужно
        checkAuthenticationAndRedirect();
    }

    private void checkAuthenticationAndRedirect() {
        viewModel.getCurrentUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d("MainActivity", "Authentication check - User: " + (user != null ? user.getUid() : "null"));

                if (user == null) {
                    // Пользователь не аутентифицирован - переходим на LoginActivity
                    Log.d("MainActivity", "No authenticated user, redirecting to LoginActivity");
                    redirectToLogin();
                } else {
                    // Пользователь аутентифицирован - продолжаем инициализацию
                    Log.d("MainActivity", "User authenticated: " + user.getUid() + ", proceeding with main screen");
                    currentUser = user;
                    completeInitialization();
                }
            }
        });

        // Запускаем загрузку пользователя
        viewModel.loadCurrentUser();
    }

    private void completeInitialization() {
        // Инициализация UI и наблюдателей
        initializeUI();
        setupLiveDataObservers();

        // Загрузка данных
        loadInitialData();
        setupEventListeners();
        setupUserMode();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Закрываем MainActivity чтобы нельзя было вернуться назад
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем данные при возврате на экран
        viewModel.loadWorkouts();
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
                    }
                } else {
                    Log.e("MainActivity", "User LiveData returned null");
                }
            }
        });
    }

    private void initializeUI() {
        // Находим все View компоненты
        tvQuote = findViewById(R.id.tvQuote);
        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        btnViewWorkouts = findViewById(R.id.btnViewWorkouts);
        btnGetQuote = findViewById(R.id.btnGetQuote);
        btnLogout = findViewById(R.id.btnLogout);

    }

    private void loadInitialData() {
        // Оптимизированная загрузка: сначала критичные данные, затем остальные
        // Загружаем пользователя в первую очередь
        if (currentUser == null) {
            viewModel.loadCurrentUser();
        }
        
        // Загружаем цитату
        viewModel.loadMotivationalQuote();
        
        // Загружаем данные для текущего пользователя (если он уже загружен)
        if (currentUser != null && !currentUser.isGuest()) {
            viewModel.loadWorkouts();
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

                // Передаем информацию о пользователе
                User currentUser = viewModel.getCurrentUserLiveData().getValue();
                if (currentUser != null) {
                    intent.putExtra("user_id", currentUser.getUid());
                    intent.putExtra("user_email", currentUser.getEmail());
                    intent.putExtra("user_display_name", currentUser.getDisplayName());
                    intent.putExtra("is_guest", currentUser.isGuest());
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


        // Кнопка выхода
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    // МЕТОД: Выход из аккаунта
    private void logout() {
        AuthRepository authRepository = new AuthRepositoryImpl(this);
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

        // ДОБАВЛЕНО: Устанавливаем userId
        dialog.setUserId(user.getUid());

        dialog.setOnWorkoutAddedListener(new AddWorkoutDialog.OnWorkoutAddedListener() {
            @Override
            public void onWorkoutAdded(Workout workout) {
                Log.d("MainActivity", "Workout added via dialog: " + workout.getDescription() + ", userId: " + workout.getUserId());
                viewModel.addWorkout(workout);
                showToast("Тренировка добавлена!");
            }
        });
        dialog.show(getSupportFragmentManager(), "AddWorkoutDialog");
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}