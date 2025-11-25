package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;

public class SetGoalDialog extends DialogFragment {
    
    public interface OnGoalSetListener {
        void onGoalSet(UserGoal goal);
    }
    
    private OnGoalSetListener listener;
    private EditText etTargetWeight;
    private EditText etWorkoutsPerWeek;
    private EditText etDescription;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_set_goal, null);
        
        etTargetWeight = view.findViewById(R.id.etTargetWeight);
        etWorkoutsPerWeek = view.findViewById(R.id.etWorkoutsPerWeek);
        etDescription = view.findViewById(R.id.etGoalDescription);
        
        Button btnSave = view.findViewById(R.id.btnSaveGoal);
        Button btnCancel = view.findViewById(R.id.btnCancelGoal);
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    UserGoal goal = createGoal();
                    if (listener != null) {
                        listener.onGoalSet(goal);
                    }
                    dismiss();
                }
            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        builder.setView(view);
        builder.setTitle("Установить цель");
        
        return builder.create();
    }
    
    public void setOnGoalSetListener(OnGoalSetListener listener) {
        this.listener = listener;
    }
    
    private boolean validateInput() {
        String weightStr = etTargetWeight.getText().toString().trim();
        String workoutsStr = etWorkoutsPerWeek.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        
        if (weightStr.isEmpty()) {
            Toast.makeText(getActivity(), "Введите целевой вес", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (workoutsStr.isEmpty()) {
            Toast.makeText(getActivity(), "Введите количество тренировок в неделю", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        try {
            int weight = Integer.parseInt(weightStr);
            int workouts = Integer.parseInt(workoutsStr);
            
            if (weight <= 0) {
                Toast.makeText(getActivity(), "Вес должен быть больше 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (workouts <= 0 || workouts > 14) {
                Toast.makeText(getActivity(), "Количество тренировок должно быть от 1 до 14", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Некорректный формат чисел", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (description.isEmpty()) {
            Toast.makeText(getActivity(), "Введите описание цели", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private UserGoal createGoal() {
        int targetWeight = Integer.parseInt(etTargetWeight.getText().toString().trim());
        int workoutsPerWeek = Integer.parseInt(etWorkoutsPerWeek.getText().toString().trim());
        String description = etDescription.getText().toString().trim();
        String id = String.valueOf(System.currentTimeMillis());
        
        return new UserGoal(id, targetWeight, workoutsPerWeek, description, false);
    }
}

