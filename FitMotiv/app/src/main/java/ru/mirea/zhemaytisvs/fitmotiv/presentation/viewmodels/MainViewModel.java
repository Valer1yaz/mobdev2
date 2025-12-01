package ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.*;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.*;

public class MainViewModel extends AndroidViewModel {

    // Use Cases
    private TrackWorkoutUseCase trackWorkoutUseCase;
    private GetWorkoutHistoryUseCase getWorkoutHistoryUseCase;
    private DeleteWorkoutUseCase deleteWorkoutUseCase;
    private GetMotivationalQuoteUseCase getMotivationalQuoteUseCase;
    private GetCurrentUserUseCase getCurrentUserUseCase;

    // Executor для фоновых операций
    private ExecutorService executorService;

    // LiveData для цитат
    private MutableLiveData<String> quoteLiveData;

    // LiveData для тренировок
    private MutableLiveData<List<Workout>> workoutsLiveData;

    // LiveData для пользователя
    private MutableLiveData<User> currentUserLiveData;

    // LiveData для статистики тренировок
    private MutableLiveData<WorkoutStatistics> workoutStatisticsLiveData;

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
        AuthRepository authRepository = new AuthRepositoryImpl();

        trackWorkoutUseCase = new TrackWorkoutUseCase(workoutRepository);
        getWorkoutHistoryUseCase = new GetWorkoutHistoryUseCase(workoutRepository);
        deleteWorkoutUseCase = new DeleteWorkoutUseCase(workoutRepository);
        getMotivationalQuoteUseCase = new GetMotivationalQuoteUseCase(quoteRepository);
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
    }

    private void initializeLiveData() {
        quoteLiveData = new MutableLiveData<>();
        workoutsLiveData = new MutableLiveData<>();
        currentUserLiveData = new MutableLiveData<>();
        workoutStatisticsLiveData = new MutableLiveData<>();

        // Инициализируем пустым списком
        workoutsLiveData.setValue(new ArrayList<>());
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
        Log.d("MainViewModel", "=== LOAD WORKOUTS CALLED ===");

        User currentUser = currentUserLiveData.getValue();
        Log.d("MainViewModel", "Current user: " + (currentUser != null ? currentUser.getUid() : "null"));

        if (currentUser == null || currentUser.isGuest()) {
            Log.d("MainViewModel", "User is null or guest - returning empty list");
            workoutsLiveData.postValue(new ArrayList<>());
            return;
        }

        String userId = currentUser.getUid();
        Log.d("MainViewModel", "Loading workouts for user: " + userId);

        executorService.execute(() -> {
            Log.d("MainViewModel", "Starting background load for user: " + userId);
            try {
                List<Workout> workouts = getWorkoutHistoryUseCase.execute(userId);
                Log.d("MainViewModel", "UseCase returned " + (workouts != null ? workouts.size() : 0) + " workouts");

                // ИСПРАВЛЕНО: Убрал runOnUiThread - postValue() уже потокобезопасен
                workoutsLiveData.postValue(workouts != null ? workouts : new ArrayList<>());
                updateWorkoutStatistics(workouts != null ? workouts : new ArrayList<>());

            } catch (Exception e) {
                Log.e("MainViewModel", "Error in loadWorkouts", e);
                workoutsLiveData.postValue(new ArrayList<>());
                updateWorkoutStatistics(new ArrayList<>());
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
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    Log.e("MainViewModel", "Cannot add workout: user is null or guest");
                    return; // Гости не могут добавлять тренировки
                }

                String userId = currentUser.getUid();
                if (userId == null || userId.isEmpty()) {
                    Log.e("MainViewModel", "Cannot add workout: userId is null or empty");
                    return;
                }

                trackWorkoutUseCase.execute(workout, userId);
                Log.d("MainViewModel", "Workout saved for user: " + userId);

                // Обновляем список тренировок
                loadWorkouts();
            } catch (Exception e) {
                Log.e("MainViewModel", "Error adding workout", e);
            }
        });
    }

    public void deleteWorkout(String workoutId) {
        executorService.execute(() -> {
            try {
                // Получаем userId текущего пользователя
                User currentUser = currentUserLiveData.getValue();
                if (currentUser == null || currentUser.isGuest()) {
                    Log.e("MainViewModel", "Cannot delete workout: user is null or guest");
                    return; // Гости не могут удалять тренировки
                }

                String userId = currentUser.getUid();
                if (userId == null || userId.isEmpty()) {
                    Log.e("MainViewModel", "Cannot delete workout: userId is null or empty");
                    return;
                }

                deleteWorkoutUseCase.execute(workoutId, userId);
                Log.d("MainViewModel", "Workout deleted for user: " + userId);

                // Обновляем список тренировок
                loadWorkouts();
            } catch (Exception e) {
                Log.e("MainViewModel", "Error deleting workout", e);
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

    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public LiveData<WorkoutStatistics> getWorkoutStatisticsLiveData() {
        return workoutStatisticsLiveData;
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