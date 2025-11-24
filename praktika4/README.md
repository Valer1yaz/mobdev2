**Отчёт по практической работе №4**
-----------
Основные способы отображения списков


**Цель работы**

Изучить различные виды списков, их преимущества и недостатки. Реализовать отображение замоканных данных через RecycleView в проекте.

-----------
**Практическая часть**


-----------
1 ScrollView.
-----------
Был создан новый модуль, в котором подготовлен файл разметки item.xml, определяющий структуру отдельного элемента списка, включающего текстовое поле и изображение. Основной экран приложения, описанный в activity_main.xml, содержит контейнер ScrollView, внутрь которого помещён LinearLayout для последовательного размещения элементов.

В классе MainActivity программно, с использованием LayoutInflater, динамически создавались представления для каждого из 100 элементов списка. Для каждого элемента вычислялось значение геометрической прогрессии (с множителем 2), которое вместе с порядковым номером устанавливалось в текстовое поле. Для изображения назначалась стандартная иконка. Все созданные элементы добавлялись в линейный контейнер, что в итоге позволило отобразить на экране прокручиваемый список.

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout wrapper = findViewById(R.id.wrapper);
        BigDecimal firstTerm = BigDecimal.ONE;
        BigDecimal ratio = new BigDecimal("2");

        for (int i = 1; i <= 100; i++) {
            BigDecimal term = firstTerm.multiply(ratio.pow(i - 1));
            View view = getLayoutInflater().inflate(R.layout.item, null, false);
            TextView text = view.findViewById(R.id.textView);
            text.setText(String.format("Элемент %d: %s", i, term.toString()));
            wrapper.addView(view);
        }
    }
}
```

<img width="1811" height="943" alt="image" src="https://github.com/user-attachments/assets/e2f56379-5e18-4566-a43a-e97fe0921a15" />



-----------
2 ListView
-----------

Для работы с этим компонентом был создан отдельный модуль. В его разметке главного экрана был размещён виджет ListView. В коде активности MainActivity был объявлен и инициализирован массив строк, содержащий названия различных стран.

Для связи данных с интерфейсом был использован ArrayAdapter. Стандартная разметка simple_list_item_2 была адаптирована путём переопределения метода getView(). В этом методе для каждого элемента списка в верхнее текстовое поле выводился его порядковый номер, а в нижнее — соответствующее название страны из массива. Настроенный таким образом адаптер был назначен для ListView, в результате чего на экране отобразился нумерованный список стран.

```
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    List<Book> books = new ArrayList<>();
    books.add(new Book("Мари Кондо", "Магическая уборка. Японское искусство наведения порядка дома и в жизни"));
    books.add(new Book("Фумио Сасаки", "Хорошийbye, вещи. Минимализм по-японски"));

...

        ListView booksListView = findViewById(R.id.books_list_view);
        ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                books) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                Book book = getItem(position);
                text1.setText(book.getTitle());
                text2.setText(book.getAuthor());
                return view;
            }
        };

        booksListView.setAdapter(adapter);
    }
}
```

<img width="1818" height="950" alt="image" src="https://github.com/user-attachments/assets/5799b9f2-3d7b-4f7d-8dca-a0315eaf08ab" />


-----------
3 RecyclerView
-----------

Был создан ещё один модуль для демонстрации работы с более современным и производительным компонентом RecyclerView. Для элемента списка была создана собственная разметка item_event.xml, содержащая ImageView для изображения и два TextView для заголовка и описания.

<img width="1813" height="935" alt="image" src="https://github.com/user-attachments/assets/57aa2605-665b-401c-9dcf-97dfb90cccc0" />


Был создан класс данных GarbageType, содержащий: название, описание и идентификатор ресурса изображения. 

```
public class GarbageType {
    private String eventName;
    private String description;
    private String imageName;
    private int year;

