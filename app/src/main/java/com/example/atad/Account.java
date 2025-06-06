package com.example.atad;

public class Account {
    private String title;
    private String password;

    public Account() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account(String title, String password) {
        this.title = title;
        this.password = password;
    }

    public String getMaskedPassword() {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "••••••••";
    }
}