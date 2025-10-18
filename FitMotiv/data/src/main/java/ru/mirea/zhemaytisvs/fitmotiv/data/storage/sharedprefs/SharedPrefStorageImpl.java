package ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.WorkoutStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.ProgressPhotoStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.UserGoalStorage;

import java.util.Date;

public class SharedPrefStorageImpl implements SharedPrefStorage {
    private static final String TAG = "SharedPrefStorage";
    private static final String SHARED_PREFS_NAME = "fitness_motivator_prefs";

    private static final String KEY_WORKOUT_ID = "workout_id";
    private static final String KEY_WORKOUT_TYPE = "workout_type";
    private static final String KEY_WORKOUT_DURATION = "workout_duration";
    private static final String KEY_WORKOUT_CALORIES = "workout_calories";
    private static final String KEY_WORKOUT_DATE = "workout_date";
    private static final String KEY_WORKOUT_DESCRIPTION = "workout_description";
    private static final String KEY_WORKOUT_SAVED_AT = "workout_saved_at";

    private static final String KEY_PHOTO_ID = "photo_id";
    private static final String KEY_PHOTO_URL = "photo_url";
    private static final String KEY_PHOTO_DESCRIPTION = "photo_description";
    private static final String KEY_PHOTO_DATE = "photo_date";
    private static final String KEY_PHOTO_LIKES = "photo_likes";
    private static final String KEY_PHOTO_SAVED_AT = "photo_saved_at";

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
    public void saveWorkout(WorkoutStorage workout) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_WORKOUT_ID, workout.getId());
            editor.putString(KEY_WORKOUT_TYPE, workout.getType());
            editor.putInt(KEY_WORKOUT_DURATION, workout.getDuration());
            editor.putInt(KEY_WORKOUT_CALORIES, workout.getCalories());
            editor.putLong(KEY_WORKOUT_DATE, workout.getDate().getTime());
            editor.putString(KEY_WORKOUT_DESCRIPTION, workout.getDescription());
            editor.putLong(KEY_WORKOUT_SAVED_AT, new Date().getTime());
            editor.apply();
            Log.d(TAG, "Workout saved to SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Error saving workout to SharedPreferences", e);
        }
    }

    @Override
    public WorkoutStorage getWorkout() {
        try {
            String id = sharedPreferences.getString(KEY_WORKOUT_ID, "default_id");
            String type = sharedPreferences.getString(KEY_WORKOUT_TYPE, "CARDIO");
            int duration = sharedPreferences.getInt(KEY_WORKOUT_DURATION, 0);
            int calories = sharedPreferences.getInt(KEY_WORKOUT_CALORIES, 0);
            long dateMillis = sharedPreferences.getLong(KEY_WORKOUT_DATE, System.currentTimeMillis());
            String description = sharedPreferences.getString(KEY_WORKOUT_DESCRIPTION, "No description");
            long savedAtMillis = sharedPreferences.getLong(KEY_WORKOUT_SAVED_AT, System.currentTimeMillis());

            return new WorkoutStorage(
                    id, type, duration, calories,
                    new Date(dateMillis), description, new Date(savedAtMillis)
            );
        } catch (Exception e) {
            Log.e(TAG, "Error getting workout from SharedPreferences", e);
            return null;
        }
    }

    @Override
    public void saveProgressPhoto(ProgressPhotoStorage photo) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_PHOTO_ID, photo.getId());
            editor.putString(KEY_PHOTO_URL, photo.getImageUrl());
            editor.putString(KEY_PHOTO_DESCRIPTION, photo.getDescription());
            editor.putLong(KEY_PHOTO_DATE, photo.getDate().getTime());
            editor.putInt(KEY_PHOTO_LIKES, photo.getLikes());
            editor.putLong(KEY_PHOTO_SAVED_AT, new Date().getTime());
            editor.apply();
            Log.d(TAG, "Progress photo saved to SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Error saving progress photo to SharedPreferences", e);
        }
    }

    @Override
    public ProgressPhotoStorage getProgressPhoto() {
        try {
            String id = sharedPreferences.getString(KEY_PHOTO_ID, "default_photo_id");
            String url = sharedPreferences.getString(KEY_PHOTO_URL, "");
            String description = sharedPreferences.getString(KEY_PHOTO_DESCRIPTION, "No description");
            long dateMillis = sharedPreferences.getLong(KEY_PHOTO_DATE, System.currentTimeMillis());
            int likes = sharedPreferences.getInt(KEY_PHOTO_LIKES, 0);
            long savedAtMillis = sharedPreferences.getLong(KEY_PHOTO_SAVED_AT, System.currentTimeMillis());

            return new ProgressPhotoStorage(
                    id, url, description,
                    new Date(dateMillis), likes, new Date(savedAtMillis)
            );
        } catch (Exception e) {
            Log.e(TAG, "Error getting progress photo from SharedPreferences", e);
            return null;
        }
    }

    @Override
    public void saveUserGoal(UserGoalStorage goal) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_GOAL_ID, goal.getId());
            editor.putInt(KEY_GOAL_WEIGHT, goal.getTargetWeight());
            editor.putInt(KEY_GOAL_WORKOUTS, goal.getWorkoutsPerWeek());
            editor.putString(KEY_GOAL_DESCRIPTION, goal.getDescription());
            editor.putBoolean(KEY_GOAL_COMPLETED, goal.isCompleted());
            editor.putLong(KEY_GOAL_SAVED_AT, System.currentTimeMillis());
            editor.apply();
            Log.d(TAG, "User goal saved to SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Error saving user goal to SharedPreferences", e);
        }
    }

    @Override
    public UserGoalStorage getUserGoal() {
        try {
            String id = sharedPreferences.getString(KEY_GOAL_ID, "default_goal_id");
            int weight = sharedPreferences.getInt(KEY_GOAL_WEIGHT, 70);
            int workouts = sharedPreferences.getInt(KEY_GOAL_WORKOUTS, 3);
            String description = sharedPreferences.getString(KEY_GOAL_DESCRIPTION, "Похудеть и набрать форму");
            boolean completed = sharedPreferences.getBoolean(KEY_GOAL_COMPLETED, false);
            long savedAt = sharedPreferences.getLong(KEY_GOAL_SAVED_AT, System.currentTimeMillis());

            return new UserGoalStorage(id, weight, workouts, description, completed, savedAt);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user goal from SharedPreferences", e);
            return null;
        }
    }
}