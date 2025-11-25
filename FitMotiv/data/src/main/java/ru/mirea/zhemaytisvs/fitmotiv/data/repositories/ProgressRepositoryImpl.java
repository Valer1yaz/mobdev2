package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.util.Log;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressRepositoryImpl implements ProgressRepository {
    private static final String TAG = "ProgressRepositoryImpl";
    
    // Хранилище фото по userId
    private Map<String, List<ProgressPhoto>> userPhotosMap = new HashMap<>();
    
    public ProgressRepositoryImpl() {
        // Упрощенная реализация - фото хранятся в памяти
    }
    
    @Override
    public void saveProgressPhoto(ProgressPhoto photo, String userId) {
        photo.setUserId(userId);
        List<ProgressPhoto> userPhotos = userPhotosMap.get(userId);
        if (userPhotos == null) {
            userPhotos = new ArrayList<>();
            userPhotosMap.put(userId, userPhotos);
        }
        userPhotos.add(photo);
        Log.d(TAG, "Photo saved for user: " + userId);
    }

    @Override
    public List<ProgressPhoto> getProgressPhotos(String userId) {
        // Возвращаем фото конкретного пользователя
        List<ProgressPhoto> userPhotos = userPhotosMap.get(userId);
        if (userPhotos == null || userPhotos.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(userPhotos);
    }

    @Override
    public ProgressPhoto getProgressPhotoById(String id, String userId) {
        List<ProgressPhoto> photos = getProgressPhotos(userId);
        for (ProgressPhoto photo : photos) {
            if (photo.getId().equals(id)) {
                return photo;
            }
        }
        return null;
    }
}
