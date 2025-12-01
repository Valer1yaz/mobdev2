package ru.mirea.zhemaitisvs.fragmentmanagerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import ru.mirea.zhemaitisvs.fragmentmanagerapp.model.Sport;
import ru.mirea.zhemaitisvs.fragmentmanagerapp.viewmodel.SharedViewModel;
import java.util.Arrays;
import java.util.List;

public class SportListFragment extends Fragment {

    private SharedViewModel viewModel;
    private List<Sport> sports = Arrays.asList(
            new Sport("Футбол", "Англия", "Командный вид спорта с мячом"),
            new Sport("Баскетбол", "США", "Игра с мячом и кольцами"),
            new Sport("Хоккей", "Канада", "Игра на льду с шайбой")
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sport_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        ListView listView = view.findViewById(R.id.list_view_sports);
        ArrayAdapter<Sport> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                sports
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectSport(sports.get(position));
            }
        });
    }
}
