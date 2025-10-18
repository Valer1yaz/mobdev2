package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;

public interface UserRepository {
    void setUserGoal(UserGoal goal);
    UserGoal getUserGoal();
    boolean isUserLoggedIn();
}