package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    private static final String TAG = "AuthRepositoryImpl";
    private final FirebaseAuth firebaseAuth;
    private User currentUser;

    public AuthRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            this.currentUser = new User(
                    firebaseUser.getUid(),
                    firebaseUser.getEmail(),
                    firebaseUser.getDisplayName(),
                    false
            );
        }
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            currentUser = new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail(),
                                    firebaseUser.getDisplayName(),
                                    false
                            );
                            Log.d(TAG, "User logged in: " + currentUser.getEmail());
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
                                            currentUser = new User(
                                                    firebaseUser.getUid(),
                                                    firebaseUser.getEmail(),
                                                    displayName,
                                                    false
                                            );
                                            Log.d(TAG, "User registered: " + currentUser.getEmail());
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
        Log.d(TAG, "Guest user logged in");
        callback.onSuccess(currentUser);
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
        currentUser = null;
        Log.d(TAG, "User logged out");
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isUserLoggedIn() {
        return currentUser != null && !currentUser.isGuest();
    }
}