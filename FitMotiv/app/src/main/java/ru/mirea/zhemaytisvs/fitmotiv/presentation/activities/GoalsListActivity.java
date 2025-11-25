package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity для просмотра и управления списком целей пользователя
 */
public class GoalsListActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private RecyclerView rvGoals;
    private GoalsAdapter goalsAdapter;
    private Button btnAddGoal;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_list);

        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();
        loadGoals();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initializeUI() {
        rvGoals = findViewById(R.id.rvGoals);
        btnAddGoal = findViewById(R.id.btnAddGoal);
        btnBack = findViewById(R.id.btnGoalsBack);

        goalsAdapter = new GoalsAdapter(new ArrayList<>(), new GoalsAdapter.OnGoalClickListener() {
            @Override
            public void onGoalClick(UserGoal goal) {
                // Можно добавить детали цели
            }

            @Override
            public void onGoalDelete(UserGoal goal) {
                viewModel.deleteGoal(goal.getId());
            }
        });

        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalsAdapter);

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewGoal();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupLiveDataObservers() {
        viewModel.getGoalsLiveData().observe(this, new Observer<List<UserGoal>>() {
            @Override
            public void onChanged(List<UserGoal> goals) {
                if (goals != null) {
                    goalsAdapter.updateData(goals);
                }
            }
        });
    }

    private void loadGoals() {
        viewModel.loadGoals();
    }

    private void addNewGoal() {
        SetGoalDialog dialog = new SetGoalDialog();
        dialog.setOnGoalSetListener(new SetGoalDialog.OnGoalSetListener() {
            @Override
            public void onGoalSet(UserGoal goal) {
                viewModel.addGoal(goal);
            }
        });
        dialog.show(getSupportFragmentManager(), "SetGoalDialog");
    }
}

