package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class LoginAsGuestUseCase {
    private final AuthRepository authRepository;

    public LoginAsGuestUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(AuthRepository.AuthCallback callback) {
        authRepository.loginAsGuest(callback);
    }
}