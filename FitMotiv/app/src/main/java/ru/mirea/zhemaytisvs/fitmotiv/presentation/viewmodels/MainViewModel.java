package ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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

public class MainViewModel extends AndroidViewModel {
    
    // Use Cases
    private TrackWorkoutUseCase trackWorkoutUseCase;
    private GetWorkoutHistoryUseCase getWorkoutHistoryUseCase;
    private GetMotivationalQuoteUseCase getMotivationalQuoteUseCase;
    private SetGoalUseCase setGoalUseCase;
    private GetProgressPhotosUseCase getProgressPhotosUseCase;
    private GetCurrentUserUseCase getCurrentUserUseCase;
    private AnalyzeExerciseUseCase analyzeExerciseUseCase;
    
    // Executor для фоновых операций
    private ExecutorService executorService;
    
    // LiveData для цитат
    private MutableLiveData<String> quoteLiveData;
    
    // LiveData для тренировок
    private MutableLiveData<List<Workout>> workoutsLiveData;
    
    // LiveData для целей (список)
    private MutableLiveData<List<UserGoal>> goalsLiveData;
    
    // LiveData для фото прогресса
    private MutableLiveData<List<ProgressPhoto>> progressPhotosLiveData;
    
    // LiveData для пользователя
    private MutableLiveData<User> currentUserLiveData;
    
    // LiveData для статистики тренировок
    private MutableLiveData<WorkoutStatistics> workoutStatisticsLiveData;
    
    // LiveData для анализа упражнений
    private MutableLiveData<ExerciseAnalysis> exerciseAnalysisLiveData;
    private UserRepository userRepository;
    private ProgressRepository progressRepository;
    
    public MainViewModel(@NonNull Application application) {
        super(application);
        
        executorService = Executors.newSingleThreadExecutor();
        
        // Инициализация репозиториев и Use Cases
        initializeRepositories(application);
        
        // Инициализация LiveData
        initializeLiveData();
    }
    
    private void initializeRepositories(Application application) {
        // Используем Room Database вместо SharedPrefs для тренировок
        WorkoutRepository workoutRepository = new WorkoutRepositoryImpl(application);
        QuoteRepository quoteRepository = new QuoteRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl(application);
        ProgressRepository progressRepository = new ProgressRepositoryImpl(application);
        AuthRepository authRepository = new AuthRepositoryImpl();
        
        trackWorkoutUseCase = new TrackWorkoutUseCase(workoutRepository);
        getWorkoutHistoryUseCase = new GetWorkoutHistoryUseCase(workoutRepository);
        getMotivationalQuoteUseCase = new GetMotivationalQuoteUseCase(quoteRepository);
        setGoalUseCase = new SetGoalUseCase(userRepository);
        getProgressPhotosUseCase = new GetProgressPhotosUseCase(progressRepository);
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        analyzeExerciseUseCase = new AnalyzeExerciseUseCase();
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }
    
    private void initializeLiveData() {
        quoteLiveData = new MutableLiveData<>();
        workoutsLiveData = new MutableLiveData<>();
        goalsLiveData = new MutableLiveData<>();
        progressPhotosLiveData = new MutableLiveData<>();
        currentUserLiveData = new MutableLiveData<>();
        workoutStatisticsLiveData = new MutableLiveData<>();
        exerciseAnalysisLiveData = new MutableLiveData<>();
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
        // Получаем userId текущего пользователя
        User currentUser = currentUserLiveData.getValue();
        if (currentUser == null || currentUser.isGuest()) {
            // Для гостей не загружаем тренировки
            workoutsLiveData.postValue(new ArrayList<>());
            return;
        }
        
        String userId = currentUser.getUid();
        
        // Загружаем тренировки из БД
        executorService.execute(() -> {
            try {
                List<Workout> workouts = getWorkoutHistoryUseCase.execute(userId);
                workoutsLiveData.postValue(workouts != null ? workouts : new ArrayList<>());
                updateWorkoutStatistics(workouts != null ? workouts : new ArrayList<>());
            } catch (Exception e) {
                // Обработка ошибки БД - отправляем пустой список
                workoutsLiveData.postValue(new ArrayList<>());
                updateWorkoutStatistics(new ArrayList<>());
            }
        });
    }
    
