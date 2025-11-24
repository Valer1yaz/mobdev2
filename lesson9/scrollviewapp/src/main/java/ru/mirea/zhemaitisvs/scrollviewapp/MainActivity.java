package ru.mirea.zhemaitisvs.scrollviewapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout wrapper = findViewById(R.id.wrapper);
        BigDecimal firstTerm = BigDecimal.ONE;
        BigDecimal ratio = new BigDecimal("2");

        for (int i = 1; i <= 100; i++) {
            BigDecimal term = firstTerm.multiply(ratio.pow(i - 1));
            View view = getLayoutInflater().inflate(R.layout.item, null, false);
            TextView text = view.findViewById(R.id.textView);
            text.setText(String.format("Элемент %d: %s", i, term.toString()));
            wrapper.addView(view);
        }
    }
}