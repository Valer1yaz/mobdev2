package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.NetworkApi;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.QuoteResponse;

public class QuoteRepositoryImpl implements QuoteRepository {
    private final NetworkApi networkApi;
    private String[] fallbackQuotes = {
            "Сила не в том, чтобы никогда не падать, а в том, чтобы подниматься каждый раз, когда падаешь.",
            "Ваше тело может вынести практически anything. Это ваш разум, который вам нужно убедить.",
            "Не ждите идеальных условий для начала. Начните сейчас и сделайте условия идеальными.",
            "Тренировка - это не наказание, это праздник того, что ваше тело может делать.",
            "Единственная плохая тренировка - это та, которой не было."
    };

    public QuoteRepositoryImpl() {
        this.networkApi = new NetworkApi();
    }

    @Override
    public String getMotivationalQuote() {
        try {
            // Пытаемся получить цитату из сети
            QuoteResponse response = networkApi.getMotivationalQuote();
            return response.getContent() + " - " + response.getAuthor();
        } catch (Exception e) {
            // Fallback на локальные цитаты при ошибке сети
            return fallbackQuotes[new java.util.Random().nextInt(fallbackQuotes.length)];
        }
    }
}