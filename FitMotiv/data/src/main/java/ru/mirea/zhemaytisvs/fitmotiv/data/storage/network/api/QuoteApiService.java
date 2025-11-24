package ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.QuoteApiResponse;

/**
 * API сервис для получения мотивационных цитат на русском языке
 * Использует бесплатный API: https://api.forismatic.com/api/1.0/
 */
public interface QuoteApiService {
    @GET("api/1.0/")
    Call<QuoteApiResponse> getQuote(
            @Query("method") String method,
            @Query("format") String format,
            @Query("lang") String lang
    );
}

