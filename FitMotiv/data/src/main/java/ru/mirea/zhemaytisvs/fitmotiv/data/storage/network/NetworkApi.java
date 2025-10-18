package ru.mirea.zhemaytisvs.fitmotiv.data.storage.network;

import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.QuoteResponse;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.WorkoutResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NetworkApi {
    private Random random = new Random();

    // Mock данные для цитат
    private QuoteResponse[] mockQuotes = {
            new QuoteResponse("Сила не в том, чтобы никогда не падать, а в том, чтобы подниматься каждый раз, когда падаешь.", "Автор неизвестен"),
            new QuoteResponse("Ваше тело может вынести практически anything. Это ваш разум, который вам нужно убедить.", "Арнольд Шварценеггер"),
            new QuoteResponse("Не ждите идеальных условий для начала. Начните сейчас и сделайте условия идеальными.", "Джим Рон"),
            new QuoteResponse("Тренировка - это не наказание, это праздник того, что ваше тело может делать.", "Неизвестный автор"),
            new QuoteResponse("Единственная плохая тренировка - это та, которой не было.", "Неизвестный автор"),
            new QuoteResponse("Если что-то мечтаешь сделать - не жди. Сделай сегодня, ведь через год тебе будет жаль, что не начал сегодня.", "Неизвестный автор")
    };

    // Mock данные для тренировок
    private WorkoutResponse[] mockWorkouts = {
            new WorkoutResponse("net_1", "CARDIO", 30, 250, new Date(), "Утренняя пробежка"),
            new WorkoutResponse("net_2", "STRENGTH", 45, 350, new Date(), "Силовая тренировка"),
            new WorkoutResponse("net_3", "YOGA", 60, 200, new Date(), "Йога для расслабления")
    };

    public QuoteResponse getMotivationalQuote() {
        // Имитация сетевой задержки
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Возвращаем случайную цитату
        return mockQuotes[random.nextInt(mockQuotes.length)];
    }

    public List<WorkoutResponse> getWorkoutsFromCloud() {
        // Имитация сетевой задержки
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Возвращаем mock данные тренировок
        List<WorkoutResponse> workouts = new ArrayList<>();
        for (WorkoutResponse workout : mockWorkouts) {
            workouts.add(workout);
        }
        return workouts;
    }

    public boolean syncWorkoutToCloud(WorkoutResponse workout) {
        // Имитация отправки данных в облако
        try {
            Thread.sleep(300);
            return true; // Всегда успешно в mock реализации
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}