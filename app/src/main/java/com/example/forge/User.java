package com.example.forge;

public class User {
    private String username;
    private String email;
    private boolean isSelected;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.isSelected = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
