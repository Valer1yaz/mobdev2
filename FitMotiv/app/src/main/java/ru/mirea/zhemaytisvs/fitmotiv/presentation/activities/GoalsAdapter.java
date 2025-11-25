package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;

import java.util.ArrayList;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {
    private List<UserGoal> goals;
    private OnGoalClickListener listener;

    public interface OnGoalClickListener {
        void onGoalClick(UserGoal goal);
        void onGoalDelete(UserGoal goal);
    }

    public GoalsAdapter(List<UserGoal> goals, OnGoalClickListener listener) {
        this.goals = goals != null ? goals : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<UserGoal> newGoals) {
        this.goals = newGoals != null ? newGoals : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        UserGoal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvDescription;
        private final TextView tvTargetWeight;
        private final TextView tvWorkoutsPerWeek;
        private final TextView tvStatus;
        private final Button btnDelete;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardGoal);
            tvDescription = itemView.findViewById(R.id.tvGoalDescription);
            tvTargetWeight = itemView.findViewById(R.id.tvGoalTargetWeight);
            tvWorkoutsPerWeek = itemView.findViewById(R.id.tvGoalWorkoutsPerWeek);
            tvStatus = itemView.findViewById(R.id.tvGoalStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteGoal);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onGoalClick(goals.get(position));
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onGoalDelete(goals.get(position));
                    }
                }
            });
        }

        public void bind(UserGoal goal) {
            tvDescription.setText(goal.getDescription());
            tvTargetWeight.setText("Целевой вес: " + goal.getTargetWeight() + " кг");
            tvWorkoutsPerWeek.setText("Тренировок в неделю: " + goal.getWorkoutsPerWeek());
            tvStatus.setText(goal.isCompleted() ? "✓ Выполнена" : "В процессе");
        }
    }
}

