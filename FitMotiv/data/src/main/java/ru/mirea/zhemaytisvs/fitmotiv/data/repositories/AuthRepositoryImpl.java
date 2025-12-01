package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ru.mirea.zhemaytisvs.fitmotiv.data.managers.SessionManager;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    private static final String TAG = "AuthRepositoryImpl";
    private final FirebaseAuth firebaseAuth;
    private final SessionManager sessionManager;
    private User currentUser;

    public AuthRepositoryImpl(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.sessionManager = new SessionManager(context);
        restoreUserSession();
    }

    private void restoreUserSession() {
        Log.d(TAG, "Attempting to restore user session...");

        // Сначала проверяем Firebase Auth
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Получаем photoUrl из Firebase
            String photoUrl = null;
            if (firebaseUser.getPhotoUrl() != null) {
                photoUrl = firebaseUser.getPhotoUrl().toString();
            }

            // Создаем пользователя с photoUrl
            currentUser = new User(
                    firebaseUser.getUid(),
                    firebaseUser.getEmail(),
                    firebaseUser.getDisplayName(),
                    photoUrl,
                    false
            );

            // Сохраняем в сессию
            sessionManager.saveUserSession(currentUser);
            Log.d(TAG, "User restored from Firebase Auth: " + currentUser.getUid() +
                    ", photo: " + (photoUrl != null));
        } else if (sessionManager.hasSavedSession()) {
            // Восстанавливаем из сохраненной сессии
            currentUser = sessionManager.getSavedUser();
            if (currentUser != null) {
                if (currentUser.isGuest()) {
                    Log.d(TAG, "Guest user restored from saved session");
                } else {
                    Log.d(TAG, "User restored from saved session: " + currentUser.getUid() +
                            ", photo: " + (currentUser.getPhotoUrl() != null));
                }
            } else {
                Log.d(TAG, "Failed to restore user from saved session");
            }
        } else {
            Log.d(TAG, "No user session to restore");
        }
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Получаем photoUrl из Firebase
                            String photoUrl = null;
                            if (firebaseUser.getPhotoUrl() != null) {
                                photoUrl = firebaseUser.getPhotoUrl().toString();
                            }

                            currentUser = new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail(),
                                    firebaseUser.getDisplayName(),
                                    photoUrl,
                                    false
                            );

                            // Сохраняем сессию
                            sessionManager.saveUserSession(currentUser);
                            Log.d(TAG, "User logged in and session saved: " + currentUser.getEmail() +
                                    ", photo: " + (photoUrl != null));
                            callback.onSuccess(currentUser);
                        }
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Login failed";
                        Log.e(TAG, "Login error: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }

    @Override
    public void register(String email, String password, String displayName, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Update profile with display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Получаем обновленного пользователя
                                            String photoUrl = null;
                                            if (firebaseUser.getPhotoUrl() != null) {
                                                photoUrl = firebaseUser.getPhotoUrl().toString();
                                            }

                                            currentUser = new User(
                                                    firebaseUser.getUid(),
                                                    firebaseUser.getEmail(),
                                                    displayName,
                                                    photoUrl,
                                                    false
                                            );

                                            // Сохраняем сессию
                                            sessionManager.saveUserSession(currentUser);
                                            Log.d(TAG, "User registered and session saved: " + currentUser.getEmail() +
                                                    ", photo: " + (photoUrl != null));
                                            callback.onSuccess(currentUser);
                                        } else {
                                            callback.onError("Failed to set display name");
                                        }
                                    });
                        }
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Registration failed";
                        Log.e(TAG, "Registration error: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }

    @Override
    public void loginAsGuest(AuthCallback callback) {
        currentUser = User.createGuestUser();
        // Сохраняем гостевую сессию
        sessionManager.saveUserSession(currentUser);
        Log.d(TAG, "Guest user logged in and session saved");
        callback.onSuccess(currentUser);
    }

    @Override
    public void logout(LogoutCallback callback) {
        try {
            // Выходим из Firebase Auth
            firebaseAuth.signOut();
            // Очищаем сессию
            sessionManager.clearSession();
            currentUser = null;

            Log.d(TAG, "User logged out and session cleared");
            callback.onSuccess();

        } catch (Exception e) {
            String errorMessage = "Ошибка выхода: " + e.getMessage();
            Log.e(TAG, errorMessage, e);
            callback.onError(errorMessage);
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isUserLoggedIn() {
        return currentUser != null && !currentUser.isGuest();
    }

    @Override
    public void updateProfilePhoto(String photoUrl, AuthCallback callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            callback.onError("Пользователь не аутентифицирован");
            return;
        }

        try {
            // Проверяем, что photoUrl является валидным URL
            Uri photoUri = Uri.parse(photoUrl);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUri)
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Обновляем локального пользователя
                            if (currentUser != null) {
                                currentUser.setPhotoUrl(photoUrl);
                            }

                            // Обновляем сессию
                            sessionManager.updateUserPhoto(photoUrl);

                            Log.d(TAG, "Profile photo updated successfully: " + photoUrl);
                            callback.onSuccess(currentUser);
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Не удалось обновить фото профиля";
                            Log.e(TAG, "Failed to update profile photo: " + errorMessage);
                            callback.onError(errorMessage);
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Invalid photo URL: " + photoUrl, e);
            callback.onError("Неверный URL фотографии");
        }
    }
}