package ru.mirea.zhemaitisvs.retrofitapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {
    private LayoutInflater layoutInflater;
    private List<Todo> todos;
    private ApiService apiService;
    private Context context;
    private final String[] IMAGE_URLS = {
            "https://avatars.mds.yandex.net/i?id=0f0ce4e516bc6dee6e0a7304476ff3d2_l-5233129-images-thumbs&n=13",
            "https://suckhoedoisong.qltns.mediacdn.vn/324455921873985536/2025/7/3/joven-morena-delgada-con-uniforme-deportivo-haciendo-yoga-en-la-terraza-de-una-casa-junto-al-jardin-1751514861663967846350.jpg",
            "https://cs9.pikabu.ru/post_img/2020/07/17/8/159499340263760733.jpg",
            "https://silver.ru/upload/iblock/3ab/3ab495dfc8b623d2c39e2519683ed1be.jpg",
            "https://101hairtips.com/wp-content/uploads/f/0/7/f0783c7db47f4f95bbfe635a4bbae146.jpeg",
            "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AA1xnkV4.img?w=1600&h=1000&m=4&q=100"
    };

    public TodoAdapter(Context context, List<Todo> todoList, ApiService apiService) {
        this.layoutInflater = LayoutInflater.from(context);
        this.todos = todoList;
        this.apiService = apiService;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.textViewTitle.setText(todo.getTitle());
        holder.checkBoxCompleted.setChecked(Boolean.TRUE.equals(todo.getCompleted()));
        loadImageWithPicasso(holder.imageView, todo.getId());
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    todos.get(adapterPosition).setCompleted(isChecked);
                    updateTodoOnServer(todos.get(adapterPosition), adapterPosition);
                }
            }
        });
    }

    private void loadImageWithPicasso(ImageView imageView, Integer todoId) {
        String imageUrl = IMAGE_URLS[todoId % IMAGE_URLS.length];

        Log.d("Picasso", "Loading image: " + imageUrl);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .resize(100, 100)
                .centerCrop()
                .into(imageView);
    }

    private void updateTodoOnServer(Todo todo, int position) {
        Todo updateTodo = new Todo();
        updateTodo.setUserId(todo.getUserId());
        updateTodo.setId(todo.getId());
        updateTodo.setTitle(todo.getTitle());
        updateTodo.setCompleted(todo.getCompleted());

        Call<Todo> call = apiService.updateTodo(todo.getId(), updateTodo);
        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                if (response.isSuccessful()) {
                    Log.d("TodoAdapter", "Todo updated successfully: " + todo.getId());
                    Toast.makeText(context, "Todo updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TodoAdapter", "Failed to update todo: " + response.code());
                    Toast.makeText(context, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    restorePreviousState(position);
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Log.e("TodoAdapter", "Network error: " + t.getMessage());
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();

                restorePreviousState(position);
            }
        });
    }

    private void restorePreviousState(int position) {
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }
}