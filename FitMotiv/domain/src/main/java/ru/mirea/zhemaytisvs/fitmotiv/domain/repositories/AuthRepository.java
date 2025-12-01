package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;

public interface AuthRepository {
    void login(String email, String password, AuthCallback callback);
    void register(String email, String password, String displayName, AuthCallback callback);
    void loginAsGuest(AuthCallback callback);
    void logout();
    User getCurrentUser();
    boolean isUserLoggedIn();

    // Добавленный метод для обновления фото профиля
    void updateProfilePhoto(String photoUrl, AuthCallback callback);

    interface AuthCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }
}