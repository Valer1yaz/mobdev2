package ru.mirea.zhemaytisvs.fitmotiv.data.repositories;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mirea.zhemaytisvs.fitmotiv.domain.repositories.QuoteRepository;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.NetworkApi;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.api.QuoteApiService;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.QuoteApiResponse;
import ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models.QuoteResponse;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class QuoteRepositoryImpl implements QuoteRepository {
    private static final String TAG = "QuoteRepositoryImpl";
    private static final String BASE_URL = "https://api.forismatic.com/";
    
    private final NetworkApi networkApi;
    private final QuoteApiService quoteApiService;
    private final Retrofit retrofit;
    
    private String[] fallbackQuotes = {
            "Сила не в том, чтобы никогда не падать, а в том, чтобы подниматься каждый раз, когда падаешь.",
            "Ваше тело может вынести практически всё. Это ваш разум, который вам нужно убедить.",
            "Не ждите идеальных условий для начала. Начните сейчас и сделайте условия идеальными.",
            "Тренировка - это не наказание, это праздник того, что ваше тело может делать.",
            "Единственная плохая тренировка - это та, которой не было.",
            "Успех - это способность идти от неудачи к неудаче, не теряя энтузиазма.",
            "Победители никогда не сдаются, а сдающиеся никогда не побеждают."
    };

    public QuoteRepositoryImpl() {
        this.networkApi = new NetworkApi();
        
        // Настройка OkHttpClient с таймаутами для оптимизации
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        
        // Инициализация Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        quoteApiService = retrofit.create(QuoteApiService.class);
    }

    @Override
    public String getMotivationalQuote() {
        try {
            // Пытаемся получить цитату на русском языке из API forismatic.com
            Call<QuoteApiResponse> call = quoteApiService.getQuote("getQuote", "json", "ru");
            Response<QuoteApiResponse> response = call.execute();
            
            if (response.isSuccessful() && response.body() != null) {
                QuoteApiResponse apiResponse = response.body();
                String quote = apiResponse.getQuoteText();
                String author = apiResponse.getQuoteAuthor();
                
                if (quote != null && !quote.trim().isEmpty()) {
                    return quote + " - " + author;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении цитаты из API", e);
        }
        
        // Fallback: сначала пробуем mock API, затем локальные цитаты
        try {
            QuoteResponse mockResponse = networkApi.getMotivationalQuote();
            return mockResponse.getContent() + " - " + mockResponse.getAuthor();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении mock цитаты", e);
            return fallbackQuotes[new java.util.Random().nextInt(fallbackQuotes.length)];
        }
    }
}