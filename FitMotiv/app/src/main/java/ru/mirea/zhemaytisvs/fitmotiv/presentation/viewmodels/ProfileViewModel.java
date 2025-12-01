package ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.AuthRepositoryImpl;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.WorkoutRepositoryImpl;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.GetCurrentUserUseCase;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.GetWorkoutHistoryUseCase;

public class ProfileViewModel extends AndroidViewModel {

    // Use Cases
    private GetWorkoutHistoryUseCase getWorkoutHistoryUseCase;
    private GetCurrentUserUseCase getCurrentUserUseCase;

    // Repositories
    private AuthRepository authRepository;
    private WorkoutRepository workoutRepository;

    // Executor для фоновых операций
    private ExecutorService executorService;

    // LiveData
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<WorkoutStatistics> workoutStatisticsLiveData;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        executorService = Executors.newSingleThreadExecutor();

        // Инициализация репозиториев и Use Cases
        initializeRepositories(application);

        // Инициализация LiveData
        initializeLiveData();
    }

    private void initializeRepositories(Application application) {
        // Инициализация репозиториев
        workoutRepository = new WorkoutRepositoryImpl(application);
        authRepository = new AuthRepositoryImpl(application);

        // Инициализация Use Cases
        getWorkoutHistoryUseCase = new GetWorkoutHistoryUseCase(workoutRepository);
        getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
    }

    private void initializeLiveData() {
        userLiveData = new MutableLiveData<>();
        workoutStatisticsLiveData = new MutableLiveData<>();
    }

    /**
     * Загружает данные пользователя и его статистику тренировок
     */
    public void loadUserData() {
        executorService.execute(() -> {
            try {
                // Получаем текущего пользователя
                User user = getCurrentUserUseCase.execute();
                userLiveData.postValue(user);

                if (user != null && !user.isGuest()) {
                    // Загружаем статистику тренировок для аутентифицированного пользователя
                    loadWorkoutStatistics(user.getUid());
                } else {
                    // Для гостя устанавливаем нулевую статистику
                    workoutStatisticsLiveData.postValue(new WorkoutStatistics(0, 0));
                }
            } catch (Exception e) {
                Log.e("ProfileViewModel", "Error loading user data", e);
                userLiveData.postValue(User.createGuestUser());
                workoutStatisticsLiveData.postValue(new WorkoutStatistics(0, 0));
            }
        });
    }

    /**
     * Загружает статистику тренировок для указанного пользователя
     */
    private void loadWorkoutStatistics(String userId) {
        executorService.execute(() -> {
            try {
                Log.d("ProfileViewModel", "Loading workout statistics for user: " + userId);

                // Получаем историю тренировок через UseCase
                List<Workout> workouts = getWorkoutHistoryUseCase.execute(userId);
                Log.d("ProfileViewModel", "Found " + (workouts != null ? workouts.size() : 0) + " workouts");

                // Вычисляем статистику
                WorkoutStatistics stats = calculateStatistics(workouts != null ? workouts : java.util.Collections.emptyList());
                workoutStatisticsLiveData.postValue(stats);

            } catch (Exception e) {
                Log.e("ProfileViewModel", "Error loading workout statistics", e);
                workoutStatisticsLiveData.postValue(new WorkoutStatistics(0, 0));
            }
        });
    }

    /**
     * Вычисляет статистику тренировок на основе списка
     */
    private WorkoutStatistics calculateStatistics(List<Workout> workouts) {
        int totalWorkouts = workouts.size();
        int totalCalories = 0;

        for (Workout workout : workouts) {
            totalCalories += workout.getCalories();
        }

        return new WorkoutStatistics(totalWorkouts, totalCalories);
    }

    /**
     * Обновляет фото профиля пользователя
     */
    public void updateProfilePhoto(String photoUrl, AuthRepository.AuthCallback callback) {
        executorService.execute(() -> {
            try {
                if (authRepository != null) {
                    authRepository.updateProfilePhoto(photoUrl, new AuthRepository.AuthCallback() {
                        @Override
                        public void onSuccess(User user) {
                            // Обновляем данные пользователя в LiveData
                            userLiveData.postValue(user);
                            callback.onSuccess(user);
                        }

                        @Override
                        public void onError(String message) {
                            callback.onError(message);
                        }
                    });
                } else {
                    callback.onError("AuthRepository not initialized");
                }
            } catch (Exception e) {
                callback.onError("Error updating profile photo: " + e.getMessage());
            }
        });
    }

    /**
     * Выход из аккаунта
     */
    public void logout(AuthRepository.LogoutCallback callback) {
        executorService.execute(() -> {
            try {
                if (authRepository != null) {
                    authRepository.logout(callback);
                } else {
                    callback.onError("AuthRepository not initialized");
                }
            } catch (Exception e) {
                Log.e("ProfileViewModel", "Error during logout", e);
                callback.onError("Ошибка выхода: " + e.getMessage());
            }
        });
    }

    /**
     * Обновляет данные пользователя и статистику (например, при возвращении на экран)
     */
    public void refreshData() {
        loadUserData();
    }

    // Getters для LiveData

    public MutableLiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<WorkoutStatistics> getWorkoutStatisticsLiveData() {
        return workoutStatisticsLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Класс статистики тренировок (переиспользован из MainViewModel)
     */
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

        @Override
        public String toString() {
            return "WorkoutStatistics{" +
                    "totalWorkouts=" + totalWorkouts +
                    ", totalCalories=" + totalCalories +
                    '}';
        }
    }
}