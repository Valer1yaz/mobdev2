package ru.mirea.zhemaytisvs.fitmotiv.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.User;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.activities.AddWorkoutDialog;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

public class HomeFragment extends Fragment {

    private MainViewModel viewModel;
    private TextView tvQuote, tvWorkoutCount;
    private Button btnAddWorkout, btnViewWorkouts, btnGetQuote, btnAnalyzeExercise;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewModel();
        initializeUI(view);
        setupLiveDataObservers();
        loadInitialData();
        setupEventListeners();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void initializeUI(View view) {
        tvQuote = view.findViewById(R.id.tvQuote);
        tvWorkoutCount = view.findViewById(R.id.tvWorkoutCount);
        btnAddWorkout = view.findViewById(R.id.btnAddWorkout);
        btnViewWorkouts = view.findViewById(R.id.btnViewWorkouts);
        btnGetQuote = view.findViewById(R.id.btnGetQuote);
        btnAnalyzeExercise = view.findViewById(R.id.btnAnalyzeExercise);
    }

    private void setupLiveDataObservers() {
        // Наблюдатель для цитат
        viewModel.getQuoteLiveData().observe(getViewLifecycleOwner(), quote -> {
            if (quote != null && tvQuote != null) {
                tvQuote.setText("\"" + quote + "\"");
            }
        });

        // Наблюдатель для статистики тренировок
        viewModel.getWorkoutStatisticsLiveData().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null && tvWorkoutCount != null) {
                String statsText = String.format("Тренировок: %d\nСожжено калорий: %d",
                        stats.getTotalWorkouts(), stats.getTotalCalories());
                tvWorkoutCount.setText(statsText);
            }
        });

        // Наблюдатель для пользователя
        viewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                setupUserMode(user);
            }
        });
    }

    private void loadInitialData() {
        viewModel.loadMotivationalQuote();
        viewModel.loadCurrentUser();
    }

    private void setupEventListeners() {
        btnAddWorkout.setOnClickListener(v -> addNewWorkout());

        btnViewWorkouts.setOnClickListener(v -> {
            // Проверяем, не гость ли пользователь
            if (currentUser != null && currentUser.isGuest()) {
                Toast.makeText(requireContext(), "Для просмотра тренировок необходимо войти в аккаунт", Toast.LENGTH_SHORT).show();
                return;
            }
            // Навигация к списку тренировок
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_workoutListFragment);
        });

        btnGetQuote.setOnClickListener(v -> viewModel.loadMotivationalQuote());

        btnAnalyzeExercise.setOnClickListener(v -> {
            // Проверяем, не гость ли пользователь
            if (currentUser != null && currentUser.isGuest()) {
                Toast.makeText(requireContext(), "Для анализа упражнений необходимо войти в аккаунт", Toast.LENGTH_SHORT).show();
                return;
            }
            // Навигация к анализу упражнений
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_exerciseAnalysisFragment);
        });
    }

    private void setupUserMode(User user) {
        if (user.isGuest()) {
            btnAddWorkout.setEnabled(false);
            btnAddWorkout.setAlpha(0.5f);
            btnAddWorkout.setText("Добавить тренировку (только для зарегистрированных)");
            btnAnalyzeExercise.setEnabled(false);
            btnAnalyzeExercise.setAlpha(0.5f);
        } else {
            btnAddWorkout.setEnabled(true);
            btnAddWorkout.setAlpha(1f);
            btnAddWorkout.setText("Добавить тренировку");
            btnAnalyzeExercise.setEnabled(true);
            btnAnalyzeExercise.setAlpha(1f);
        }
    }

    private void addNewWorkout() {
        if (currentUser == null || currentUser.isGuest()) {
            Toast.makeText(requireContext(), "Для добавления тренировки необходимо войти в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }

        AddWorkoutDialog dialog = new AddWorkoutDialog();
        dialog.setUserId(currentUser.getUid());
        dialog.setOnWorkoutAddedListener(workout -> {
            viewModel.addWorkout(workout);
            Toast.makeText(requireContext(), "Тренировка добавлена!", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getParentFragmentManager(), "AddWorkoutDialog");
    }
}