    public GarbageType(String eventName, String description, String imageName, int year) {
        this.eventName = eventName;
        this.description = description;
        this.imageName = imageName;
        this.year = year;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return this.eventName + " (" + this.year + ")";
    }
}
```

Для связи этих данных с представлением реализован адаптер EventAdapter, который включает в себя класс EventViewHolder. В адаптере переопределены стандартные методы: onCreateViewHolder() для создания нового экземпляра ViewHolder, onBindViewHolder() для привязки данных конкретного события к элементам интерфейса и getItemCount().

```
public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private List<GarbageType> events;
    private Context context;
    public EventRecyclerViewAdapter(List<GarbageType> events) {
        this.events = events;
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View recyclerViewItem = LayoutInflater.from(context)
                .inflate(R.layout.event_item_view, parent, false);
        return new EventViewHolder(recyclerViewItem);
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        GarbageType event = this.events.get(position);
        String pkgName = context.getPackageName();
        int resID = context.getResources().getIdentifier(event.getImageName(), "drawable", pkgName);
        if (resID == 0) {
            resID = R.drawable.ic_garbage;
        }
        holder.getFlagView().setImageResource(resID);
        holder.getEventNameView().setText(event.getEventName());
        holder.getDescriptionView().setText(event.getDescription());
        holder.getYearView().setText(String.valueOf(event.getYear()));
    }
    @Override
    public int getItemCount() {
        return this.events.size();
    }
}
```

В MainActivity был инициализирован RecyclerView и назначен ему LinearLayoutManager для линейного расположения элементов. Был создан список объектов, который передавался в конструктор адаптера. Настроенный адаптер был установлен для RecyclerView, что привело к отображению на экране прокручиваемого списка карточек с информацией о исторических событиях.

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<GarbageType> events = getListData();
        RecyclerView recyclerView = this.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new EventRecyclerViewAdapter(events));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private List<GarbageType> getListData() {
        List<GarbageType> list = new ArrayList<GarbageType>();

        list.add(new GarbageType("Макулатура",
                "Бумажные и картонные отходы, которые перерабатываются путем измельчения, очистки и превращения в новую бумажную продукцию",
                "waste_paper", 105));

        list.add(new GarbageType("Стеклобой",
                "Битое стекло, которое дробится, плавится и используется для производства новой стеклянной тары и строительных материалов",
                "glass_waste", 1500));

        list.add(new GarbageType("Полимерные отходы",
                "Пластиковые отходы, которые сортируются по типам пластика, измельчаются и переплавляются для создания новых изделий или химической переработки",
                "polymer_waste", 1907));

        list.add(new GarbageType("Металлолом",
                "Черные и цветные металлы, которые переплавляются в печах для производства нового металла с значительной экономией энергии и ресурсов",
                "metal_scrap", -3000));

        list.add(new GarbageType("Органические отходы",
                "Пищевые и растительные отходы, которые перерабатываются методами компостирования или анаэробного сбраживания с получением удобрений и биогаза",
                "organic_waste", -10000));

        return list;
    }
}
```

----------
*Доработка FitMotiv*
----------

Задание: Создать в репозитории заглушки с набором данных (напр. которые
планируете использовать при взаимодействии с API внешнего сервиса). Передать
эти данные в слой представления с помощью LiveData и установить в RecycleView.

Реализована передача потока данных цитат от внешнего API до UI с использованием LiveData и механизма, применимого к RecyclerView.

Внешний API → Repository → Use Case → ViewModel (LiveData) → Activity → UI

1. Получение данных из внешнего сервиса

1.1. API сервис (QuoteApiService.java)

Определён интерфейс для работы с API forismatic.com:

```
public interface QuoteApiService {
    @GET("api/1.0/")
    Call<QuoteApiResponse> getQuote(
            @Query("method") String method,
            @Query("format") String format,
            @Query("lang") String lang  // "ru" для русского языка
    );
}
```

1.2. Модель данных (QuoteApiResponse.java)
```
public class QuoteApiResponse {
    @SerializedName("quoteText")
    private String quoteText;
    
    @SerializedName("quoteAuthor")
    private String quoteAuthor;
    
    // Getters и Setters
    public String getQuoteText() { return quoteText; }
    public String getQuoteAuthor() { 
        return quoteAuthor != null && !quoteAuthor.isEmpty() 
            ? quoteAuthor : "Неизвестный автор"; 
    }
}
```

2. Слой данных (Repository)

2.1. QuoteRepositoryImpl.java

Репозиторий получает данные из API и обрабатывает ошибки:

```
public class QuoteRepositoryImpl implements QuoteRepository {
    private static final String BASE_URL = "https://api.forismatic.com/";
    private final QuoteApiService quoteApiService;
    private final Retrofit retrofit;
    
    public QuoteRepositoryImpl() {
        // Настройка OkHttpClient с таймаутами
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        
        // Инициализация Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        quoteApiService = retrofit.create(QuoteApiService.class);
    }

    @Override
    public String getMotivationalQuote() {
        try {
            // Получение цитаты на русском языке
            Call<QuoteApiResponse> call = quoteApiService.getQuote("getQuote", "json", "ru");
            Response<QuoteApiResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                QuoteApiResponse apiResponse = response.body();
                String quote = apiResponse.getQuoteText();
                String author = apiResponse.getQuoteAuthor();
                
                if (quote != null && !quote.trim().isEmpty()) {
                    return quote + " - " + author;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении цитаты из API", e);
        }
        
        // Fallback на локальные цитаты при ошибке
        return fallbackQuotes[new Random().nextInt(fallbackQuotes.length)];
    }
}
```

