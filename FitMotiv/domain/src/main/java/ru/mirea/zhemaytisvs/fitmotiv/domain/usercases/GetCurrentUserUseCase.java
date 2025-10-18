package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class GetCurrentUserUseCase {
    private final AuthRepository authRepository;

    public GetCurrentUserUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User execute() {
        return authRepository.getCurrentUser();
    }
}