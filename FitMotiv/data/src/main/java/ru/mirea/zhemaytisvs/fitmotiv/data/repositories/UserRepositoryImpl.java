package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.UserRepository;

public class UserRepositoryImpl implements UserRepository {
    private UserGoal currentGoal;

    @Override
    public void setUserGoal(UserGoal goal) {
        this.currentGoal = goal;
    }

    @Override
    public UserGoal getUserGoal() {
        if (currentGoal == null) {
            // Возвращаем тестовую цель по умолчанию
            return new UserGoal("default", 75, 4, "Похудеть и набрать мышечную массу", false);
        }
        return currentGoal;
    }

    @Override
    public boolean isUserLoggedIn() {
        return true; // Для тестирования всегда считаем пользователя авторизованным
    }
}