package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.content.Context;
import android.util.Log;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorage;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.sharedprefs.SharedPrefStorageImpl;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.models.ProgressPhotoStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgressRepositoryImpl implements ProgressRepository {
    private static final String TAG = "ProgressRepositoryImpl";
    
    private final SharedPrefStorage sharedPrefStorage;
    
    public ProgressRepositoryImpl() {
        // Для совместимости со старым кодом
        this.sharedPrefStorage = null;
    }
    
    public ProgressRepositoryImpl(Context context) {
        this.sharedPrefStorage = new SharedPrefStorageImpl(context);
    }
    
    @Override
    public void saveProgressPhoto(ProgressPhoto photo, String userId) {
        if (sharedPrefStorage == null || userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot save photo: sharedPrefStorage is null or userId is invalid");
            return;
        }
        
        photo.setUserId(userId);
        ProgressPhotoStorage storage = new ProgressPhotoStorage(
                photo.getId(),
                photo.getUserId(),
                photo.getImageUrl(),
                photo.getDescription(),
                photo.getDate() != null ? photo.getDate().getTime() : System.currentTimeMillis(),
                System.currentTimeMillis()
        );
        
        sharedPrefStorage.saveProgressPhoto(storage, userId);
        Log.d(TAG, "Photo saved for user: " + userId);
    }

    @Override
    public List<ProgressPhoto> getProgressPhotos(String userId) {
        if (sharedPrefStorage == null || userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ProgressPhotoStorage> storages = sharedPrefStorage.getProgressPhotos(userId);
        List<ProgressPhoto> photos = new ArrayList<>();
        
        for (ProgressPhotoStorage storage : storages) {
            ProgressPhoto photo = new ProgressPhoto(
                    storage.getId(),
                    storage.getImageUrl(),
                    storage.getDescription(),
                    new Date(storage.getDate())
            );
            photo.setUserId(storage.getUserId());
            photos.add(photo);
        }
        
        Log.d(TAG, "Loaded " + photos.size() + " photos for user: " + userId);
        return photos;
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
