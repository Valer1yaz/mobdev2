package ru.mirea.zhemaitisvs.resultapifragmentapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DataFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.button_send);
        EditText editText = view.findViewById(R.id.edit_text_data);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("key", text);

                getParentFragmentManager().setFragmentResult("requestKey", bundle);

                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getParentFragmentManager(), "ModalBottomSheet");
            }
        });
    }
}