3. Слой домена (Use Case)

3.1. GetMotivationalQuoteUseCase.java
   
```
public class GetMotivationalQuoteUseCase {
    private final QuoteRepository quoteRepository;

    public GetMotivationalQuoteUseCase(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public String execute() {
        return quoteRepository.getMotivationalQuote();
    }
}
```

4. Слой представления (ViewModel + LiveData)

4.1. MainViewModel.java

ViewModel использует LiveData для передачи данных в UI:

```
public class MainViewModel extends AndroidViewModel {
    // LiveData для цитат
    private MutableLiveData<String> quoteLiveData;
    private ExecutorService executorService;
    private GetMotivationalQuoteUseCase getMotivationalQuoteUseCase;
    
    public MainViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        initializeRepositories(application);
        initializeLiveData();
    }
    
    private void initializeLiveData() {
        quoteLiveData = new MutableLiveData<>();
    }
    
    // Метод для загрузки цитаты
    public void loadMotivationalQuote() {
        executorService.execute(() -> {
            try {
                // Выполнение Use Case в фоновом потоке
                String quote = getMotivationalQuoteUseCase.execute();
                // Обновление LiveData (автоматически переключается на главный поток)
                quoteLiveData.postValue(quote);
            } catch (Exception e) {
                quoteLiveData.postValue("Великие дела требуют великих усилий!");
            }
        });
    }
    
    // Getter для LiveData
    public LiveData<String> getQuoteLiveData() {
        return quoteLiveData;
    }
}
```


5. Слой представления (Activity)

5.1. MainActivity.java — текущая реализация (TextView)
   
```
public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private TextView tvQuote;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();
        loadInitialData();
    }
    
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
    
    private void initializeUI() {
        tvQuote = findViewById(R.id.tvQuote);
    }
    
    // Настройка наблюдателя для LiveData
    private void setupLiveDataObservers() {
        viewModel.getQuoteLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String quote) {
                // Автоматически вызывается при изменении данных
                if (quote != null && tvQuote != null) {
                    tvQuote.setText("\"" + quote + "\"");
                }
            }
        });
    }
    
    private void loadInitialData() {
        // Запрос данных через ViewModel
        viewModel.loadMotivationalQuote();
    }
}
```
В данном проекте реализовано получение цитат:

1. Передача данных через LiveData:
- В MainViewModel есть MutableLiveData<String> quoteLiveData
- Метод loadMotivationalQuote() загружает данные и обновляет LiveData через postValue()
- Getter getQuoteLiveData() возвращает LiveData
2. Observer в Activity:
- В MainActivity есть Observer<String> для цитат
- При изменении данных обновляется TextView (tvQuote.setText())
3. Полный поток данных:
- API → Repository → Use Case → ViewModel (LiveData) → Activity (Observer) → TextView
4. RecyclerView в проекте есть  для других данных:
- rvProgressPhotos — для фото прогресса
- rvWorkouts — для списка тренировок
5. Адаптер:
- Есть WorkoutAdapter и ProgressPhotoAdapter

<img width="456" height="889" alt="image" src="https://github.com/user-attachments/assets/15000da7-72c3-4e23-89aa-69a43327a4e6" /> <img width="437" height="867" alt="image" src="https://github.com/user-attachments/assets/ee60bf7a-04d6-44ea-9b8a-0d07f7fc7639" />



Выводы
----------

- Данные цитат получаются из внешнего API через Retrofit
- Repository обрабатывает запросы и ошибки
- Use Case инкапсулирует бизнес-логику
- ViewModel использует LiveData для передачи данных
- Activity подписывается на LiveData через Observer
- UI обновляется автоматически при изменении данных

 Механизм одинаков для TextView и RecyclerView: LiveData уведомляет наблюдателя, который обновляет UI. Для RecyclerView обновляется адаптер, который перерисовывает список.

*Преимущества использования LiveData:*

- Реактивность: UI обновляется при изменении данных
- Управление жизненным циклом: подписка активна только при наличии активного наблюдателя
- Безопасность потоков: postValue() можно вызывать из любого потока
- Отсутствие утечек памяти: автоматическая отписка при уничтожении Activity
- Сохранение состояния: данные сохраняются при повороте экрана
