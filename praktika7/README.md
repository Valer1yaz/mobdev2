**Отчёт по практической работе №7**
-----------

**Цель работы**

Освоить работу с элементами навигации Android-приложений.

-----------
**Практическая часть**

-----------
1 «BottomNavigationApp».
-----------
Придумать приложение и реализовать его навигацию с помощью Bottom Navigation, добавьте иконки, обновите цветовую гамму. 

Создан новый модуль.

Дополнен файл gradle

<img width="822" height="325" alt="image" src="https://github.com/user-attachments/assets/89009539-77f0-420f-b109-b6859cdb25dc" />

Созданы три фрагмента приложения в виде балванок которые выводят своё название

<img width="289" height="132" alt="image" src="https://github.com/user-attachments/assets/2a0d47ff-182b-4748-af0d-8dbdfd3adcf4" />

Они полностью одинаковы по наполнению, только отличаются выводящимся текстом по названию фрагмента

<img width="967" height="709" alt="image" src="https://github.com/user-attachments/assets/23c9c0ae-d0ad-4649-bbab-0b7ff915eec0" />

Также добавлены их xml файлы

<img width="854" height="577" alt="image" src="https://github.com/user-attachments/assets/2ea8781d-38eb-4437-b2ce-ea33bfc958b4" />

В values/colors были добавлены новые цвета

<img width="581" height="324" alt="image" src="https://github.com/user-attachments/assets/2e43873b-cac4-4857-980e-e5e4b5668b60" />

Обновлено отображение в теме

<img width="1085" height="616" alt="image" src="https://github.com/user-attachments/assets/93bc741e-1107-4db8-a2f9-eed6ac4efc9a" />

Создана папка "меню" и в неё добавлен файл самого элемента навигации с названиями и иконками, в файлы проекта добавлены иконки

<img width="764" height="499" alt="image" src="https://github.com/user-attachments/assets/6443f30c-72f2-44a7-9079-28c1ca94e4ab" />

Добавлена директория navigation и файл navigation содержащий привязку фрагментов приложения в виде ссылок к кнопкам

<img width="1471" height="804" alt="image" src="https://github.com/user-attachments/assets/d8875fbd-4ee5-4129-9cf8-0a3ec14535a0" />

Добавлено наполнение xml activity_main

<img width="1103" height="852" alt="image" src="https://github.com/user-attachments/assets/7bd5cfed-b9de-406a-96eb-ed4e645f0b40" />

Mainactivity java написан с использованием binding

<img width="1066" height="671" alt="image" src="https://github.com/user-attachments/assets/d8ae43b8-ded9-4972-9223-65ee6a016944" />

Результат работы меню

<img width="401" height="739" alt="image" src="https://github.com/user-attachments/assets/16afd0e7-b11e-4a68-a35e-363de4e9fc03" /> <img width="379" height="739" alt="image" src="https://github.com/user-attachments/assets/4b5b0d3f-7cf5-4ad4-b89d-f332f9e33a47" /> <img width="384" height="738" alt="image" src="https://github.com/user-attachments/assets/068cdf5a-d6a4-438a-be91-576710c22a78" />

-----------
2 «NavigationDrawerApp».
-----------
Придумать приложение и реализовать его навигацию с помощью Navigation Drawer, обновить цветовую гамму, реализовать закрытие шторки на кнопку «Back».

По аналогии с предыдущим модулем обновлён файл Gradle, в файлы проекта добавлены иконки, созданы файлы болванок фрагментов и их xml, также xml menu и navigation. Добавлены цвета, дополнена цветами тема.

В папке layout добавлены content_main и nav_header_main

<img width="829" height="902" alt="image" src="https://github.com/user-attachments/assets/fe106f9b-9d16-437f-99fa-b97170c4ca79" /> <img width="765" height="645" alt="image" src="https://github.com/user-attachments/assets/feaa57db-579a-4d37-ab07-92d0d5910fe1" />

В mainActivity java был подключен контроллер навигации, настроена обработка кнопки "назад" для скрытия шторки при нажатии, если она открыта. Все нажатия назад пропускаем через условие - открыта ли шторка, в ином случае даём сработать кнопке "назад" как обычно

<img width="1027" height="638" alt="image" src="https://github.com/user-attachments/assets/385ea02f-285a-4a36-85ce-f4b6d567f609" />

Вид приложения:

<img width="408" height="806" alt="image" src="https://github.com/user-attachments/assets/baeec820-827d-4212-a14b-069c0d5b4607" /> <img width="403" height="819" alt="image" src="https://github.com/user-attachments/assets/c9e6ad0e-95bb-4282-9f23-6e61bcf37cda" /> 

<img width="392" height="815" alt="image" src="https://github.com/user-attachments/assets/e8457158-930d-446a-8aef-64d857521458" /> <img width="423" height="802" alt="image" src="https://github.com/user-attachments/assets/1a202d08-e3f7-4ebe-bc98-7f74c81dea77" />

-----------
**Контрольное задание**

*Доработка FitMotiv*
----------

Задание: Реализовать в приложении навигацию с использованием Navigation Component и Navigation
Drawer/Bottom Navigation.

Я сделала bottom navigation

В Gradle модуля app добавлен вьюбиндинг
```
    buildFeatures {
        viewBinding = true;
    }
```

Далее по аналогии с практическим заданием создан файл menu/bottom_navigation_menu.xml 

<img width="571" height="528" alt="image" src="https://github.com/user-attachments/assets/9fd1f973-37a6-45a2-9341-04ee173e2fc8" />

И navigation/nav_graph.xml в котором для навигации перечислены все связи фрагментов приложения 

<img width="1187" height="913" alt="image" src="https://github.com/user-attachments/assets/2ab79118-4fc5-40d3-8cb9-dad451727b10" />


<img width="965" height="749" alt="image" src="https://github.com/user-attachments/assets/bf87ed72-5f72-439e-9a22-28921fca01ce" />

<img width="1020" height="732" alt="image" src="https://github.com/user-attachments/assets/2c6bcab1-3c7d-402e-9c6f-0e7716115fb8" />

<img width="1018" height="397" alt="image" src="https://github.com/user-attachments/assets/eaf8e38a-ebe1-46fc-8319-772d5617e6ed" />

В файлы проекта были добавлены подходящие иконки.

В layout/activity_main_nav.xml обозначено расположение на экране bottom_navigation

<img width="825" height="977" alt="image" src="https://github.com/user-attachments/assets/4e24baaa-e1a0-4c8e-bbf3-ba68f7c6ea30" />

В MainActivity.java реализовано функциональное подключение навигационного контроллера к нашему приложению и реализованной нижней менюшке

<img width="1105" height="852" alt="image" src="https://github.com/user-attachments/assets/ed9fa04c-4ede-407b-bb0d-41bdd5864c2f" />

Итог:

<img width="442" height="845" alt="image" src="https://github.com/user-attachments/assets/437a7bfe-58ad-48a4-b12f-4a8fc9ca9c7d" /> <img width="422" height="848" alt="image" src="https://github.com/user-attachments/assets/13f55e87-ffbf-4aca-8cad-8e890744988e" /> <img width="438" height="843" alt="image" src="https://github.com/user-attachments/assets/5bf67c31-c460-41a4-a6a0-30f4516ac7e9" />



    







