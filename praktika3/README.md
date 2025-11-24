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
