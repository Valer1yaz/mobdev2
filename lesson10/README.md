Отчет по практической работе №2
----------------------------------------------
В ходе практической работы изучены основные различия моделей данных разных слоёв ПО. Рассмотрены принципы "чистой архитектуры".
Произведено ПОСТРОЕНИЕ МОДУЛЬНОГО ПРОЕКТА, а также для своего проекта сделаны макеты в фигме и созданы модули, активность для регистрации и входа, а также произведено подключение к файербейз

----------------------------------------------
Практика
----------------------------------------------

Задание №1
Было изменено приложение из практики 1 (lesson 9) по аналогии с примером, где проект разделён на модули

**Исправлена система хранения данных о фильмах с использованием SharedPreferences. Реализована двухслойная архитектура:**

Data-слой:
- Интерфейс MovieStorage для операций get/save;
- Реализация SharedPrefMovieStorage для работы с SharedPreferences;
- Модель Movie с полями id, name, localDate.

Domain-слой:
- Модель MovieDomain (переименована из Movie);
- Репозиторий MovieRepository с методами-мапперами для преобразования между data и domain моделями.

**Интеграция:**
- Создан экземпляр хранилища в MainActivity;
- Передан в репозиторий для связи слоев;
- Реализовано преобразование данных между слоями через мапперы.

**Архитектура: ** Проект разделен на модули (app, domain, data) с четким разделением ответственности между слоями.

<img width="376" height="499" alt="image" src="https://github.com/user-attachments/assets/21a90243-26e3-432e-a985-bb4e51e0ad15" />

<img width="436" height="456" alt="image" src="https://github.com/user-attachments/assets/b76cabd6-f8f0-4c26-a5b8-9dfc4bdf3e5c" />

<img width="418" height="366" alt="image" src="https://github.com/user-attachments/assets/22aaffa6-5ff0-4433-a0e0-c41239664aeb" />

работа приложения:
<img width="358" height="744" alt="image" src="https://github.com/user-attachments/assets/f37a654e-5121-4819-88fa-f9f1db4464fe" />

----------------------------------------------
Контрольное задание - проект Фитнес мотивация
----------------------------------------------

Задание №1. Нарисовать прототип приложения в figma

<img width="761" height="792" alt="image" src="https://github.com/user-attachments/assets/2382c611-afdd-455f-8d4f-b368a46a67c4" />

<img width="757" height="788" alt="image" src="https://github.com/user-attachments/assets/f90746b0-1154-449d-ba8c-7c229993d710" />

<img width="750" height="797" alt="image" src="https://github.com/user-attachments/assets/d8a07356-0a3b-4dcc-bacb-c85b04bdf48b" />
<img width="740" height="260" alt="image" src="https://github.com/user-attachments/assets/04204aef-54cd-4284-ab1f-7eaec50be974" />

Задание №2. Создать новые модули data и domain. Перенести соответствующий
код приложения в данные модули.

Модуль Domain:

· Создан Java-модуль без Android зависимостей
· Перенесены entity-классы: Workout, ProgressPhoto, UserGoal, ExerciseAnalysis, User
· Добавлены интерфейсы репозиториев: WorkoutRepository, ProgressRepository, QuoteRepository, UserRepository, AuthRepository
· Реализованы Use Cases: TrackWorkoutUseCase, GetWorkoutHistoryUseCase, AnalyzeExerciseUseCase, GetMotivationalQuoteUseCase, SetGoalUseCase, GetProgressPhotosUseCase

Модуль Data:

· Создан Android Library модуль
· Перенесены реализации репозиториев: WorkoutRepositoryImpl, ProgressRepositoryImpl, QuoteRepositoryImpl, UserRepositoryImpl, AuthRepositoryImpl
· Добавлены модели для хранения: WorkoutStorage, ProgressPhotoStorage, UserGoalStorage

<img width="503" height="646" alt="image" src="https://github.com/user-attachments/assets/629ab360-5ecd-490c-9ed1-105fd1037989" />

<img width="496" height="620" alt="image" src="https://github.com/user-attachments/assets/df3da719-d2de-41b0-bb6b-3bd5285a1d6b" />

<img width="512" height="760" alt="image" src="https://github.com/user-attachments/assets/395c52bf-5019-4477-af82-1fecc8fe9681" />


Задание №3. Создать новую активити и реализовать в ней страницу авторизации с
использованием firebase auth. Логику работы с FB распределить между тремя
модулями.

Presentation Layer (app):

· Создана LoginActivity с формами входа/регистрации
· Реализован UI с переключением между формами
· Добавлена кнопка "Продолжить как гость"

Domain Layer:

· Добавлены Use Cases: LoginUseCase, RegisterUseCase, LoginAsGuestUseCase, GetCurrentUserUseCase
· Создана entity User с поддержкой гостевого режима

Data Layer:

· Реализован AuthRepositoryImpl с интеграцией Firebase Auth
· Обработка callback-ов Firebase и преобразование в доменные модели

<img width="1862" height="881" alt="image" src="https://github.com/user-attachments/assets/155c30ee-085d-494c-a2a1-25f20ebdd96e" />

<img width="482" height="859" alt="image" src="https://github.com/user-attachments/assets/ae12290e-01d9-4046-881a-70119f69fd10" /> <img width="413" height="856" alt="image" src="https://github.com/user-attachments/assets/b650e635-cfa7-4b15-b716-94247776b969" />


Через форму была произведена регистрация тестового пользователя. Вот его данные из файербейз:
<img width="1321" height="570" alt="image" src="https://github.com/user-attachments/assets/d13b2790-4084-4a62-b778-bc077be2a8c8" />


Задание №4. В репозитории реализованы способы обработки данных:
- SharedPreferences, информация о клиенте

  Создан SharedPrefStorage интерфейс и SharedPrefStorageImpl
· Реализовано сохранение данных о тренировках, фото прогресса и целях пользователя
· Добавлены мапперы между domain и storage моделями

<img width="1410" height="695" alt="image" src="https://github.com/user-attachments/assets/e39041d1-fcd3-4f56-a0bb-108da58625eb" />

- Room и класс NetworkApi для работы с сетью с замоканными данными

Добавлены зависимости Room в data модуль
· Подготовлена структура для будущей реализации

NetworkApi с замоканными данными:

· Создан класс NetworkApi с mock-реализацией
· Реализованы методы: getMotivationalQuote(), getWorkoutsFromCloud(), syncWorkoutToCloud()
· Добавлены модели сетевых ответов: QuoteResponse, WorkoutResponse

<img width="1812" height="825" alt="image" src="https://github.com/user-attachments/assets/f1a46171-e1bb-4fd5-b2ce-e2b2b9af91d1" />

Также во время выполнения заданий были:
· Настроены зависимости между модулями в Gradle
· Добавлена проверка прав доступа для гостевых пользователей
· Реализован выход из аккаунта
· Обновлен MainActivity для отображения информации о пользователе


Приложение теперь соответствует требованиям "чистой архитектуры" с четким разделением ответственности между слоями.









