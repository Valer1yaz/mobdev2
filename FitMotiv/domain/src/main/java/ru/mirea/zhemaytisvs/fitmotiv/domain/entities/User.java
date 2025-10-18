package ru.mirea.zhemaytisvs.fitmotiv.domain.entities;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private boolean isGuest;

    public User(String uid, String email, String displayName, boolean isGuest) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.isGuest = isGuest;
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public boolean isGuest() { return isGuest; }

    // Guest user constructor
    public static User createGuestUser() {
        return new User("guest", "guest@example.com", "Гость", true);
    }
}