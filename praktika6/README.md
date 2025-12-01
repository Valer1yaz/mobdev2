**Отчёт по практической работе №6**
-----------

**Цель работы**

Освоить работу с фрагментами в Android-приложениях, создание статических и динамических фрагментов, управление навигацией и передачу данных между компонентам

-----------
**Практическая часть**

-----------
1 FragmentApp.
-----------
Реализовать приложение,	позволяющее	просматривать	список	дел	и	статус	их	выполнения.

Создан новый модуль "FragmentApp". 

Настроены зависимости в файле build.gradle, где добавлена библиотека androidx.fragment для работы с фрагментами. 
```
dependencies {
    var fragment_version = "1.8.5"
    implementation ("androidx.fragment:fragment:$fragment_version")
}
```
В главной активности MainActivity реализована проверка состояния savedInstanceState через условие if - фрагмент добавляется только при первоначальном запуске приложения, а не при каждом его пересоздании. Для передачи данных использован объект Bundle, в который помещен номер студента и этот Bundle  установлен в качестве аргументов фрагмента. 

<img width="1341" height="808" alt="image" src="https://github.com/user-attachments/assets/360ac326-3b84-49c6-a475-edfee6af3335" />

Затем создан фрагмент StudentFragment, который в методе onViewCreated с помощью requireArguments() извлекает переданный номер и отображает его в текстовом поле TextView.
Во фрагмент передан свой номер	по списку.

<img width="1166" height="551" alt="image" src="https://github.com/user-attachments/assets/5c4e412a-2301-4342-ae5b-f1cc292c07ee" />



-----------
2 FragmentManagerApp.
-----------

Для второго задания создан модуль "FragmentManagerApp".

Необходимо было скомпоновать на	главном	экране	либо	два	фрагмента	с	помощью	тега	«fragment» и	указанием	соответсвующего «name» в	main_activity.xml, либо	с использованием метода	replace в FragmentManager.

В приложение добавлено несколько фрагментов.	Создана разметка для них. Первый фрагмент содержит список видов спорта.	Второй детальную информацию о выбранном пункте. При	 выборе пункта производится обновление содержимого экрана в DetailsFragment

<img width="1255" height="969" alt="image" src="https://github.com/user-attachments/assets/5d70dc2f-d06e-42e7-be85-cdd3c895adc1" />

Создана модель данных Sport, содержащая информацию о названии вида спорта, стране происхождения и других характеристиках. 

Сначала оба фрагмента созданы как независимые компоненты - SportListFragment со списком видов спорта в ListView и SportDetailsFragment с макетом для отображения детальной информации.  На начальном этапе фрагменты не связаны между собой, и при выборе спорта в списке ничего не происходит. 

<img width="1141" height="961" alt="image" src="https://github.com/user-attachments/assets/94c5c80b-9dc2-4b3a-bc2c-f274c87b5cdc" /> <img width="1110" height="976" alt="image" src="https://github.com/user-attachments/assets/62db1fa5-8479-4112-9fae-5c1106771c4f" />

Для организации взаимодействия между фрагментами создан SharedViewModel, который использует LiveData для отслеживания выбранного спорта.

```
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Sport> selectedSport = new MutableLiveData<>();

    public void selectSport(Sport sport) {
        selectedSport.setValue(sport);
    }

    public MutableLiveData<Sport> getSelectedSport() {
        return selectedSport;
    }
}
```

В SportListFragment добавлен обработчик onItemClickListener, который при выборе элемента передаваёт данные в SharedViewModel через метод selectSport(). В SportDetailsFragment реализован Observer, который отслеживает изменения в SharedViewModel и автоматически обновляет интерфейс при получении новых данных. 

```
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectSport(sports.get(position));
            }
        });
```

Оба фрагмента были размещены в activity_main.xml с помощью тегов fragment для организации многокомпонентного интерфейса и поэтапной реализации взаимодействия между фрагментами через общую ViewModel.

<img width="1782" height="899" alt="image" src="https://github.com/user-attachments/assets/f0f77e56-ecda-46c3-bc77-f0041f653f4a" />

	
-----------
3 ResultApiFragmentApp.
-----------

Необходимо	передать данные	из одного фрагмента в другой.

Создан модуль ResultApiFragmentApp для передачи данных через Fragment Result API. 

<img width="429" height="848" alt="image" src="https://github.com/user-attachments/assets/61e36fff-5502-443e-954d-88a4f2097788" /> <img width="419" height="849" alt="image" src="https://github.com/user-attachments/assets/88b65e0a-7693-4b51-8013-5bf248da2aee" />



Gradle дополнен зависимостями
```
    var fragment_version = "1.8.5"
    implementation ("androidx.fragment:fragment:$fragment_version")
    implementation ("com.google.android.material:material:1.9.0")
```

Добавлен DataFragment с полем ввода EditText и кнопкой отправки данных. Изначально кнопка не выполняет действий, поэтому реализована логика упаковки данных в Bundle и их передачи через setFragmentResult(). 

```
public class DataFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.button_send);
        EditText editText = view.findViewById(R.id.edit_text_data);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("key", text);

                getParentFragmentManager().setFragmentResult("requestKey", bundle);

                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getParentFragmentManager(), "ModalBottomSheet");
            }
        });
    }
}
```

После этого создан BottomSheetFragment, наследующийся от BottomSheetDialogFragment, который первоначально отображался как пустая bottom sheet панель. 
На следующем этапе в BottomSheetFragment добавлен setFragmentResultListener(), который автоматически получает данные и отображает их в интерфейсе. 

```
public class BottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("requestKey", this,
                (requestKey, bundle) -> {
                    String text = bundle.getString("key");
                    TextView textView = view.findViewById(R.id.text_result);
                    textView.setText("Полученные данные: " + text);
                });
    }
}
```

Main Activity динамически добавляет DataFragment при запуске приложения, а BottomSheetFragment отображается поверх основного контента в виде выдвижной панели только после нажатия кнопки отправки.

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DataFragment())
                    .commit();
        }
    }
}
```

-----------
**Контрольное задание**

*Доработка FitMotiv*
----------

Задание: реализовать отображение списка с использованием ViewModel и нескольких фрагментов. 

<img width="1809" height="847" alt="image" src="https://github.com/user-attachments/assets/31288248-4eec-4fe9-aebe-443b17615af3" /> <img width="405" height="845" alt="image" src="https://github.com/user-attachments/assets/c62c1dd9-1bee-46c0-aeb0-8959780f68b5" />



Добавить фрагмент «Профиль», который на основе авторизационных данных пользователя формирует экран. 

<img width="1803" height="845" alt="image" src="https://github.com/user-attachments/assets/6fbb004d-65ba-46ae-9b38-d09099b09b5f" />


Для отображения экранов и навигации использовать
фрагменты, учесть использование бэк-стека

<img width="408" height="857" alt="image" src="https://github.com/user-attachments/assets/4f24049e-cb94-4aa3-bbfb-8019ed31f4de" />





