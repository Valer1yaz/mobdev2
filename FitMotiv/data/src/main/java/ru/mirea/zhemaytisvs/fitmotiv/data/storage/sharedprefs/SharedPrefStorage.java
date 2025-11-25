package ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs;

import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.UserGoalStorage;
import java.util.List;

public interface SharedPrefStorage {
    void saveUserGoal(UserGoalStorage goal, String userId);
    void saveUserGoals(List<UserGoalStorage> goals, String userId);
    List<UserGoalStorage> getUserGoals(String userId);
    void deleteUserGoal(String goalId, String userId);
}