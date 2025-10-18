package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;

public interface ProgressRepository {
    void saveProgressPhoto(ProgressPhoto photo);
    List<ProgressPhoto> getProgressPhotos();
    ProgressPhoto getProgressPhotoById(String id);
}