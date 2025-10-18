package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;

public class ProgressRepositoryImpl implements ProgressRepository {
    private List<ProgressPhoto> testPhotos;

    public ProgressRepositoryImpl() {
        initializeTestData();
    }

    private void initializeTestData() {
        testPhotos = new ArrayList<>(Arrays.asList(
                new ProgressPhoto("1", "https://example.com/photo1.jpg", "Начало тренировок",
                        getDate(2024, Calendar.JANUARY, 1), 5),
                new ProgressPhoto("2", "https://example.com/photo2.jpg", "Через 2 недели",
                        getDate(2024, Calendar.JANUARY, 15), 12),
                new ProgressPhoto("3", "https://example.com/photo3.jpg", "Месяц тренировок",
                        getDate(2024, Calendar.FEBRUARY, 1), 25)
        ));
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    @Override
    public void saveProgressPhoto(ProgressPhoto photo) {
        testPhotos.add(photo);
    }

    @Override
    public List<ProgressPhoto> getProgressPhotos() {
        return new ArrayList<>(testPhotos);
    }

    @Override
    public ProgressPhoto getProgressPhotoById(String id) {
        for (ProgressPhoto photo : testPhotos) {
            if (photo.getId().equals(id)) {
                return photo;
            }
        }
        return null;
    }
}