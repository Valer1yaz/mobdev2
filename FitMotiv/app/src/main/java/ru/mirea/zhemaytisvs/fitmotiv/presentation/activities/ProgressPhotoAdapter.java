package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgressPhotoAdapter extends RecyclerView.Adapter<ProgressPhotoAdapter.ProgressPhotoViewHolder> {

    private List<ProgressPhoto> photos;

    public ProgressPhotoAdapter(List<ProgressPhoto> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public void updateData(List<ProgressPhoto> newPhotos) {
        this.photos = newPhotos != null ? newPhotos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProgressPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress_photo, parent, false);
        return new ProgressPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressPhotoViewHolder holder, int position) {
        ProgressPhoto photo = photos.get(position);
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class ProgressPhotoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private final TextView tvDescription;
        private final TextView tvDate;
        private final TextView tvLikes;

        public ProgressPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }

        public void bind(ProgressPhoto photo) {
            // Используем Glide для оптимизированной загрузки изображений
            // Если imageUrl пустой или null, используем placeholder
            if (photo.getImageUrl() != null && !photo.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(photo.getImageUrl())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery))
                        .into(ivPhoto);
            } else {
                // Используем стандартную иконку, если URL отсутствует
                ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            tvDescription.setText(photo.getDescription());
            tvDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(photo.getDate()));
            tvLikes.setText("❤️ " + photo.getLikes());
        }
    }
}