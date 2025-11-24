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

    private LinearLayout wrapper;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wrapper = findViewById(R.id.wrapper);
        scrollView = findViewById(R.id.scrollView);

        BigInteger value = BigInteger.ONE;
        for (int i = 1; i <= 100; i++) {
            value = value.shiftLeft(1);

            View view = getLayoutInflater().inflate(R.layout.item, null, false);

            TextView text = view.findViewById(R.id.textView);
            text.setText(String.format("Элемент %d: %s", i, value.toString()));

            ImageView img = view.findViewById(R.id.imageView);
            img.setImageResource(android.R.drawable.ic_dialog_info);

            wrapper.addView(view);
        }
    }
}
```


-----------
2 ListView
-----------

Для работы с этим компонентом был создан отдельный модуль. В его разметке главного экрана был размещён виджет ListView. В коде активности MainActivity был объявлен и инициализирован массив строк, содержащий названия различных стран.

Для связи данных с интерфейсом был использован ArrayAdapter. Стандартная разметка simple_list_item_2 была адаптирована путём переопределения метода getView(). В этом методе для каждого элемента списка в верхнее текстовое поле выводился его порядковый номер, а в нижнее — соответствующее название страны из массива. Настроенный таким образом адаптер был назначен для ListView, в результате чего на экране отобразился нумерованный список стран.

```
public class MainActivity extends AppCompatActivity {
    private ListView listViewCountries;

    private String[] countries = new String[] {
            "Австралия", "Австрия", "Аргентина", "Бангладеш", "Белорусь", "Венгрия", "Великобритания",
            "Германия", "Гонконг", "Грузия", "Дания", "Египет", "Израиль", "Испания",
            "Италия", "Канада", "Марокко", "Норвегия", "ОАЭ", "Португалия", "Российская Федерация",
            "Сингапур", "Турция", "Уругвай", "Франция", "Хорватия", "Чехия", "Эквадор", "Япония"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewCountries = findViewById(R.id.listViewCountries);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                countries
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text2.setText(getItem(position).toString());
                text1.setText(String.valueOf(position+1));
                return view;
            }
        };

        listViewCountries.setAdapter(adapter);
    }
}
```

-----------
3 RecyclerView
-----------

Был создан ещё один модуль для демонстрации работы с более современным и производительным компонентом RecyclerView. Для элемента списка была создана собственная разметка item_event.xml, содержащая ImageView для изображения и два TextView для заголовка и описания.

Был создан класс данных HistoricalEvent, инкапсулирующий свойства исторического события: название, описание и идентификатор ресурса изображения. 

```
public class HistoricalEvent {
    private String title;
    private String description;
    private int imageResId;

    public HistoricalEvent(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
}
```

Для связи этих данных с представлением реализован адаптер EventAdapter, который включает в себя класс EventViewHolder. В адаптере переопределены стандартные методы: onCreateViewHolder() для создания нового экземпляра ViewHolder, onBindViewHolder() для привязки данных конкретного события к элементам интерфейса и getItemCount().

```
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<HistoricalEvent> events;

    public EventAdapter(List<HistoricalEvent> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        HistoricalEvent event = events.get(position);
        holder.textTitle.setText(event.getTitle());
        holder.textDescription.setText(event.getDescription());
        holder.imageEvent.setImageResource(event.getImageResId());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEvent;
        TextView textTitle, textDescription;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEvent = itemView.findViewById(R.id.imageEvent);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}
```

В MainActivity был инициализирован RecyclerView и назначен ему LinearLayoutManager для линейного расположения элементов. Был создан список объектов HistoricalEvent, который передавался в конструктор адаптера. Настроенный адаптер был установлен для RecyclerView, что привело к отображению на экране прокручиваемого списка карточек с информацией о исторических событиях.

```
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<HistoricalEvent> events = new ArrayList<>();
        events.add(new HistoricalEvent("Падение Римской империи",
                "476 год — падение Западной Римской империи, конец античности.",
                R.drawable.rome));
        events.add(new HistoricalEvent("Открытие Америки",
                "1492 год — Христофор Колумб достиг берегов Нового Света.",
                R.drawable.columbus));
        events.add(new HistoricalEvent("Первая мировая война",
                "1914–1918 годы — крупнейший конфликт начала XX века.",
                R.drawable.ww1));
        events.add(new HistoricalEvent("Высадка на Луну",
                "1969 год — Нил Армстронг сделал первый шаг на Луне.",
                R.drawable.moon));

        adapter = new EventAdapter(events);
        recyclerView.setAdapter(adapter);
    }
}
```



