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

Создать в репозитории заглушки с набором данных (напр. которые
планируете использовать при взаимодействии с API внешнего сервиса). Передать
эти данные в слой представления с помощью LiveData и установить в RecycleView.





