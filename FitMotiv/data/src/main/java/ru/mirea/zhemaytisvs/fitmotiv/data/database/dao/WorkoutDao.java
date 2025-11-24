package ru.mirea.zhemaytisvs.fitmotiv.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import ru.mirea.zhemaytisvs.fitmotiv.data.database.entities.WorkoutEntity;

@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    LiveData<List<WorkoutEntity>> getAllWorkouts();
    
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    List<WorkoutEntity> getAllWorkoutsSync();
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    WorkoutEntity getWorkoutById(String id);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkout(WorkoutEntity workout);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkouts(List<WorkoutEntity> workouts);
    
    @Query("DELETE FROM workouts WHERE id = :id")
    void deleteWorkout(String id);
    
    @Query("DELETE FROM workouts")
    void deleteAllWorkouts();
}
