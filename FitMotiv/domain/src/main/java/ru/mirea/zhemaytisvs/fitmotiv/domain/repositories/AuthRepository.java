package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;

public interface AuthRepository {

    interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    interface LogoutCallback {
        void onSuccess();
        void onError(String message);
    }

    void login(String email, String password, AuthCallback callback);
    void register(String email, String password, String displayName, AuthCallback callback);
    void loginAsGuest(AuthCallback callback);
    void logout(LogoutCallback callback);
    User getCurrentUser();
    boolean isUserLoggedIn();
    void updateProfilePhoto(String photoUrl, AuthCallback callback);
}