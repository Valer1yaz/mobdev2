package ru.mirea.zhemaitisvs.fragmentmanagerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ru.mirea.zhemaitisvs.fragmentmanagerapp.model.Sport;
import ru.mirea.zhemaitisvs.fragmentmanagerapp.viewmodel.SharedViewModel;

public class SportDetailsFragment extends Fragment {

    private SharedViewModel viewModel;
    private TextView textName, textCountry, textDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sport_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        textName = view.findViewById(R.id.text_sport_name);
        textCountry = view.findViewById(R.id.text_sport_country);
        textDescription = view.findViewById(R.id.text_sport_description);

        viewModel.getSelectedSport().observe(getViewLifecycleOwner(), new Observer<Sport>() {
            @Override
            public void onChanged(Sport sport) {
                if (sport != null) {
                    textName.setText(sport.getName());
                    textCountry.setText("Страна: " + sport.getCountry());
                    textDescription.setText(sport.getDescription());
                }
            }
        });
    }
}
