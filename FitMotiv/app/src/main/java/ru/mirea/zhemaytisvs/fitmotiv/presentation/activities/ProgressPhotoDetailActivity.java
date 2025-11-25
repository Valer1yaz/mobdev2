package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProgressPhotoDetailActivity extends AppCompatActivity {
    private ImageView ivPhoto;
    private TextView tvDescription;
    private TextView tvDate;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_photo_detail);

        initializeUI();
        loadPhotoData();
    }

    private void initializeUI() {
        ivPhoto = findViewById(R.id.ivDetailPhoto);
        tvDescription = findViewById(R.id.tvDetailPhotoDescription);
        tvDate = findViewById(R.id.tvDetailPhotoDate);
        btnBack = findViewById(R.id.btnPhotoDetailBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadPhotoData() {
        // Получаем данные фото из Intent
        String photoId = getIntent().getStringExtra("photo_id");
        String imageUrl = getIntent().getStringExtra("image_url");
        String description = getIntent().getStringExtra("description");
        long dateMillis = getIntent().getLongExtra("date", System.currentTimeMillis());

        // Загружаем изображение через Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Проверяем, является ли это локальным URI
            if (imageUrl.startsWith("file://") || imageUrl.startsWith("content://")) {
                Glide.with(this)
                        .load(android.net.Uri.parse(imageUrl))
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery))
                        .into(ivPhoto);
            } else {
                Glide.with(this)
                        .load(imageUrl)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery))
                        .into(ivPhoto);
            }
        } else {
            ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Устанавливаем текст
        tvDescription.setText(description != null ? description : "");
        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new java.util.Date(dateMillis)));
    }
}

