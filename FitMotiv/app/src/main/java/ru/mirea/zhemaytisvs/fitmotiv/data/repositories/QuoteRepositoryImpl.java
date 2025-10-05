package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import java.util.Random;

public class QuoteRepositoryImpl implements QuoteRepository {
    private String[] testQuotes = {
            "Сила не в том, чтобы никогда не падать, а в том, чтобы подниматься каждый раз, когда падаешь.",
            "Ваше тело может вынести практически anything. Это ваш разум, который вам нужно убедить.",
            "Не ждите идеальных условий для начала. Начните сейчас и сделайте условия идеальными.",
            "Тренировка - это не наказание, это праздник того, что ваше тело может делать.",
            "Единственная плохая тренировка - это та, которой не было."
    };

    private Random random = new Random();

    @Override
    public String getMotivationalQuote() {
        return testQuotes[random.nextInt(testQuotes.length)];
    }
}