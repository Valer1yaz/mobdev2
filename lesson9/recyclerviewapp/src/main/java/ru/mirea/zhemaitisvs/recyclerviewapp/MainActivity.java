package ru.mirea.zhemaitisvs.recyclerviewapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<GarbageType> events = getListData();
        RecyclerView recyclerView = this.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new EventRecyclerViewAdapter(events));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private List<GarbageType> getListData() {
        List<GarbageType> list = new ArrayList<GarbageType>();

        list.add(new GarbageType("Макулатура",
                "Бумажные и картонные отходы, которые перерабатываются путем измельчения, очистки и превращения в новую бумажную продукцию",
                "waste_paper", 105));

        list.add(new GarbageType("Стеклобой",
                "Битое стекло, которое дробится, плавится и используется для производства новой стеклянной тары и строительных материалов",
                "glass_waste", 1500));

        list.add(new GarbageType("Полимерные отходы",
                "Пластиковые отходы, которые сортируются по типам пластика, измельчаются и переплавляются для создания новых изделий или химической переработки",
                "polymer_waste", 1907));

        list.add(new GarbageType("Металлолом",
                "Черные и цветные металлы, которые переплавляются в печах для производства нового металла с значительной экономией энергии и ресурсов",
                "metal_scrap", -3000));

        list.add(new GarbageType("Органические отходы",
                "Пищевые и растительные отходы, которые перерабатываются методами компостирования или анаэробного сбраживания с получением удобрений и биогаза",
                "organic_waste", -10000));

        return list;
    }
}