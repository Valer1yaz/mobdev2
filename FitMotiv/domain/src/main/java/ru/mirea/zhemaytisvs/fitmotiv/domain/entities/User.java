package ru.mirea.zhemaytisvs.fitmotiv.domain.entities;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl; // Добавлено
    private boolean isGuest;

    public User(String uid, String email, String displayName, boolean isGuest) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.isGuest = isGuest;
        this.photoUrl = null; // По умолчанию null
    }

    // Конструктор с photoUrl
    public User(String uid, String email, String displayName, String photoUrl, boolean isGuest) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.isGuest = isGuest;
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPhotoUrl() { return photoUrl; } // Добавлено
    public boolean isGuest() { return isGuest; }

    // Setters (опционально)
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    // Guest user constructor
    public static User createGuestUser() {
        return new User("guest", "guest@example.com", "Гость", true);
    }
}