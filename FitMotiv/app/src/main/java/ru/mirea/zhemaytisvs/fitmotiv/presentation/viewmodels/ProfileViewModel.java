package ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.AuthRepositoryImpl;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class ProfileViewModel extends AndroidViewModel {

    private MutableLiveData<User> userData = new MutableLiveData<>();
    private MutableLiveData<TrainingStats> trainingStats = new MutableLiveData<>();

    private FirebaseAuth auth;
    private DatabaseReference database;
    private ExecutorService executorService;
    private AuthRepository authRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        executorService = Executors.newSingleThreadExecutor();
        authRepository = new AuthRepositoryImpl(application);
    }

    public void loadUserData() {
        executorService.execute(() -> {
            FirebaseUser firebaseUser = auth.getCurrentUser();

            if (firebaseUser != null) {
                // Создаем пользователя с photoUrl из Firebase
                User user = new User(
                        firebaseUser.getUid(),
                        firebaseUser.getEmail(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                        false
                );
                userData.postValue(user);

                // Загружаем статистику тренировок
                loadTrainingStats(firebaseUser.getUid());
            } else {
                // Гостевой режим
                userData.postValue(User.createGuestUser());
                trainingStats.postValue(new TrainingStats(0, 0));
            }
        });
    }

    private void loadTrainingStats(String userId) {
        database.child("workouts")
                .orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalTrainings = 0;
                        int totalCalories = 0;

                        for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                            totalTrainings++;
                            Integer calories = workoutSnapshot.child("calories").getValue(Integer.class);
                            if (calories != null) {
                                totalCalories += calories;
                            }
                        }

                        trainingStats.postValue(new TrainingStats(totalTrainings, totalCalories));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ProfileViewModel", "Error loading training stats", error.toException());
                        trainingStats.postValue(new TrainingStats(0, 0));
                    }
                });
    }

    public void updateProfilePhoto(String photoUrl, AuthRepository.AuthCallback callback) {
        executorService.execute(() -> {
            FirebaseUser firebaseUser = auth.getCurrentUser();

            if (firebaseUser != null) {
                try {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(android.net.Uri.parse(photoUrl))
                            .build();

                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Обновляем локального пользователя
                                    User currentUser = userData.getValue();
                                    if (currentUser != null) {
                                        currentUser.setPhotoUrl(photoUrl);
                                        userData.postValue(currentUser);
                                    }

                                    Log.d("ProfileViewModel", "Profile photo updated: " + photoUrl);
                                    callback.onSuccess(currentUser);
                                } else {
                                    callback.onError("Не удалось обновить фото профиля");
                                }
                            });

                } catch (Exception e) {
                    callback.onError("Неверный URL фотографии");
                }
            } else {
                callback.onError("Пользователь не аутентифицирован");
            }
        });
    }

    public MutableLiveData<User> getUserData() {
        return userData;
    }

    public MutableLiveData<TrainingStats> getTrainingStats() {
        return trainingStats;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public static class TrainingStats {
        private int totalTrainings;
        private int totalCalories;

        public TrainingStats(int totalTrainings, int totalCalories) {
            this.totalTrainings = totalTrainings;
            this.totalCalories = totalCalories;
        }

        public int getTotalTrainings() {
            return totalTrainings;
        }

        public int getTotalCalories() {
            return totalCalories;
        }
    }
}