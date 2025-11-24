package ru.mirea.zhemaitisvs.recyclerviewapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private ImageView flagView;
    private TextView eventNameView;
    private TextView descriptionView;
    private TextView yearView;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        this.flagView = itemView.findViewById(R.id.imageView);
        this.eventNameView = itemView.findViewById(R.id.textViewEventName);
        this.descriptionView = itemView.findViewById(R.id.textViewDescription);
        this.yearView = itemView.findViewById(R.id.textViewYear);
    }

    public ImageView getFlagView() {
        return flagView;
    }

    public TextView getEventNameView() {
        return eventNameView;
    }

    public TextView getDescriptionView() {
        return descriptionView;
    }

    public TextView getYearView() {
        return yearView;
    }
}
