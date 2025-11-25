package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.content.Context;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.UserRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorageImpl;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.UserGoalStorage;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private final SharedPrefStorage sharedPrefStorage;

    public UserRepositoryImpl() {
        // Для совместимости со старым кодом
        this.sharedPrefStorage = null;
    }

    public UserRepositoryImpl(Context context) {
        this.sharedPrefStorage = new SharedPrefStorageImpl(context);
    }

    @Override
    public void addUserGoal(UserGoal goal, String userId) {
        // Получаем текущий список целей
        List<UserGoal> goals = getUserGoals(userId);
        // Добавляем новую цель
        goals.add(goal);
        // Сохраняем обновленный список
        saveGoalsList(goals, userId);
    }

    @Override
    public List<UserGoal> getUserGoals(String userId) {
        if (sharedPrefStorage != null && userId != null && !userId.isEmpty()) {
            List<UserGoalStorage> storages = sharedPrefStorage.getUserGoals(userId);
            List<UserGoal> goals = new ArrayList<>();
            for (UserGoalStorage storage : storages) {
                goals.add(new UserGoal(
                        storage.getId(),
                        storage.getTargetWeight(),
                        storage.getWorkoutsPerWeek(),
                        storage.getDescription(),
                        storage.isCompleted()
                ));
            }
            return goals;
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteUserGoal(String goalId, String userId) {
        List<UserGoal> goals = getUserGoals(userId);
        goals.removeIf(goal -> goal.getId().equals(goalId));
        saveGoalsList(goals, userId);
    }

    private void saveGoalsList(List<UserGoal> goals, String userId) {
        if (sharedPrefStorage != null && userId != null && !userId.isEmpty()) {
            List<UserGoalStorage> storages = new ArrayList<>();
            for (UserGoal goal : goals) {
                storages.add(new UserGoalStorage(
                        goal.getId(),
                        goal.getTargetWeight(),
                        goal.getWorkoutsPerWeek(),
                        goal.getDescription(),
                        goal.isCompleted(),
                        System.currentTimeMillis()
                ));
            }
            sharedPrefStorage.saveUserGoals(storages, userId);
        }
    }

    @Override
    public boolean isUserLoggedIn() {
        return true; // Для тестирования всегда считаем пользователя авторизованным
    }
}