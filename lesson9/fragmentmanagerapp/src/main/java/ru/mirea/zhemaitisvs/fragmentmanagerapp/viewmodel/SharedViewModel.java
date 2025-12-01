package ru.mirea.zhemaitisvs.fragmentmanagerapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ru.mirea.zhemaitisvs.fragmentmanagerapp.model.Sport;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Sport> selectedSport = new MutableLiveData<>();

    public void selectSport(Sport sport) {
        selectedSport.setValue(sport);
    }

    public MutableLiveData<Sport> getSelectedSport() {
        return selectedSport;
    }
}