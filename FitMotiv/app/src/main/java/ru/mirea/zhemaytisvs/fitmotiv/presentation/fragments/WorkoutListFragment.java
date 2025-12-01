package ru.mirea.zhemaytisvs.fitmotiv.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.activities.WorkoutAdapter;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

public class WorkoutListFragment extends Fragment {

    private MainViewModel viewModel;
    private RecyclerView rvWorkouts;
    private WorkoutAdapter workoutAdapter;
    private TextView tvEmpty;
    private Button btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewModel();
        initializeUI(view);
        setupLiveDataObservers();
        loadWorkouts();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void initializeUI(View view) {
        rvWorkouts = view.findViewById(R.id.rvWorkouts);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        btnBack = view.findViewById(R.id.btnBack); // Добавляем кнопку назад

        workoutAdapter = new WorkoutAdapter(new ArrayList<>(), workout -> {
            // Навигация к деталям тренировки
            Bundle bundle = new Bundle();
            bundle.putString("workoutId", workout.getId());
            Navigation.findNavController(view).navigate(
                    R.id.action_workoutListFragment_to_workoutDetailFragment,
                    bundle
            );
        });

        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWorkouts.setAdapter(workoutAdapter);

        // Обработчик кнопки "назад"
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp(); // Возврат назад
        });
    }

    private void setupLiveDataObservers() {
        viewModel.getCurrentUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user == null || user.isGuest()) {
                showEmptyState("Гостевой режим. Тренировки недоступны.");
            } else {
                viewModel.loadWorkouts();
            }
        });

        viewModel.getWorkoutsLiveData().observe(getViewLifecycleOwner(), workouts -> {
            updateUIWithWorkouts(workouts);
        });
    }

    private void loadWorkouts() {
        viewModel.loadWorkouts();
    }

    private void updateUIWithWorkouts(List<Workout> workouts) {
        if (workouts != null) {
            workoutAdapter.updateData(workouts);

            if (workouts.isEmpty()) {
                showEmptyState("Тренировок пока нет");
            } else {
                hideEmptyState();
            }
        } else {
            showEmptyState("Ошибка загрузки тренировок");
        }
    }

    private void showEmptyState(String message) {
        if (tvEmpty != null) {
            tvEmpty.setText(message);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        if (rvWorkouts != null) {
            rvWorkouts.setVisibility(View.GONE);
        }
    }

    private void hideEmptyState() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
        if (rvWorkouts != null) {
            rvWorkouts.setVisibility(View.VISIBLE);
        }
    }
}