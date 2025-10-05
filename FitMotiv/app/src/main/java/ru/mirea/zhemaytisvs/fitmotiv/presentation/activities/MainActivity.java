package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Фитнес-Мотиватор запущен!");
        setContentView(textView);
    }
}