package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;
    private OnWorkoutClickListener listener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(List<Workout> workouts, OnWorkoutClickListener listener) {
        this.workouts = workouts != null ? workouts : new ArrayList<>();
        this.listener = listener;
        Log.d("WorkoutAdapter", "Adapter created with " + this.workouts.size() + " workouts");
    }

    public void updateData(List<Workout> newWorkouts) {
        Log.d("WorkoutAdapter", "=== UPDATE DATA CALLED ===");
        Log.d("WorkoutAdapter", "Old size: " + (this.workouts != null ? this.workouts.size() : 0));
        Log.d("WorkoutAdapter", "New size: " + (newWorkouts != null ? newWorkouts.size() : 0));

        this.workouts = newWorkouts != null ? newWorkouts : new ArrayList<>();
        notifyDataSetChanged();

        Log.d("WorkoutAdapter", "Adapter notified, item count: " + getItemCount());
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("WorkoutAdapter", "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);

        // Проверяем, что View найдены правильно
        CardView cardView = view.findViewById(R.id.cardWorkout);
        TextView tvType = view.findViewById(R.id.tvWorkoutType);
        TextView tvDescription = view.findViewById(R.id.tvWorkoutDescription);

        Log.d("WorkoutAdapter", "cardView: " + (cardView != null ? "found" : "NULL"));
        Log.d("WorkoutAdapter", "tvType: " + (tvType != null ? "found" : "NULL"));
        Log.d("WorkoutAdapter", "tvDescription: " + (tvDescription != null ? "found" : "NULL"));

        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Log.d("WorkoutAdapter", "onBindViewHolder position: " + position);
        if (workouts != null && position < workouts.size()) {
            Workout workout = workouts.get(position);
            Log.d("WorkoutAdapter", "Binding workout: " + workout.getType() + " - " + workout.getDescription());
            holder.bind(workout);
        } else {
            Log.e("WorkoutAdapter", "Invalid position or workouts list: position=" + position + ", size=" + (workouts != null ? workouts.size() : "null"));
        }
    }

    @Override
    public int getItemCount() {
        int count = workouts.size();
        Log.d("WorkoutAdapter", "getItemCount() = " + count);
        return count;
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvType;
        private final TextView tvDescription;
        private final TextView tvDate;
        private final TextView tvDuration;
        private final TextView tvCalories;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("WorkoutAdapter", "WorkoutViewHolder constructor");

            cardView = itemView.findViewById(R.id.cardWorkout);
            tvType = itemView.findViewById(R.id.tvWorkoutType);
            tvDescription = itemView.findViewById(R.id.tvWorkoutDescription);
            tvDate = itemView.findViewById(R.id.tvWorkoutDate);
            tvDuration = itemView.findViewById(R.id.tvWorkoutDuration);
            tvCalories = itemView.findViewById(R.id.tvWorkoutCalories);

            // Проверяем, что все View найдены
            Log.d("WorkoutAdapter", "Views found - " +
                    "cardView: " + (cardView != null) + ", " +
                    "tvType: " + (tvType != null) + ", " +
                    "tvDescription: " + (tvDescription != null) + ", " +
                    "tvDate: " + (tvDate != null) + ", " +
                    "tvDuration: " + (tvDuration != null) + ", " +
                    "tvCalories: " + (tvCalories != null));

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    Log.d("WorkoutAdapter", "Card clicked at position: " + position);
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onWorkoutClick(workouts.get(position));
                    }
                }
            });
        }

        public void bind(Workout workout) {
            Log.d("WorkoutAdapter", "Binding workout: " + workout.getType() + " - " + workout.getDescription());

            try {
                tvType.setText(getTypeString(workout.getType()));
                tvDescription.setText(workout.getDescription());
                tvDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(workout.getDate()));
                tvDuration.setText(workout.getDuration() + " мин");
                tvCalories.setText(workout.getCalories() + " ккал");

                Log.d("WorkoutAdapter", "Workout bound successfully");
            } catch (Exception e) {
                Log.e("WorkoutAdapter", "Error binding workout", e);
            }
        }

        private String getTypeString(Workout.WorkoutType type) {
            switch (type) {
                case CARDIO: return "Кардио";
                case STRENGTH: return "Силовая";
                case YOGA: return "Йога";
                case SWIMMING: return "Плавание";
                default: return "Тренировка";
            }
        }
    }
}