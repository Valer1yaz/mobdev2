package ru.mirea.zhemaytisvs.fitmotiv.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

public class WorkoutDetailFragment extends Fragment {

    private MainViewModel viewModel;
    private Workout currentWorkout;

    private ImageView ivWorkoutDetail;
    private TextView tvType, tvDescription, tvDate, tvDuration, tvCalories;
    private Button btnDeleteWorkout, btnEditWorkout, btnBack;

    private String workoutId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем workoutId из аргументов
        if (getArguments() != null) {
            workoutId = getArguments().getString("workoutId");
        }

        initializeViewModel();
        initializeUI(view);
        setupLiveDataObservers();
        setupEventListeners();

        // Загружаем данные тренировки
        loadWorkoutDetails();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void initializeUI(View view) {
        ivWorkoutDetail = view.findViewById(R.id.ivWorkoutDetail);
        tvType = view.findViewById(R.id.tvType);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvDate = view.findViewById(R.id.tvDate);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvCalories = view.findViewById(R.id.tvCalories);
        btnDeleteWorkout = view.findViewById(R.id.btnDeleteWorkout);
        btnEditWorkout = view.findViewById(R.id.btnEditWorkout);
        btnBack = view.findViewById(R.id.btnBack); // Добавляем кнопку назад
    }

    private void setupLiveDataObservers() {
        viewModel.getWorkoutsLiveData().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && workoutId != null) {
                for (Workout workout : workouts) {
                    if (workout.getId().equals(workoutId)) {
                        currentWorkout = workout;
                        updateUIWithWorkoutData();
                        break;
                    }
                }
            }
        });

        viewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                boolean isGuest = user.isGuest();
                btnDeleteWorkout.setEnabled(!isGuest);
                btnEditWorkout.setEnabled(!isGuest);

                if (isGuest) {
                    btnDeleteWorkout.setAlpha(0.5f);
                    btnEditWorkout.setAlpha(0.5f);
                }
            }
        });
    }

    private void setupEventListeners() {
        btnDeleteWorkout.setOnClickListener(v -> showDeleteConfirmationDialog());
        btnEditWorkout.setOnClickListener(v -> showEditWorkoutDialog());
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void loadWorkoutDetails() {
        if (workoutId == null) {
            Toast.makeText(getContext(), "Ошибка: ID тренировки не найден", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
            return;
        }

        // Загружаем все тренировки через ViewModel
        viewModel.loadWorkouts();
    }

    private void updateUIWithWorkoutData() {
        if (currentWorkout == null) return;

        // Установка текстовых данных
        tvType.setText(getTypeString(currentWorkout.getType()));
        tvDescription.setText(currentWorkout.getDescription());
        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(currentWorkout.getDate()));
        tvDuration.setText(currentWorkout.getDuration() + " минут");
        tvCalories.setText(currentWorkout.getCalories() + " ккал");

        // Загрузка изображения с помощью Glide
        int imageResource = getWorkoutImageResource(currentWorkout.getType());

        Glide.with(this)
                .load(imageResource)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.workout_placeholder)
                .error(R.drawable.workout_placeholder)
                .into(ivWorkoutDetail);
    }

    private String getTypeString(Workout.WorkoutType type) {
        switch (type) {
            case CARDIO: return "Кардио тренировка";
            case STRENGTH: return "Силовая тренировка";
            case YOGA: return "Йога";
            case SWIMMING: return "Плавание";
            default: return "Тренировка";
        }
    }

    private int getWorkoutImageResource(Workout.WorkoutType type) {
        switch (type) {
            case CARDIO: return R.drawable.ic_cardio;
            case STRENGTH: return R.drawable.ic_strength;
            case YOGA: return R.drawable.ic_yoga;
            case SWIMMING: return R.drawable.ic_swimming;
            default: return R.drawable.workout_placeholder;
        }
    }

    private void showDeleteConfirmationDialog() {
        if (currentWorkout == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Удаление тренировки")
                .setMessage("Вы уверены, что хотите удалить эту тренировку?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    viewModel.deleteWorkout(currentWorkout.getId());
                    Toast.makeText(getContext(), "Тренировка удалена", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditWorkoutDialog() {
        // TODO: Реализовать редактирование тренировки
        Toast.makeText(getContext(), "Функция редактирования в разработке", Toast.LENGTH_SHORT).show();
    }
}