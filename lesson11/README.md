**Отчёт по практической работе №3**
-----------
**Тема:**
Изучение архитектурных паттернов и внедрение MVVM в мобильное приложение
-----------
**Цель работы**
Ознакомиться с основными архитектурными паттернами (MVC, MVP, MVVM, MVI, VIPER), провести их сравнительный анализ и реализовать шаблон MVVM в рамках модификации проекта из практической работы №1.
-----------
**Теоретическая часть**
В ходе работы были рассмотрены следующие архитектурные подходы:

MVC – разделение на модель, представление и контроллер;

MVP – использование презентера для связи модели и представления;

MVVM – связь через модель представления и привязку данных;

MVI – однонаправленный поток данных на основе намерений;

VIPER – модульная архитектура с чётким разделением ответственности.

Каждый из паттернов был проанализирован с точки зрения:

состава компонентов,

области применения,

преимуществ и недостатков.

Для дальнейшей реализации был выбран MVVM как наиболее подходящий для использования с LiveData и учёта жизненного цикла компонентов Android.
-----------
**Практическая часть**
-----------
Задача: Реорганизовать приложение для сохранения и отображения любимого фильма, перенести логику из Activity в ViewModel, обеспечить сохранение состояния при повороте экрана.

1. Создание MainViewModel
Был создан класс MainViewModel, наследующийся от ViewModel. Его задачи:

хранение данных, связанных с UI;

взаимодействие с use case-ами;

управление состоянием через LiveData.
-----------
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
-----------
2. Реализация фабрики для ViewModel
Для инкапсуляции логики создания зависимостей ViewModel была написана фабрика:
-----------
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
-----------
3. Обновление MainActivity
В активности были выполнены следующие изменения:

Инициализация ViewModel через ViewModelProvider с использованием фабрики.

Подписка на изменения LiveData для автоматического обновления UI.

Вынесение обработки кликов в отдельные методы.
-----------
java
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
-----------
**Результаты**
После запуска приложения данные успешно сохраняются и загружаются.

При повороте экрана состояние интерфейса сохраняется, так как ViewModel не пересоздаётся.

Логи в Logcat подтверждают, что MainViewModel создаётся один раз, а MainActivity – при каждом изменении конфигурации.

Вывод: Использование ViewModel в связке с LiveData позволяет корректно отделить логику от представления, сохранять состояние UI и избегать лишних операций при повороте экрана.
