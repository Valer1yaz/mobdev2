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
    private OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(ProgressPhoto photo);
    }

    public ProgressPhotoAdapter(List<ProgressPhoto> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.listener = listener;
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

    class ProgressPhotoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private final TextView tvDescription;
        private final TextView tvDate;

        public ProgressPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            
            // Обработка клика на фото
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onPhotoClick(photos.get(position));
                    }
                }
            });
        }

        public void bind(ProgressPhoto photo) {
            // Используем Glide для оптимизированной загрузки изображений
            if (photo.getImageUrl() != null && !photo.getImageUrl().isEmpty()) {
                // Проверяем, является ли это локальным URI (file://)
                if (photo.getImageUrl().startsWith("file://") || photo.getImageUrl().startsWith("content://")) {
                    Glide.with(itemView.getContext())
                            .load(android.net.Uri.parse(photo.getImageUrl()))
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .error(android.R.drawable.ic_menu_gallery))
                            .into(ivPhoto);
                } else {
                    // Для URL из интернета
                    Glide.with(itemView.getContext())
                            .load(photo.getImageUrl())
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .error(android.R.drawable.ic_menu_gallery))
                            .into(ivPhoto);
                }
            } else {
                // Используем стандартную иконку, если URL отсутствует
                ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Безопасная установка текста
            tvDescription.setText(photo.getDescription() != null ? photo.getDescription() : "");
            
            // Форматирование даты с обработкой null
            if (photo.getDate() != null) {
                tvDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(photo.getDate()));
            } else {
                tvDate.setText("");
            }
        }
    }
}