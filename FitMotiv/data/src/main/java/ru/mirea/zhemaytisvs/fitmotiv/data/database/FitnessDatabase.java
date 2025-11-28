package ru.mirea.zhemaytisvs.fitmotiv.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.mirea.zhemaytisvs.fitmotiv.data.database.dao.WorkoutDao;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.entities.WorkoutEntity;

@Database(entities = {WorkoutEntity.class}, version = 2, exportSchema = false)
public abstract class FitnessDatabase extends RoomDatabase {
    private static volatile FitnessDatabase INSTANCE;
    
    public abstract WorkoutDao workoutDao();

    public static FitnessDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (FitnessDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FitnessDatabase.class,
                            "fitness_database"
                    )
                    .fallbackToDestructiveMigration() // в продакшене нужна миграция
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
