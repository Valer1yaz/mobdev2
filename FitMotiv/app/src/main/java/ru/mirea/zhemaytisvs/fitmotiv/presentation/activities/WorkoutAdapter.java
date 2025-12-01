package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

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
    }

    public void updateData(List<Workout> newWorkouts) {
        this.workouts = newWorkouts != null ? newWorkouts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_with_image, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        if (workouts != null && position < workouts.size()) {
            Workout workout = workouts.get(position);
            holder.bind(workout);
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView ivWorkout;
        private final TextView tvType;
        private final TextView tvDescription;
        private final TextView tvDate;
        private final TextView tvDuration;
        private final TextView tvCalories;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardWorkout);
            ivWorkout = itemView.findViewById(R.id.ivWorkout);
            tvType = itemView.findViewById(R.id.tvWorkoutType);
            tvDescription = itemView.findViewById(R.id.tvWorkoutDescription);
            tvDate = itemView.findViewById(R.id.tvWorkoutDate);
            tvDuration = itemView.findViewById(R.id.tvWorkoutDuration);
            tvCalories = itemView.findViewById(R.id.tvWorkoutCalories);

            cardView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onWorkoutClick(workouts.get(position));
                }
            });
        }

        public void bind(Workout workout) {
            tvType.setText(getTypeString(workout.getType()));
            tvDescription.setText(workout.getDescription());
            tvDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(workout.getDate()));
            tvDuration.setText(workout.getDuration() + " мин");
            tvCalories.setText(workout.getCalories() + " ккал");

            // Загрузка изображения с помощью Glide
            int imageResource = getWorkoutImage(workout.getType());

            Glide.with(itemView.getContext())
                    .load(imageResource)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.workout_placeholder)
                    .error(R.drawable.workout_placeholder)
                    .centerCrop()
                    .into(ivWorkout);
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

        private int getWorkoutImage(Workout.WorkoutType type) {
            switch (type) {
                case CARDIO: return R.drawable.ic_cardio;
                case STRENGTH: return R.drawable.ic_strength;
                case YOGA: return R.drawable.ic_yoga;
                case SWIMMING: return R.drawable.ic_swimming;
                default: return R.drawable.workout_placeholder;
            }
        }
    }
}