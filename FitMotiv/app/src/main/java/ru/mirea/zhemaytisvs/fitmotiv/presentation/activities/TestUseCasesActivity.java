package ru.mirea.zhemaytisvs.fitmotiv.presentation.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.ProgressPhoto;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.UserGoal;
import ru.mirea.zhemaytisvs.fitmotiv.domain.entities.Workout;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.ProgressRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.UserRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.WorkoutRepository;
import ru.mirea.zhemaytisvs.fitmotiv.domain.usercases.*;
import ru.mirea.zhemaytisvs.fitmotiv.data.repositories.*;

import java.util.Date;
import java.util.List;

public class TestUseCasesActivity extends AppCompatActivity {

    private static final String TAG = "UseCasesTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Запускаем тесты
        testAllUseCases();
    }

    private void testAllUseCases() {
        Log.d(TAG, "=== НАЧАЛО ТЕСТИРОВАНИЯ USE CASES ===");

        // Инициализация репозиториев
        WorkoutRepository workoutRepository = new WorkoutRepositoryImpl();
        QuoteRepository quoteRepository = new QuoteRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        ProgressRepository progressRepository = new ProgressRepositoryImpl();

        // Тест 1: TrackWorkoutUseCase
        testTrackWorkoutUseCase(workoutRepository);

        // Тест 2: GetWorkoutHistoryUseCase
        testGetWorkoutHistoryUseCase(workoutRepository);

        // Тест 3: GetMotivationalQuoteUseCase
        testGetMotivationalQuoteUseCase(quoteRepository);

        // Тест 4: SetGoalUseCase
        testSetGoalUseCase(userRepository);

        // Тест 5: GetProgressPhotosUseCase
        testGetProgressPhotosUseCase(progressRepository);

        // Тест 6: AnalyzeExerciseUseCase
        testAnalyzeExerciseUseCase(workoutRepository);

        Log.d(TAG, "=== ЗАВЕРШЕНИЕ ТЕСТИРОВАНИЯ USE CASES ===");
    }

    private void testTrackWorkoutUseCase(WorkoutRepository workoutRepository) {
        Log.d(TAG, "--- Тест TrackWorkoutUseCase ---");

        try {
            TrackWorkoutUseCase useCase = new TrackWorkoutUseCase(workoutRepository);

            // Создаем тестовую тренировку
            Workout testWorkout = new Workout(
                    "test_" + System.currentTimeMillis(),
                    Workout.WorkoutType.CARDIO,
                    45,
                    350,
                    new Date(),
                    "Тестовая пробежка"
            );

            // Выполняем use case
            useCase.execute(testWorkout);

            Log.d(TAG, "✅ TrackWorkoutUseCase: Тренировка успешно добавлена");
            Log.d(TAG, "   - Тип: " + testWorkout.getType());
            Log.d(TAG, "   - Длительность: " + testWorkout.getDuration() + " мин");
            Log.d(TAG, "   - Калории: " + testWorkout.getCalories());

        } catch (Exception e) {
            Log.e(TAG, "❌ TrackWorkoutUseCase ОШИБКА: " + e.getMessage());
        }
    }

    private void testGetWorkoutHistoryUseCase(WorkoutRepository workoutRepository) {
        Log.d(TAG, "--- Тест GetWorkoutHistoryUseCase ---");

        try {
            GetWorkoutHistoryUseCase useCase = new GetWorkoutHistoryUseCase(workoutRepository);

            // Выполняем use case
            List<Workout> workouts = useCase.execute();

            Log.d(TAG, "✅ GetWorkoutHistoryUseCase: Успешно получено " + workouts.size() + " тренировок");

            for (int i = 0; i < workouts.size(); i++) {
                Workout workout = workouts.get(i);
                Log.d(TAG, "   Тренировка " + (i + 1) + ": " + workout.getType() +
                        " - " + workout.getDuration() + " мин, " + workout.getCalories() + " калорий");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ GetWorkoutHistoryUseCase ОШИБКА: " + e.getMessage());
        }
    }

    private void testGetMotivationalQuoteUseCase(QuoteRepository quoteRepository) {
        Log.d(TAG, "--- Тест GetMotivationalQuoteUseCase ---");

        try {
            GetMotivationalQuoteUseCase useCase = new GetMotivationalQuoteUseCase(quoteRepository);

            // Выполняем use case несколько раз для демонстрации
            for (int i = 1; i <= 3; i++) {
                String quote = useCase.execute();
                Log.d(TAG, "✅ GetMotivationalQuoteUseCase (цитата " + i + "): " + quote);
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ GetMotivationalQuoteUseCase ОШИБКА: " + e.getMessage());
        }
    }

    private void testSetGoalUseCase(UserRepository userRepository) {
        Log.d(TAG, "--- Тест SetGoalUseCase ---");

        try {
            SetGoalUseCase useCase = new SetGoalUseCase(userRepository);

            // Создаем тестовую цель
            UserGoal testGoal = new UserGoal(
                    "test_goal_1",
                    75,
                    5,
                    "Похудеть на 5 кг за 2 месяца",
                    false
            );

            // Выполняем use case
            useCase.execute(testGoal);

            Log.d(TAG, "✅ SetGoalUseCase: Цель успешно установлена");
            Log.d(TAG, "   - Описание: " + testGoal.getDescription());
            Log.d(TAG, "   - Целевой вес: " + testGoal.getTargetWeight() + " кг");
            Log.d(TAG, "   - Тренировок в неделю: " + testGoal.getWorkoutsPerWeek());

            // Проверяем получение цели
            UserGoal savedGoal = userRepository.getUserGoal();
            Log.d(TAG, "   Проверка получения: " + savedGoal.getDescription());

        } catch (Exception e) {
            Log.e(TAG, "❌ SetGoalUseCase ОШИБКА: " + e.getMessage());
        }
    }

    private void testGetProgressPhotosUseCase(ProgressRepository progressRepository) {
        Log.d(TAG, "--- Тест GetProgressPhotosUseCase ---");

        try {
            GetProgressPhotosUseCase useCase = new GetProgressPhotosUseCase(progressRepository);

            // Выполняем use case
            List<ProgressPhoto> photos = useCase.execute();

            Log.d(TAG, "✅ GetProgressPhotosUseCase: Успешно получено " + photos.size() + " фото");

            for (int i = 0; i < photos.size(); i++) {
                ProgressPhoto photo = photos.get(i);
                Log.d(TAG, "   Фото " + (i + 1) + ": " + photo.getDescription() +
                        " (" + new Date(photo.getDate().getTime()).toString() + ")" +
                        " - ❤️ " + photo.getLikes());
            }

            // Тест добавления нового фото
            ProgressPhoto newPhoto = new ProgressPhoto(
                    "test_photo_" + System.currentTimeMillis(),
                    "https://example.com/test.jpg",
                    "Тестовое фото прогресса",
                    new Date(),
                    0
            );

            progressRepository.saveProgressPhoto(newPhoto);
            Log.d(TAG, "✅ Добавлено тестовое фото: " + newPhoto.getDescription());

        } catch (Exception e) {
            Log.e(TAG, "❌ GetProgressPhotosUseCase ОШИБКА: " + e.getMessage());
        }
    }

    private void testAnalyzeExerciseUseCase(WorkoutRepository workoutRepository) {
        Log.d(TAG, "--- Тест AnalyzeExerciseUseCase ---");

        try {
            AnalyzeExerciseUseCase useCase = new AnalyzeExerciseUseCase(workoutRepository);

            // Создаем тестовые данные для анализа
            byte[] mockImageData = new byte[100]; // Mock данные изображения

            // Тестируем разные упражнения
            String[] exercises = {"Приседания", "Отжимания", "Планка"};

            for (String exercise : exercises) {
                var analysis = useCase.execute(exercise, mockImageData);

                Log.d(TAG, "✅ AnalyzeExerciseUseCase для '" + exercise + "':");
                Log.d(TAG, "   - Оценка правильности: " + analysis.getCorrectnessScore() + "%");
                Log.d(TAG, "   - Рекомендации: " + analysis.getFeedback());
                Log.d(TAG, "   - Техника правильная: " + analysis.isCorrect());
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ AnalyzeExerciseUseCase ОШИБКА: " + e.getMessage());
        }
    }
}