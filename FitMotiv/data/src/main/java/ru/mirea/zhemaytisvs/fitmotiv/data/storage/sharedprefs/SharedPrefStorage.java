package ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs;

import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.WorkoutStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.ProgressPhotoStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.UserGoalStorage;

public interface SharedPrefStorage {
    void saveWorkout(WorkoutStorage workout);
    WorkoutStorage getWorkout();
    void saveProgressPhoto(ProgressPhotoStorage photo);
    ProgressPhotoStorage getProgressPhoto();
    void saveUserGoal(UserGoalStorage goal);
    UserGoalStorage getUserGoal();
}