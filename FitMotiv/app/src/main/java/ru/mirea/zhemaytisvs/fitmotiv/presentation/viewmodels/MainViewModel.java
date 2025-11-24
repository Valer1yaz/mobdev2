package ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ExerciseAnalysis;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.UserRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.*;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.*;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.NetworkApi;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.WorkoutResponse;

public class MainViewModel extends AndroidViewModel {
    
    // Use Cases
    private TrackWorkoutUseCase trackWorkoutUseCase;
    private GetWorkoutHistoryUseCase getWorkoutHistoryUseCase;
    private GetMotivationalQuoteUseCase getMotivationalQuoteUseCase;
    private SetGoalUseCase setGoalUseCase;
    private GetProgressPhotosUseCase getProgressPhotosUseCase;
    private GetCurrentUserUseCase getCurrentUserUseCase;
    private AnalyzeExerciseUseCase analyzeExerciseUseCase;
    
    // Network API для получения данных из сети
    private NetworkApi networkApi;
    
    // Executor для фоновых операций
    private ExecutorService executorService;
    
    // LiveData для цитат
    private MutableLiveData<String> quoteLiveData;
    
    // MediatorLiveData для тренировок (объединяет данные из БД и сети)
    private MediatorLiveData<List<Workout>> workoutsMediatorLiveData;
    private MutableLiveData<List<Workout>> workoutsFromDbLiveData;
    private MutableLiveData<List<Workout>> workoutsFromNetworkLiveData;
    
    // LiveData для целей
    private MutableLiveData<UserGoal> goalLiveData;
    
    // LiveData для фото прогресса
    private MutableLiveData<List<ProgressPhoto>> progressPhotosLiveData;
    
    // LiveData для пользователя
    private MutableLiveData<User> currentUserLiveData;
    
    // LiveData для статистики тренировок
    private MutableLiveData<WorkoutStatistics> workoutStatisticsLiveData;
    
    // LiveData для анализа упражнений
    private MutableLiveData<ExerciseAnalysis> exerciseAnalysisLiveData;
    
    public MainViewModel(@NonNull Application application) {
        super(application);
        
        executorService = Executors.newSingleThreadExecutor();
        
        // Инициализация репозиториев и Use Cases
        initializeRepositories(application);
        
        // Инициализация LiveData
        initializeLiveData();
        
        // Настройка MediatorLiveData для тренировок
        setupWorkoutsMediator();
    }
    
    private void initializeRepositories(Application application) {
        // Используем Room Database вместо SharedPrefs для тренировок
        WorkoutRepository workoutRepository = new WorkoutRepositoryImpl(application);
        QuoteRepository quoteRepository = new QuoteRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        ProgressRepository progressRepository = new ProgressRepositoryImpl();
        AuthRepository authRepository = new AuthRepositoryImpl();
        
        networkApi = new NetworkApi();
        
        trackWorkoutUseCase = new TrackWorkoutUseCase(workoutRepository);
        getWorkoutHistoryUseCase = new GetWorkoutHistoryUseCase(workoutRepository);
        getMotivationalQuoteUseCase = new GetMotivationalQuoteUseCase(quoteRepository);
        setGoalUseCase = new SetGoalUseCase(userRepository);
        getProgressPhotosUseCase = new GetProgressPhotosUseCase(progressRepository);
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        analyzeExerciseUseCase = new AnalyzeExerciseUseCase(workoutRepository);
    }
    
    private void initializeLiveData() {
        quoteLiveData = new MutableLiveData<>();
        workoutsFromDbLiveData = new MutableLiveData<>();
        workoutsFromNetworkLiveData = new MutableLiveData<>();
        workoutsMediatorLiveData = new MediatorLiveData<>();
        goalLiveData = new MutableLiveData<>();
        progressPhotosLiveData = new MutableLiveData<>();
        currentUserLiveData = new MutableLiveData<>();
        workoutStatisticsLiveData = new MutableLiveData<>();
        exerciseAnalysisLiveData = new MutableLiveData<>();
    }
    
    private void setupWorkoutsMediator() {
        // MediatorLiveData объединяет данные из БД и сети
        workoutsMediatorLiveData.addSource(workoutsFromDbLiveData, dbWorkouts -> {
            combineWorkouts();
        });
        
        workoutsMediatorLiveData.addSource(workoutsFromNetworkLiveData, networkWorkouts -> {
            combineWorkouts();
        });
    }
    
