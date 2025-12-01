package ru.mirea.zhemaytisvs.fitmotiv.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;

public class SessionManager {
    private static final String TAG = "SessionManager";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "FitMotivSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_DISPLAY_NAME = "userDisplayName";
    private static final String KEY_PHOTO_URL = "photoUrl"; // Добавлено
    private static final String KEY_IS_GUEST = "isGuest";

    private Context context;
    private int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Сохраняет данные пользователя с photoUrl
     */
    public void saveUserSession(User user) {
        Log.d(TAG, "Saving user session: " + user.getUid());

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getUid());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_DISPLAY_NAME, user.getDisplayName());
        editor.putString(KEY_PHOTO_URL, user.getPhotoUrl()); // Сохраняем photoUrl
        editor.putBoolean(KEY_IS_GUEST, user.isGuest());

        editor.apply();
        Log.d(TAG, "User session saved successfully with photo: " + (user.getPhotoUrl() != null));
    }

    /**
     * Восстанавливает пользователя из сохраненной сессии с photoUrl
     */
    public User getSavedUser() {
        if (!pref.getBoolean(KEY_IS_LOGGED_IN, false)) {
            Log.d(TAG, "No saved user session found");
            return null;
        }

        String userId = pref.getString(KEY_USER_ID, null);
        String email = pref.getString(KEY_USER_EMAIL, null);
        String displayName = pref.getString(KEY_USER_DISPLAY_NAME, null);
        String photoUrl = pref.getString(KEY_PHOTO_URL, null); // Получаем photoUrl
        boolean isGuest = pref.getBoolean(KEY_IS_GUEST, true);

        if (userId == null) {
            Log.d(TAG, "Saved user ID is null");
            return null;
        }

        Log.d(TAG, "Restoring user from session: " + userId + ", photo: " + (photoUrl != null));

        // Используем конструктор с photoUrl
        return new User(userId, email, displayName, photoUrl, isGuest);
    }

    /**
     * Очищает сессию при выходе
     */
    public void clearSession() {
        Log.d(TAG, "Clearing user session");
        editor.clear();
        editor.apply();
    }

    /**
     * Проверяет, есть ли сохраненная сессия
     */
    public boolean hasSavedSession() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Обновляет только photoUrl пользователя
     */
    public void updateUserPhoto(String photoUrl) {
        if (pref.getBoolean(KEY_IS_LOGGED_IN, false)) {
            editor.putString(KEY_PHOTO_URL, photoUrl);
            editor.apply();
            Log.d(TAG, "Updated user photo URL: " + photoUrl);
        }
    }

    /**
     * Проверяет, вошел ли пользователь (не гость)
     */
    public boolean isUserLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false) &&
                !pref.getBoolean(KEY_IS_GUEST, true);
    }

    /**
     * Получает только photoUrl из сессии
     */
    public String getSavedPhotoUrl() {
        return pref.getString(KEY_PHOTO_URL, null);
    }

    /**
     * Обновляет displayName пользователя
     */
    public void updateUserDisplayName(String displayName) {
        if (pref.getBoolean(KEY_IS_LOGGED_IN, false)) {
            editor.putString(KEY_USER_DISPLAY_NAME, displayName);
            editor.apply();
            Log.d(TAG, "Updated user display name: " + displayName);
        }
    }
}