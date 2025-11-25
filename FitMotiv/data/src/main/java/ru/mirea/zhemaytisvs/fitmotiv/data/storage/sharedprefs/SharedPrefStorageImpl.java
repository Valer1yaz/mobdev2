package ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.UserGoalStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.ProgressPhotoStorage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SharedPrefStorageImpl implements SharedPrefStorage {
    private static final String TAG = "SharedPrefStorage";
    private static final String SHARED_PREFS_NAME = "fitness_motivator_prefs";

    private static final String KEY_GOAL_ID = "goal_id";
    private static final String KEY_GOAL_WEIGHT = "goal_weight";
    private static final String KEY_GOAL_WORKOUTS = "goal_workouts";
    private static final String KEY_GOAL_DESCRIPTION = "goal_description";
    private static final String KEY_GOAL_COMPLETED = "goal_completed";
    private static final String KEY_GOAL_SAVED_AT = "goal_saved_at";

    private final SharedPreferences sharedPreferences;

    public SharedPrefStorageImpl(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveUserGoal(UserGoalStorage goal, String userId) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Используем userId в ключах для разделения целей разных пользователей
            String prefix = "user_" + userId + "_";
            editor.putString(prefix + KEY_GOAL_ID, goal.getId());
            editor.putInt(prefix + KEY_GOAL_WEIGHT, goal.getTargetWeight());
            editor.putInt(prefix + KEY_GOAL_WORKOUTS, goal.getWorkoutsPerWeek());
            editor.putString(prefix + KEY_GOAL_DESCRIPTION, goal.getDescription());
            editor.putBoolean(prefix + KEY_GOAL_COMPLETED, goal.isCompleted());
            editor.putLong(prefix + KEY_GOAL_SAVED_AT, System.currentTimeMillis());
            editor.apply();
            Log.d(TAG, "User goal saved to SharedPreferences for user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user goal to SharedPreferences", e);
        }
    }

    @Override
    public void saveUserGoals(List<UserGoalStorage> goals, String userId) {
        try {
            String prefix = "user_" + userId + "_goals";
            Gson gson = new Gson();
            String json = gson.toJson(goals);
            sharedPreferences.edit().putString(prefix, json).apply();
            Log.d(TAG, "User goals saved to SharedPreferences for user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user goals to SharedPreferences", e);
        }
    }

    @Override
    public List<UserGoalStorage> getUserGoals(String userId) {
        try {
            String prefix = "user_" + userId + "_goals";
            String json = sharedPreferences.getString(prefix, null);
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<UserGoalStorage>>(){}.getType();
            List<UserGoalStorage> goals = gson.fromJson(json, type);
            return goals != null ? goals : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error getting user goals from SharedPreferences", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteUserGoal(String goalId, String userId) {
        List<UserGoalStorage> goals = getUserGoals(userId);
        goals.removeIf(goal -> goal.getId().equals(goalId));
        saveUserGoals(goals, userId);
    }

    @Override
    public void saveProgressPhoto(ProgressPhotoStorage photo, String userId) {
        // Получаем текущий список фото
        List<ProgressPhotoStorage> photos = getProgressPhotos(userId);
        // Добавляем новое фото
        photos.add(photo);
        // Сохраняем обновленный список
        saveProgressPhotos(photos, userId);
    }

    @Override
    public void saveProgressPhotos(List<ProgressPhotoStorage> photos, String userId) {
        try {
            String prefix = "user_" + userId + "_photos";
            Gson gson = new Gson();
            String json = gson.toJson(photos);
            sharedPreferences.edit().putString(prefix, json).apply();
            Log.d(TAG, "Progress photos saved to SharedPreferences for user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error saving progress photos to SharedPreferences", e);
        }
    }

    @Override
    public List<ProgressPhotoStorage> getProgressPhotos(String userId) {
        try {
            String prefix = "user_" + userId + "_photos";
            String json = sharedPreferences.getString(prefix, null);
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProgressPhotoStorage>>(){}.getType();
            List<ProgressPhotoStorage> photos = gson.fromJson(json, type);
            return photos != null ? photos : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error getting progress photos from SharedPreferences", e);
            return new ArrayList<>();
        }
    }
}