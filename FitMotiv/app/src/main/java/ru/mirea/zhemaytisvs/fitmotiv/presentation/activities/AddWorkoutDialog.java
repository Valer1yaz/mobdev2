package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;

import java.util.Date;

public class AddWorkoutDialog extends DialogFragment {
    
    public interface OnWorkoutAddedListener {
        void onWorkoutAdded(Workout workout);
    }
    
    private OnWorkoutAddedListener listener;
    private Spinner spinnerType;
    private EditText etDuration;
    private EditText etCalories;
    private EditText etDescription;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_workout, null);
        
        spinnerType = view.findViewById(R.id.spinnerWorkoutType);
        etDuration = view.findViewById(R.id.etDuration);
        etCalories = view.findViewById(R.id.etCalories);
        etDescription = view.findViewById(R.id.etDescription);
        
        // Настройка Spinner для типов тренировок
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.workout_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        
        Button btnSave = view.findViewById(R.id.btnSaveWorkout);
        Button btnCancel = view.findViewById(R.id.btnCancelWorkout);
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddWorkoutDialog", "Save button clicked");
                if (validateInput()) {
                    Workout workout = createWorkout();
                    Log.d("AddWorkoutDialog", "Workout created: " + workout.getType() + ", " + workout.getDescription());
                    if (listener != null) {
                        listener.onWorkoutAdded(workout);
                        Log.d("AddWorkoutDialog", "Listener called");
                    } else {
                        Log.e("AddWorkoutDialog", "Listener is null!");
                    }
                    dismiss();
                } else {
                    Log.d("AddWorkoutDialog", "Validation failed");
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
        builder.setTitle("Добавить тренировку");
        
        return builder.create();
    }
    
    public void setOnWorkoutAddedListener(OnWorkoutAddedListener listener) {
        this.listener = listener;
    }
    
    private boolean validateInput() {
        String durationStr = etDuration.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        
        if (durationStr.isEmpty()) {
            Toast.makeText(getActivity(), "Введите длительность тренировки", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (caloriesStr.isEmpty()) {
            Toast.makeText(getActivity(), "Введите количество сожженных калорий", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        try {
            int duration = Integer.parseInt(durationStr);
            int calories = Integer.parseInt(caloriesStr);
            
            if (duration <= 0) {
                Toast.makeText(getActivity(), "Длительность должна быть больше 0", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (calories <= 0) {
                Toast.makeText(getActivity(), "Калории должны быть больше 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Некорректный формат чисел", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (description.isEmpty()) {
            Toast.makeText(getActivity(), "Введите описание тренировки", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private Workout createWorkout() {
        String typeStr = spinnerType.getSelectedItem().toString();
        Workout.WorkoutType type = mapStringToWorkoutType(typeStr);
        int duration = Integer.parseInt(etDuration.getText().toString().trim());
        int calories = Integer.parseInt(etCalories.getText().toString().trim());
        String description = etDescription.getText().toString().trim();
        String id = String.valueOf(System.currentTimeMillis());
        
        return new Workout(id, type, duration, calories, new Date(), description);
    }
    
    private Workout.WorkoutType mapStringToWorkoutType(String typeStr) {
        switch (typeStr) {
            case "Кардио":
                return Workout.WorkoutType.CARDIO;
            case "Силовая":
                return Workout.WorkoutType.STRENGTH;
            case "Йога":
                return Workout.WorkoutType.YOGA;
            case "Плавание":
                return Workout.WorkoutType.SWIMMING;
            default:
                return Workout.WorkoutType.CARDIO;
        }
    }
}

