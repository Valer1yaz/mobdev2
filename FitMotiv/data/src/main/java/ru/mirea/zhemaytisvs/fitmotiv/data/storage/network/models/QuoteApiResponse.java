package ru.mirea.zhemaytisvs.fitmotiv.data.storage.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Модель ответа от API forismatic.com
 */
public class QuoteApiResponse {
    @SerializedName("quoteText")
    private String quoteText;
    
    @SerializedName("quoteAuthor")
    private String quoteAuthor;
    
    @SerializedName("senderName")
    private String senderName;
    
    @SerializedName("senderLink")
    private String senderLink;
    
    @SerializedName("quoteLink")
    private String quoteLink;

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteAuthor() {
        return quoteAuthor != null && !quoteAuthor.isEmpty() ? quoteAuthor : "Неизвестный автор";
    }

    public void setQuoteAuthor(String quoteAuthor) {
        this.quoteAuthor = quoteAuthor;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLink() {
        return senderLink;
    }

    public void setSenderLink(String senderLink) {
        this.senderLink = senderLink;
    }

    public String getQuoteLink() {
        return quoteLink;
    }

    public void setQuoteLink(String quoteLink) {
        this.quoteLink = quoteLink;
    }
}

