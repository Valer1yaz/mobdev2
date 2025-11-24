**Отчёт по практической работе №3**
-----------
Изучение архитектурных паттернов и внедрение MVVM в мобильное приложение


**Цель работы**

Ознакомиться с основными архитектурными паттернами (MVC, MVP, MVVM, MVI, VIPER), провести их сравнительный анализ и реализовать шаблон MVVM в рамках модификации проекта из прошлых практик. Доработать приложение для сохранения и отображения любимого фильма, перенести логику из MainActivity во ViewModel, обеспечить сохранение состояния при повороте экрана. Для реализации выбран MVVM (связь через модель представления и привязку данных) так как он больше всего подходит для использования с LiveData и контроля жизненного цикла компонентов Android.

-----------
**Практическая часть**

1. Создание MainViewModel. Был создан класс MainViewModel, наследующийся от ViewModel. Его задачи: хранение данных, связанных с UI, взаимодействие с use case-ами, управление состоянием через LiveData.

```
    java
    public class MainViewModel extends ViewModel {
        private final MovieRepository movieRepository;
        private final MutableLiveData<String> favoriteMovie = new MutableLiveData<>();

    public MainViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MutableLiveData<String> getFavoriteMovie() {
        return favoriteMovie;
    }

    public void saveMovie(MovieDomain movie) {
        boolean result = new SaveMovieToFavoriteUseCase(movieRepository).execute(movie);
        favoriteMovie.setValue(String.valueOf(result));
    }

    public void loadMovie() {
        MovieDomain movie = new GetFavoriteFilmUseCase(movieRepository).execute();
        favoriteMovie.setValue(String.format("My favorite movie is %s", movie.getName()));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
    }
```

<img width="1829" height="1034" alt="image" src="https://github.com/user-attachments/assets/84ab5112-37e5-46db-b5a1-59d1f11e2355" />


-----------
2. Реализация ViewModelFactory.

```
    java
    public class ViewModelFactory implements ViewModelProvider.Factory {
        private final Context appContext;

    public ViewModelFactory(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        MovieStorage storage = new SharedPrefMovieStorage(appContext);
        MovieRepository repo = new MovieRepositoryImpl(storage);
        return (T) new MainViewModel(repo);
    }
    }
```

<img width="1877" height="1041" alt="image" src="https://github.com/user-attachments/assets/2a115d84-89b2-4ae4-a502-c82557a15719" />

-----------

3. Обновление MainActivity. В активности выполнены следующие изменения: объявление ViewModel через ViewModelProvider с использованием созданной фабрики, подписка на изменения LiveData для автоматического обновления UI, вынесение обработки кликов в отдельные методы

```
    public class MainActivity extends AppCompatActivity {
        private MainViewModel vm;
        private EditText editText;
        private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vm = new ViewModelProvider(this, new ViewModelFactory(this))
                .get(MainViewModel.class);

        initViews();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        editText = findViewById(R.id.editTextMovie);
        textView = findViewById(R.id.textViewMovie);
    }

    private void setupObservers() {
        vm.getFavoriteMovie().observe(this, textView::setText);
    }

    private void setupClickListeners() {
        findViewById(R.id.buttonSaveMovie).setOnClickListener(v -> {
            String name = editText.getText().toString().trim();
            if (!name.isEmpty()) {
                vm.saveMovie(new MovieDomain(1, name));
                editText.setText("");
            }
        });

        findViewById(R.id.buttonGetMovie).setOnClickListener(v -> vm.loadMovie());
    }
    }
```

<img width="1352" height="932" alt="image" src="https://github.com/user-attachments/assets/7a69a3d3-3fbb-4412-af10-4d1a9a7921c2" />

-----------
После запуска приложения данные успешно сохраняются и загружаются. При повороте экрана состояние интерфейса сохраняется, так как ViewModel не пересоздаётся. Логи в Logcat подтверждают, что MainViewModel создаётся один раз, а MainActivity – при каждом изменении конфигурации. 


<img width="1349" height="215" alt="image" src="https://github.com/user-attachments/assets/18fbc3e7-9bce-40ba-adb5-67ef6377f8a7" />


<img width="1878" height="961" alt="image" src="https://github.com/user-attachments/assets/dc60b4ba-543c-4055-9e2c-1ce9e1cd6dd2" />


-----------
**Контрольное задание**
-----------

В проекте финтесмотивация требуется:
1. Переписать MainActivity для использования ViewModel и LiveData;
2. Использовать MediatorLiveData для комбинирования данных из двух источников (сеть и БД).

Для этого нужно:
- Создать ViewModel для MainActivity;
- Перенести вызовы Use Cases в ViewModel;
- Предоставить LiveData для отображения данных в UI;
- Для мотивационной цитаты использовать MediatorLiveData, которая будет объединять данные из сети и БД (если в БД есть сохраненная цитата, то показываем ее, а затем обновляем из сети);
- Реализовать Room для хранения данных (например, цитат или тренировок) и использовать вместе с сетевым источником;
- Для пункта 7 (распознавание изображений) можно добавить использование TensorFlow Lite для анализа упражнений (уже есть пустышка UseCase для анализа).

-----------
1 Взаимодействие Activity со слоем domain через ViewModel
-----------   

Создан MainViewModel, который инкапсулирует взаимодействие со слоем domain:
```
public class MainViewModel extends AndroidViewModel {
    // Use Cases для взаимодействия со слоем domain
    private TrackWorkoutUseCase trackWorkoutUseCase;
    private GetWorkoutHistoryUseCase getWorkoutHistoryUseCase;
    private GetMotivationalQuoteUseCase getMotivationalQuoteUseCase;
    // ... другие Use Cases
}
```

