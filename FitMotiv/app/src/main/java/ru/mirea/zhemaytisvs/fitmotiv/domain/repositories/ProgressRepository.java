package ru.mirea.zhemaytisvs.fitmotiv.domain.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import java.util.List;

public interface ProgressRepository {
    void saveProgressPhoto(ProgressPhoto photo);
    List<ProgressPhoto> getProgressPhotos();
    ProgressPhoto getProgressPhotoById(String id);
}