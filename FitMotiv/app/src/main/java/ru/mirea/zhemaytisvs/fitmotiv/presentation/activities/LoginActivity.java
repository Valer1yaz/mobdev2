package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.AuthRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.LoginUseCase;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.RegisterUseCase;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.LoginAsGuestUseCase;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.AuthRepositoryImpl;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private EditText etDisplayName, etRegisterEmail, etRegisterPassword;
    private Button btnLogin, btnRegister, btnGuest;
    private ProgressBar progressBar;
    private TextView tvSwitchToRegister, tvSwitchToLogin;
    private View loginForm, registerForm;

    private LoginUseCase loginUseCase;
    private RegisterUseCase registerUseCase;
    private LoginAsGuestUseCase loginAsGuestUseCase;
    private AuthRepository authRepository;

    // Добавляем FirebaseAuth
    private FirebaseAuth firebaseAuth;

    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализируем FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        initializeDependencies();
        initializeUI();
        setupClickListeners();

        // Проверяем, не вошел ли уже пользователь
        checkExistingSession();

        // Проверяем, не пришли ли мы после выхода
        if (getIntent() != null && getIntent().getBooleanExtra("from_logout", false)) {
            Toast.makeText(this, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeDependencies() {
        // Инициализируем authRepository как поле класса
        authRepository = new AuthRepositoryImpl(this);
        loginUseCase = new LoginUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);
        loginAsGuestUseCase = new LoginAsGuestUseCase(authRepository);
    }

    private void checkExistingSession() {
        // Теперь authRepository доступен как поле класса
        User currentUser = authRepository.getCurrentUser();
        if (currentUser != null && !currentUser.isGuest()) {
            // Пользователь уже вошел, переходим на MainActivity
            Log.d("LoginActivity", "User already logged in, redirecting to MainActivity");
            navigateToMainActivity(currentUser);
        } else {
            Log.d("LoginActivity", "No existing session found, showing login form");
        }
    }

    private void navigateToMainActivity(User user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Очищаем историю стека чтобы нельзя было вернуться к LoginActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeUI() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        etDisplayName = findViewById(R.id.etDisplayName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnGuest = findViewById(R.id.btnGuest);
        progressBar = findViewById(R.id.progressBar);
        tvSwitchToRegister = findViewById(R.id.tvSwitchToRegister);
        tvSwitchToLogin = findViewById(R.id.tvSwitchToLogin);
        loginForm = findViewById(R.id.loginForm);
        registerForm = findViewById(R.id.registerForm);

        showLoginForm();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> register());
        btnGuest.setOnClickListener(v -> loginAsGuest());

        tvSwitchToRegister.setOnClickListener(v -> showRegisterForm());
        tvSwitchToLogin.setOnClickListener(v -> showLoginForm());
    }

    private void showLoginForm() {
        isRegisterMode = false;
        loginForm.setVisibility(View.VISIBLE);
        registerForm.setVisibility(View.GONE);
        tvSwitchToRegister.setVisibility(View.VISIBLE);
        tvSwitchToLogin.setVisibility(View.GONE);
    }

    private void showRegisterForm() {
        isRegisterMode = true;
        loginForm.setVisibility(View.GONE);
        registerForm.setVisibility(View.VISIBLE);
        tvSwitchToRegister.setVisibility(View.GONE);
        tvSwitchToLogin.setVisibility(View.VISIBLE);
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        loginUseCase.execute(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity(user);
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void register() {
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String displayName = etDisplayName.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Используем firebaseAuth вместо auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Обновляем displayName
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    // Можно добавить фото по умолчанию
                                    // .setPhotoUri(Uri.parse("https://example.com/default-avatar.png"))
                                    .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        showLoading(false);
                                        if (updateTask.isSuccessful()) {
                                            // Получаем URL фото (может быть null)
                                            String photoUrl = null;
                                            if (firebaseUser.getPhotoUrl() != null) {
                                                photoUrl = firebaseUser.getPhotoUrl().toString();
                                            }

                                            User user = new User(
                                                    firebaseUser.getUid(),
                                                    firebaseUser.getEmail(),
                                                    firebaseUser.getDisplayName(),
                                                    photoUrl,
                                                    false
                                            );

                                            Toast.makeText(LoginActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                                            navigateToMainActivity(user);
                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "Ошибка обновления профиля: " +
                                                            (updateTask.getException() != null ?
                                                                    updateTask.getException().getMessage() : "Неизвестная ошибка"),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            showLoading(false);
                            Toast.makeText(LoginActivity.this, "Ошибка получения пользователя", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showLoading(false);
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Неизвестная ошибка регистрации";
                        Toast.makeText(LoginActivity.this, "Ошибка регистрации: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginAsGuest() {
        showLoading(true);
        loginAsGuestUseCase.execute(new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Вы вошли как гость", Toast.LENGTH_SHORT).show();
                navigateToMainActivity(user);
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        btnRegister.setEnabled(!isLoading);
        btnGuest.setEnabled(!isLoading);
    }
}