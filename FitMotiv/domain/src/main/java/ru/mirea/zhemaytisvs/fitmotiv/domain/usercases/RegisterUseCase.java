package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class RegisterUseCase {
    private final AuthRepository authRepository;

    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, String displayName, AuthRepository.AuthCallback callback) {
        authRepository.register(email, password, displayName, callback);
    }
}