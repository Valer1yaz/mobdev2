package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.UserRepository;

public class SetGoalUseCase {
    private final UserRepository userRepository;

    public SetGoalUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(UserGoal goal, String userId) {
        userRepository.addUserGoal(goal, userId);
    }
}