    public void loadProgressPhotos() {
        executorService.execute(() -> {
            try {
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    progressPhotosLiveData.postValue(new ArrayList<>());
                    return;
                }
                
                String userId = currentUser.getUid();
                if (userId == null || userId.isEmpty()) {
                    android.util.Log.e("MainViewModel", "Cannot load photos: userId is null or empty");
                    progressPhotosLiveData.postValue(new ArrayList<>());
                    return;
                }
                
                List<ProgressPhoto> photos = progressRepository.getProgressPhotos(userId);
                android.util.Log.d("MainViewModel", "Loaded " + (photos != null ? photos.size() : 0) + " photos for user: " + userId);
                progressPhotosLiveData.postValue(photos != null ? photos : new ArrayList<>());
            } catch (Exception e) {
                android.util.Log.e("MainViewModel", "Error loading photos", e);
                progressPhotosLiveData.postValue(new ArrayList<>());
            }
        });
    }
    
    public void addProgressPhoto(ProgressPhoto photo) {
        executorService.execute(() -> {
            try {
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    android.util.Log.e("MainViewModel", "Cannot add photo: user is null or guest");
                    return; // Гости не могут добавлять фото
                }
                
                String userId = currentUser.getUid();
                if (userId == null || userId.isEmpty()) {
                    android.util.Log.e("MainViewModel", "Cannot add photo: userId is null or empty");
                    return;
                }
                
                progressRepository.saveProgressPhoto(photo, userId);
                android.util.Log.d("MainViewModel", "Photo saved for user: " + userId);
                
                // Обновляем список фото
                loadProgressPhotos();
            } catch (Exception e) {
                android.util.Log.e("MainViewModel", "Error adding photo", e);
            }
        });
    }
    
    public void loadGoals() {
        executorService.execute(() -> {
            try {
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    goalsLiveData.postValue(new ArrayList<>());
                    return;
                }
                
                String userId = currentUser.getUid();
                // Загружаем список целей из репозитория для этого пользователя
                List<UserGoal> goals = userRepository.getUserGoals(userId);
                goalsLiveData.postValue(goals != null ? goals : new ArrayList<>());
            } catch (Exception e) {
                goalsLiveData.postValue(new ArrayList<>());
            }
        });
    }
    
    // Метод для проверки выполнения целей
    public void checkGoalProgress() {
        executorService.execute(() -> {
            try {
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    return;
                }
                
                String userId = currentUser.getUid();
                List<UserGoal> goals = userRepository.getUserGoals(userId);
                // Получаем тренировки за текущую неделю для этого пользователя
                List<Workout> workouts = getWorkoutHistoryUseCase.execute(userId);
                int workoutsThisWeek = countWorkoutsThisWeek(workouts);
                
                boolean updated = false;
                for (UserGoal goal : goals) {
                    if (!goal.isCompleted() && workoutsThisWeek >= goal.getWorkoutsPerWeek()) {
                        // Цель выполнена
                        UserGoal completedGoal = new UserGoal(
                                goal.getId(),
                                goal.getTargetWeight(),
                                goal.getWorkoutsPerWeek(),
                                goal.getDescription(),
                                true
                        );
                        // Обновляем цель в списке
                        goals.set(goals.indexOf(goal), completedGoal);
                        updated = true;
                    }
                }
                
                if (updated) {
                    // Сохраняем обновленный список
                    for (UserGoal goal : goals) {
                        userRepository.addUserGoal(goal, userId);
                    }
                    goalsLiveData.postValue(goals);
                }
            } catch (Exception e) {
                // Обработка ошибки
            }
        });
    }
    
    private int countWorkoutsThisWeek(List<Workout> workouts) {
        if (workouts == null) return 0;
        
        long now = System.currentTimeMillis();
        long weekAgo = now - (7 * 24 * 60 * 60 * 1000L);
        
        int count = 0;
        for (Workout workout : workouts) {
            if (workout.getDate().getTime() >= weekAgo) {
                count++;
            }
        }
        return count;
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
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    android.util.Log.e("MainViewModel", "Cannot add workout: user is null or guest");
                    return; // Гости не могут добавлять тренировки
                }
                
                String userId = currentUser.getUid();
                if (userId == null || userId.isEmpty()) {
                    android.util.Log.e("MainViewModel", "Cannot add workout: userId is null or empty");
                    return;
                }
                
                trackWorkoutUseCase.execute(workout, userId);
                android.util.Log.d("MainViewModel", "Workout saved for user: " + userId);
                
                // Обновляем список тренировок
                loadWorkouts();
                // Проверяем прогресс выполнения цели после добавления тренировки
                checkGoalProgress();
            } catch (Exception e) {
                android.util.Log.e("MainViewModel", "Error adding workout", e);
            }
        });
    }
    
    public void addGoal(UserGoal goal) {
        executorService.execute(() -> {
            try {
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    android.util.Log.e("MainViewModel", "Cannot add goal: user is null or guest");
                    return; // Гости не могут устанавливать цели
                }
                
                String userId = currentUser.getUid();
                if (userId == null || userId.isEmpty()) {
                    android.util.Log.e("MainViewModel", "Cannot add goal: userId is null or empty");
                    return;
                }
                
                setGoalUseCase.execute(goal, userId);
                android.util.Log.d("MainViewModel", "Goal saved for user: " + userId);
                
                // Обновляем список целей
                loadGoals();
            } catch (Exception e) {
                android.util.Log.e("MainViewModel", "Error adding goal", e);
            }
        });
    }
    
    public void deleteGoal(String goalId) {
        executorService.execute(() -> {
            try {
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    return;
                }
                
                String userId = currentUser.getUid();
                userRepository.deleteUserGoal(goalId, userId);
                loadGoals();
            } catch (Exception e) {
                // Обработка ошибки
            }
        });
    }
    
    public void analyzeExercise(String exerciseName, byte[] imageData) {
        executorService.execute(() -> {
            try {
                // Используем TensorFlow Lite для анализа изображения
                ru.mirea.zhemaytisvs.fitmotiv.presentation.ml.TensorFlowLiteImageClassifier classifier = 
                        new ru.mirea.zhemaytisvs.fitmotiv.presentation.ml.TensorFlowLiteImageClassifier(getApplication());
                float mlScore = classifier.analyzeExercise(imageData, exerciseName);
                classifier.close();
                
                // Передаем оценку в UseCase
                ExerciseAnalysis analysis = analyzeExerciseUseCase.execute(exerciseName, imageData, mlScore);
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
        return workoutsLiveData;
    }
    
    public LiveData<List<UserGoal>> getGoalsLiveData() {
        return goalsLiveData;
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

