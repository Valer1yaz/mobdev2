package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;

public interface ProgressRepository {
    void saveProgressPhoto(ProgressPhoto photo, String userId);
    List<ProgressPhoto> getProgressPhotos(String userId);
    ProgressPhoto getProgressPhotoById(String id, String userId);
}