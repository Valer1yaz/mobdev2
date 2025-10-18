package ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models;

public class QuoteResponse {
    private String content;
    private String author;

    public QuoteResponse(String content, String author) {
        this.content = content;
        this.author = author;
    }

    // Getters
    public String getContent() { return content; }
    public String getAuthor() { return author; }

    // Setters (нужны для десериализации JSON)
    public void setContent(String content) { this.content = content; }
    public void setAuthor(String author) { this.author = author; }

    // Пустой конструктор для Gson/Jackson
    public QuoteResponse() {}
}