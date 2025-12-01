package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class UpdateProfilePhotoUseCase {
    private final AuthRepository authRepository;

    public UpdateProfilePhotoUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String photoUrl, AuthRepository.AuthCallback callback) {
        authRepository.updateProfilePhoto(photoUrl, callback);
    }
}