package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import java.util.List;

public interface UserRepository {
    void addUserGoal(UserGoal goal, String userId);
    List<UserGoal> getUserGoals(String userId);
    void deleteUserGoal(String goalId, String userId);
    boolean isUserLoggedIn();
}