package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;
import java.util.List;

public class GetProgressPhotosUseCase {
    private final ProgressRepository progressRepository;

    public GetProgressPhotosUseCase(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<ProgressPhoto> execute() {
        return progressRepository.getProgressPhotos();
    }
}