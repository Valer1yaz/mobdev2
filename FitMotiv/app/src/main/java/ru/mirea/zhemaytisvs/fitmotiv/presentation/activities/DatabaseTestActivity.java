// DatabaseTestActivity.java
package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.FitnessDatabase;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.entities.WorkoutEntity;

public class DatabaseTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(() -> {
            try {
                FitnessDatabase db = FitnessDatabase.getDatabase(this);
                Log.d("DatabaseTest", "=== DATABASE TEST START ===");

                // Получить все записи
                java.util.List<WorkoutEntity> allWorkouts = db.workoutDao().getAllWorkoutsSync();
                Log.d("DatabaseTest", "Total records in workouts table: " + allWorkouts.size());

                for (WorkoutEntity entity : allWorkouts) {
                    Log.d("DatabaseTest", "Record - ID: " + entity.id +
                            ", UserID: " + entity.userId +
                            ", Type: " + entity.type +
                            ", Desc: " + entity.description);
                }

                Log.d("DatabaseTest", "=== DATABASE TEST END ===");
            } catch (Exception e) {
                Log.e("DatabaseTest", "Error testing database", e);
            }
        }).start();

        finish();
    }
}