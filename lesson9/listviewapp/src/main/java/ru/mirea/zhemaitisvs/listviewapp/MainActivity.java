package ru.mirea.zhemaitisvs.listviewapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Book> books = new ArrayList<>();
// Минимализм как философия жизни
        books.add(new Book("Мари Кондо", "Магическая уборка. Японское искусство наведения порядка дома и в жизни"));
        books.add(new Book("Фумио Сасаки", "Хорошийbye, вещи. Минимализм по-японски"));
        books.add(new Book("Грег МакКеон", "Эссенциализм. Путь к простоте"));
        books.add(new Book("Доминик Лоро", "Искусство жить просто. Как избавиться от лишнего и обогатить свою жизнь"));
        books.add(new Book("Джошуа Бекер", "Меньше значит больше. Минимализм как путь к счастливой и осмысленной жизни"));
        books.add(new Book("Джошуа Филдс Милберн", "Вся жизнь в чемодане. Как я избавился от лишнего и обрел себя"));
        books.add(new Book("Райан Никодемус", "Минимализм на практике. Как перестать покупать ненужное и начать жить"));
        books.add(new Book("Лео Бабаута", "Путь дзен. Как сделать жизнь проще и счастливее"));
        books.add(new Book("Колин Райт", "На пределе. История человека, который жил с 51 вещью"));
        books.add(new Book("Кортни Карвер", "Проект 333. Мода без жертв: всего 33 вещи на 3 месяца"));
        books.add(new Book("Эвелин Триш", "Минимализм для семьи. Как навести порядок в доме и в голове"));
        books.add(new Book("Кристин Вандер", "Минимализм с детьми. Как жить проще и радостнее всей семьей"));
        books.add(new Book("Эми Ирвинг", "Минимализм в большом городе"));
        books.add(new Book("Фрэнсин Джей", "Радость меньшегоПочему меньше - значит больше"));
        books.add(new Book("Брайан Гарднер", "Минимализм в бизнесе. Как работать меньше, а делать больше"));
        books.add(new Book("Эрин Бойл", "Простая жизнь. Руководство по осознанному потреблению"));
        books.add(new Book("Каспар Харрингтон", "Минимализм и финансы. Как экономить, копить и быть счастливым"));
        books.add(new Book("Като Натсуко", "Японский дом без лишнего. Искусство жить в гармонии с собой"));
        books.add(new Book("Марк Бойл", "Путь без вещей. Жизнь без денег в потребительском обществе"));
        books.add(new Book("Робин Грин", "Минимализм и творчество. Как освободить пространство для вдохновения"));

// Практическое расхламление и организация пространства
        books.add(new Book("Мари Кондо", "Искры радости. Магическая уборка в иллюстрациях"));
        books.add(new Book("Дэна К. Уайт", "Как навести порядок в доме и изменить жизнь"));
        books.add(new Book("Дженнифер Л. Скотт", "Уроки мадам Шик. 10 стильных привычек, которые наведут порядок в вашей жизни"));
        books.add(new Book("Гейл Бланк", "Одноминутный организатор. Простые решения для порядка дома"));
        books.add(new Book("Джули Моргенстерн", "Организация пространства по методу КонМари"));
        books.add(new Book("Карен Кингстон", "Избавьтесь от захламленности и найдите свою судьбу"));
        books.add(new Book("Петер Уолш", "Разбери свой хлам и пойми, чего ты хочешь от жизни"));
        books.add(new Book("Бекка Бордман", "Расхламление с детьми. Как навести порядок в детской"));
        books.add(new Book("Мелисса Майер", "Быстрое расхламление за 15 минут в день"));
        books.add(new Book("Сьюзи Мур", "Расхламление для новичков. С чего начать"));
        books.add(new Book("Элисон Форбс", "Пошаговое руководство по расхламлению каждого помещения"));
        books.add(new Book("Тереза Рут", "Расхламление гардероба. Как создать капсулу на все случаи жизни"));
        books.add(new Book("Джейми Новак", "Организуй это! Простые стратегии для порядка в доме"));
        books.add(new Book("Лора Вандеркам", "168 часов. У вас больше времени, чем вы думаете"));
        books.add(new Book("Марла Силли", "Система Флайледи. Как избавиться от хаоса в доме"));
        books.add(new Book("Сэнди Дорин", "Расхламление цифрового пространства"));
        books.add(new Book("Фрэнк Бьюкенен", "Порядок в гараже и на даче"));
        books.add(new Book("Донна Смоллин", "Организация маленького пространства"));
        books.add(new Book("Лиза Вудрафф", "Организация домашнего офиса"));
        books.add(new Book("Моника Фризи", "Расхламление по зонам. План на 30 дней"));


        ListView booksListView = findViewById(R.id.books_list_view);
        ArrayAdapter<Book> adapter = new ArrayAdapter<Book>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                books) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);
                Book book = getItem(position);
                text1.setText(book.getTitle());
                text2.setText(book.getAuthor());
                return view;
            }
        };

        booksListView.setAdapter(adapter);
    }
}