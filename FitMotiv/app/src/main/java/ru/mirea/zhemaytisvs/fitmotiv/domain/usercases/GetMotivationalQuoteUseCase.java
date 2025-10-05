package ru.mirea.zhemaytisvs.fitmotiv.domain.usercases;

import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;

public class GetMotivationalQuoteUseCase {
    private final QuoteRepository quoteRepository;

    public GetMotivationalQuoteUseCase(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public String execute() {
        return quoteRepository.getMotivationalQuote();
    }
}