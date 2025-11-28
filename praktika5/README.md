**Отчёт по практической работе №5**
-----------


**Цель работы**

Изучить работу с Retrofit и Picasso

-----------
**Практическая часть**


-----------
1 Retrofit.
-----------
реализовать приложение, позволяющее просматривать список дел и статус их выполнения.

Создан новый модуль «RetrofitApp». Создана модель данных Todo с аннотациями Gson для корректной сериализации JSON-ответов от сервера JSONPlaceholder. 
Далее настроен сам Retrofit клиент с базовым URL и Gson конвертером для автоматического преобразования данных. 
Реализован RecyclerView с адаптером для отображения списка задач, где каждый элемент содержит заголовок и статус выполнения.

<img width="417" height="848" alt="image" src="https://github.com/user-attachments/assets/dabc2438-4bac-4d35-968c-d948c01878d3" />

Добавлена функциональность обновления статуса выполнения задач через взаимодействие с элементами CheckBox. Реализованы PUT и PATCH методы в ApiService для отправки измененных данных на сервер с использованием аннотаций @Path и @Body.

<img width="888" height="567" alt="image" src="https://github.com/user-attachments/assets/f833f2d9-636e-4731-a499-3294fa098d5a" />


Интегрирован обработчик OnCheckedChangeListener, который при изменении состояния CheckBox инициирует асинхронный сетевой запрос через enqueue(). 

<img width="1154" height="885" alt="image" src="https://github.com/user-attachments/assets/8bd9f12f-0bc2-4506-81a6-91b800ddae1c" />


Настроена обработка ошибок с восстановлением предыдущего состояния интерфейса при неудачных запросах и отображением статусных уведомлений для пользователя. Отправка запроса при обновлении состояния CheckBox через методы onResponse и onFailure.

<img width="1166" height="938" alt="image" src="https://github.com/user-attachments/assets/f67d0e69-310f-4797-92c2-e3cbcf99659a" />


<img width="1588" height="181" alt="image" src="https://github.com/user-attachments/assets/bc7f6602-4d58-4c1f-89c5-61bdaa86c595" />

-----------
2 Picasso.
-----------

Для второго задания изменена разметка предыдущего задания и добавлены изображения с видами спорта и добавлено отображение с помощью Picasso:
Интегрирована библиотека Picasso для загрузки изображений из интернета.

<img width="1160" height="840" alt="image" src="https://github.com/user-attachments/assets/56020dbe-bfdc-4629-96ac-13c03e0bdf47" />


Настроены параметры отображения включая resize, centerCrop, а также placeholder и error изображения.
Создан массив URL-адресов графических ресурсов и реализован метод loadImageWithPicasso с настройкой параметров отображения через resize() и centerCrop().

<img width="1481" height="964" alt="image" src="https://github.com/user-attachments/assets/ccc96949-0a91-4404-98d5-0b90f64496bd" />



----------
*Доработка FitMotiv*
----------

Задание: Реализовать получение сущностей из сети с помощью Retrofit, обработать возможные ошибки, реализовать отображение изображений с помощью Picasso, Coil или Glide

Было реализовано получение данных о мотивационных цитатах из внешнего API с использованием Retrofit, настроен сетевой слой с обработкой ошибок и асинхронными запросами к API сервису

Интегрирована библиотека Glide для загрузки и отображения изображений различных видов тренировок в RecyclerView.
```
String imageName = workout.getImageName();
int resId = holder.itemView.getContext().getResources()
        .getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());

if (resId != 0) {
    Glide.with(holder.itemView.getContext())
            .load(resId)
            .placeholder(R.drawable.cat_default)
            .error(R.drawable.workout_default)
            .into(holder.workoutImage);
}
```

Добавлен функционал создания новых записей о тренировках.


