package ru.mirea.zhemaitisvs.retrofitapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.GET;

public interface ApiService {
    @GET("todos")
    Call<List<Todo>> getTodos();
    @PUT("todos/{id}")
    Call<Todo> updateTodo(@Path("id") Integer id, @Body Todo todo);
}
