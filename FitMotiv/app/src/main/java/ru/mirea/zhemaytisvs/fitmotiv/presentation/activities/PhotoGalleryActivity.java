package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhemaytisvs.fitmotiv.R;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.presentation.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity для просмотра галереи всех фото пользователя
 */
public class PhotoGalleryActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private RecyclerView rvPhotoGallery;
    private ProgressPhotoAdapter photoAdapter;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        initializeViewModel();
        initializeUI();
        setupLiveDataObservers();
        loadPhotos();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initializeUI() {
        rvPhotoGallery = findViewById(R.id.rvPhotoGallery);
        btnBack = findViewById(R.id.btnGalleryBack);

        photoAdapter = new ProgressPhotoAdapter(new ArrayList<>());
        photoAdapter.setOnPhotoClickListener(new ProgressPhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(ProgressPhoto photo) {
                openPhotoDetail(photo);
            }
        });

        // Используем GridLayoutManager для отображения в виде сетки
        rvPhotoGallery.setLayoutManager(new GridLayoutManager(this, 2));
        rvPhotoGallery.setAdapter(photoAdapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupLiveDataObservers() {
        viewModel.getProgressPhotosLiveData().observe(this, new Observer<List<ProgressPhoto>>() {
            @Override
            public void onChanged(List<ProgressPhoto> photos) {
                if (photos != null) {
                    photoAdapter.updateData(photos);
                }
            }
        });
    }

    private void loadPhotos() {
        viewModel.loadProgressPhotos();
    }

    private void openPhotoDetail(ProgressPhoto photo) {
        Intent intent = new Intent(this, ProgressPhotoDetailActivity.class);
        intent.putExtra("photo_id", photo.getId());
        intent.putExtra("image_url", photo.getImageUrl());
        intent.putExtra("description", photo.getDescription());
        intent.putExtra("date", photo.getDate().getTime());
        startActivity(intent);
    }
}

