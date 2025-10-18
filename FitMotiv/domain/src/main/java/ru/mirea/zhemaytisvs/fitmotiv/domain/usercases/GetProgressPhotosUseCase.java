package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import java.util.List;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;

public class GetProgressPhotosUseCase {
    private final ProgressRepository progressRepository;

    public GetProgressPhotosUseCase(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<ProgressPhoto> execute() {
        return progressRepository.getProgressPhotos();
    }
}