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

Создан модуль ResultApiFragmentApp для передачи данных через Fragment Result API. Добавлен DataFragment с полем ввода EditText и кнопкой отправки данных. Изначально кнопка не выполняет действий, поэтому реализована логика упаковки данных в Bundle и их передачи через setFragmentResult(). 
После этого создан BottomSheetFragment, наследующийся от BottomSheetDialogFragment, который первоначально отображался как пустая bottom sheet панель. 
На следующем этапе в BottomSheetFragment добавлен setFragmentResultListener(), который автоматически получает данные и отображает их в интерфейсе. 
Main Activity динамически добавляет DataFragment при запуске приложения, а BottomSheetFragment отображается поверх основного контента в виде выдвижной панели только после нажатия кнопки отправки.

-----------
**Контрольное задание**

*Доработка FitMotiv*
----------

Задание: реализовать отображение списка из предыдущего занятия с использованием ViewModel и нескольких фрагментов. Добавить фрагмент «Профиль», который на основе авторизационных данных пользователя
формирует экран. Для отображения экранов и навигации использовать
фрагменты, учесть использование бэк-стека