    private void combineWorkouts() {
        List<Workout> dbWorkouts = workoutsFromDbLiveData.getValue();
        List<Workout> networkWorkouts = workoutsFromNetworkLiveData.getValue();
        
        if (dbWorkouts == null) dbWorkouts = new ArrayList<>();
        if (networkWorkouts == null) networkWorkouts = new ArrayList<>();
        
        // Объединяем тренировки, избегая дубликатов по ID
        List<Workout> combinedWorkouts = new ArrayList<>(dbWorkouts);
        
        for (Workout networkWorkout : networkWorkouts) {
            boolean exists = false;
            for (Workout dbWorkout : combinedWorkouts) {
                if (dbWorkout.getId().equals(networkWorkout.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                combinedWorkouts.add(networkWorkout);
            }
        }
        
        workoutsMediatorLiveData.setValue(combinedWorkouts);
        updateWorkoutStatistics(combinedWorkouts);
    }
    
    // Методы для загрузки данных
    
    public void loadMotivationalQuote() {
        executorService.execute(() -> {
            try {
                String quote = getMotivationalQuoteUseCase.execute();
                quoteLiveData.postValue(quote);
            } catch (Exception e) {
                quoteLiveData.postValue("Великие дела требуют великих усилий!");
            }
        });
    }
    
    public void loadWorkouts() {
        // Оптимизация: сначала загружаем из БД (быстро), затем из сети (медленно)
        executorService.execute(() -> {
            try {
                List<Workout> workouts = getWorkoutHistoryUseCase.execute();
                workoutsFromDbLiveData.postValue(workouts);
            } catch (Exception e) {
                workoutsFromDbLiveData.postValue(new ArrayList<>());
            }
        });
        
        // Загружаем тренировки из сети в фоне (не блокирует UI)
        executorService.execute(() -> {
            try {
                List<WorkoutResponse> networkWorkouts = networkApi.getWorkoutsFromCloud();
                List<Workout> domainWorkouts = convertToDomainWorkouts(networkWorkouts);
                workoutsFromNetworkLiveData.postValue(domainWorkouts);
            } catch (Exception e) {
                // При ошибке сети просто не обновляем данные из сети
                workoutsFromNetworkLiveData.postValue(new ArrayList<>());
            }
        });
    }
    
    private List<Workout> convertToDomainWorkouts(List<WorkoutResponse> networkWorkouts) {
        List<Workout> domainWorkouts = new ArrayList<>();
        for (WorkoutResponse response : networkWorkouts) {
            Workout.WorkoutType type = Workout.WorkoutType.valueOf(response.getType());
            Workout workout = new Workout(
                    response.getId(),
                    type,
                    response.getDuration(),
                    response.getCalories(),
                    response.getDate(),
                    response.getDescription()
            );
            domainWorkouts.add(workout);
        }
        return domainWorkouts;
    }
    
    public void loadProgressPhotos() {
        executorService.execute(() -> {
            try {
                List<ProgressPhoto> photos = getProgressPhotosUseCase.execute();
                progressPhotosLiveData.postValue(photos);
            } catch (Exception e) {
                progressPhotosLiveData.postValue(new ArrayList<>());
            }
        });
    }
    
    public void loadGoal() {
        executorService.execute(() -> {
            try {
                // Для демонстрации создаем тестовую цель
                // В реальном приложении здесь будет загрузка из репозитория
                UserGoal goal = new UserGoal("1", 75, 4, "Похудеть на 5 кг за месяц", false);
                goalLiveData.postValue(goal);
            } catch (Exception e) {
                goalLiveData.postValue(null);
            }
        });
    }
    
    public void loadCurrentUser() {
        executorService.execute(() -> {
            try {
                User user = getCurrentUserUseCase.execute();
                currentUserLiveData.postValue(user);
            } catch (Exception e) {
                currentUserLiveData.postValue(null);
            }
        });
    }
    
    private void updateWorkoutStatistics(List<Workout> workouts) {
        int totalWorkouts = workouts.size();
        int totalCalories = 0;
        
        for (Workout workout : workouts) {
            totalCalories += workout.getCalories();
        }
        
        WorkoutStatistics stats = new WorkoutStatistics(totalWorkouts, totalCalories);
        workoutStatisticsLiveData.postValue(stats);
    }
    
    // Методы для действий пользователя
    
    public void addWorkout(Workout workout) {
        executorService.execute(() -> {
            try {
                trackWorkoutUseCase.execute(workout);
                // Обновляем список тренировок
                loadWorkouts();
            } catch (Exception e) {
                // Обработка ошибки
            }
        });
    }
    
    public void setGoal(UserGoal goal) {
        executorService.execute(() -> {
            try {
                setGoalUseCase.execute(goal);
                goalLiveData.postValue(goal);
            } catch (Exception e) {
                // Обработка ошибки
            }
        });
    }
    
    public void analyzeExercise(String exerciseName, byte[] imageData) {
        executorService.execute(() -> {
            try {
                ExerciseAnalysis analysis = analyzeExerciseUseCase.execute(exerciseName, imageData);
                exerciseAnalysisLiveData.postValue(analysis);
            } catch (Exception e) {
                exerciseAnalysisLiveData.postValue(null);
            }
        });
    }
    
    // Getters для LiveData
    
    public LiveData<String> getQuoteLiveData() {
        return quoteLiveData;
    }
    
    public LiveData<List<Workout>> getWorkoutsLiveData() {
        return workoutsMediatorLiveData;
    }
    
    public LiveData<UserGoal> getGoalLiveData() {
        return goalLiveData;
    }
    
    public LiveData<List<ProgressPhoto>> getProgressPhotosLiveData() {
        return progressPhotosLiveData;
    }
    
    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }
    
    public LiveData<WorkoutStatistics> getWorkoutStatisticsLiveData() {
        return workoutStatisticsLiveData;
    }
    
    public LiveData<ExerciseAnalysis> getExerciseAnalysisLiveData() {
        return exerciseAnalysisLiveData;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    // Вспомогательный класс для статистики тренировок
    public static class WorkoutStatistics {
        private int totalWorkouts;
        private int totalCalories;
        
        public WorkoutStatistics(int totalWorkouts, int totalCalories) {
            this.totalWorkouts = totalWorkouts;
            this.totalCalories = totalCalories;
        }
        
        public int getTotalWorkouts() {
            return totalWorkouts;
        }
        
        public int getTotalCalories() {
            return totalCalories;
        }
    }
}