Изменения в MainActivity

До рефакторинга MainActivity напрямую создавала Use Cases, Прямые вызовы методов репозиториев, Бизнес-логика смешана с UI-логикой.
После рефакторинга MainActivity использует только ViewModel, Все взаимодействие со слоем domain через ViewModel, UI-логика отделена от бизнес-логики.

Пример использования:
```
// MainActivity
private void initializeViewModel() {
    viewModel = new ViewModelProvider(this).get(MainViewModel.class);
}

// Загрузка данных через ViewModel
viewModel.loadMotivationalQuote();
viewModel.loadWorkouts();
viewModel.addWorkout(newWorkout);
```
Результат - MainActivity не знает о Use Cases и репозиториях, Вся бизнес-логика в ViewModel, Соблюдена чистая архитектура

-----------
2 Обновление состояния интерфейса через LiveData
-----------

В MainViewModel созданы LiveData для всех данных:
```
// LiveData для различных типов данных
private MutableLiveData<String> quoteLiveData;
private MediatorLiveData<List<Workout>> workoutsMediatorLiveData;
private MutableLiveData<UserGoal> goalLiveData;
private MutableLiveData<List<ProgressPhoto>> progressPhotosLiveData;
private MutableLiveData<User> currentUserLiveData;
private MutableLiveData<WorkoutStatistics> workoutStatisticsLiveData;
```

Наблюдатели в MainActivity
Настроены Observer'ы для реактивного обновления UI:
```
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
    viewModel.getWorkoutStatisticsLiveData().observe(this, 
        new Observer<MainViewModel.WorkoutStatistics>() {
            @Override
            public void onChanged(MainViewModel.WorkoutStatistics stats) {
                if (stats != null && tvWorkoutCount != null) {
                    String statsText = String.format("Тренировок: %d\nСожжено калорий: %d", 
                            stats.getTotalWorkouts(), stats.getTotalCalories());
                    tvWorkoutCount.setText(statsText);
                }
            }
        });
    // ... другие наблюдатели
}
```
Преимущества - Автоматическое обновление UI при изменении данных, Нет необходимости вручную обновлять UI, Корректная обработка жизненного цикла Activity, Данные сохраняются при повороте экрана

-----------
3 Использование MediatorLiveData для объединения данных из сети и БД
-----------

Создан MediatorLiveData для объединения данных из двух источников:
```
// MediatorLiveData для тренировок
private MediatorLiveData<List<Workout>> workoutsMediatorLiveData;
private MutableLiveData<List<Workout>> workoutsFromDbLiveData;
private MutableLiveData<List<Workout>> workoutsFromNetworkLiveData;

private void setupWorkoutsMediator() {
    // MediatorLiveData объединяет данные из БД и сети
    workoutsMediatorLiveData.addSource(workoutsFromDbLiveData, dbWorkouts -> {
        combineWorkouts();
    });
    
    workoutsMediatorLiveData.addSource(workoutsFromNetworkLiveData, networkWorkouts -> {
        combineWorkouts();
    });
}
```

Логика объединения данных
```
private void combineWorkouts() {
    List<Workout> dbWorkouts = workoutsFromDbLiveData.getValue();
    List<Workout> networkWorkouts = workoutsFromNetworkLiveData.getValue();
    
    if (dbWorkouts == null) dbWorkouts = new ArrayList<>();
    if (networkWorkouts == null) networkWorkouts = new ArrayList<>();
    
    // Объединяем тренировки, избегая дубликатов по ID
    List<Workout> combinedWorkouts = new ArrayList<>(dbWorkouts);
    
    for (Workout networkWorkout : networkWorkouts) {
        boolean exists = false;
        for (Workout dbWorkout : combinedWorkouts) {
            if (dbWorkout.getId().equals(networkWorkout.getId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            combinedWorkouts.add(networkWorkout);
        }
    }
    
    workoutsMediatorLiveData.setValue(combinedWorkouts);
    updateWorkoutStatistics(combinedWorkouts);
}
```

Загрузка данных
```
public void loadWorkouts() {
    // Оптимизация: сначала загружаем из БД (быстро), затем из сети (медленно)
    executorService.execute(() -> {
        try {
            List<Workout> workouts = getWorkoutHistoryUseCase.execute();
            workoutsFromDbLiveData.postValue(workouts);
        } catch (Exception e) {
            workoutsFromDbLiveData.postValue(new ArrayList<>());
        }
    });
    
    // Загружаем тренировки из сети в фоне (не блокирует UI)
    executorService.execute(() -> {
        try {
            List<WorkoutResponse> networkWorkouts = networkApi.getWorkoutsFromCloud();
            List<Workout> domainWorkouts = convertToDomainWorkouts(networkWorkouts);
            workoutsFromNetworkLiveData.postValue(domainWorkouts);
        } catch (Exception e) {
            workoutsFromNetworkLiveData.postValue(new ArrayList<>());
        }
    });
}
```
Результат - Данные из БД и сети объединяются автоматически, Устранение дубликатов по ID, UI обновляется при изменении любого источника, Быстрая загрузка из БД, затем синхронизация с сетью.

-----------
Дополнительные доработки:
-----------
1. Реальный API для получения данных (JSON)
-Интегрирован API forismatic.com для цитат на русском языке
-Использован Retrofit для работы с API
-Реализован fallback на локальные данные при ошибках
2. Room Database
-Миграция с SharedPrefs на Room Database
-Созданы Entity, DAO и Database классы
-Асинхронная работа с БД

-----------------
Выводы
-----------------
MainActivity взаимодействует со слоем domain только через ViewModel
Состояние интерфейса обновляется реактивно через LiveData
MediatorLiveData объединяет данные из БД и сети без дубликато
