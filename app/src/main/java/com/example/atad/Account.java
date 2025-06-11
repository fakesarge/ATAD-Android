package com.example.atad;

public class Account {
    private String title;
    private String password;
    private String websiteUrl;
    private boolean isBreached;

    // Constructor for new accounts
    public Account(String title, String password, String websiteUrl) {
        this.title = title;
        this.password = password;
        this.websiteUrl = websiteUrl;
        this.isBreached = false;
    }

    // Constructor with all fields - already made accounts
    public Account(String title, String password, String websiteUrl, boolean isBreached) {
        this.title = title;
        this.password = password;
        this.websiteUrl = websiteUrl;
        this.isBreached = isBreached;
    }

    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public boolean isBreached() {
        return isBreached;
    }


    public void setBreached(boolean breached) {
        isBreached = breached;
    }

    public String getMaskedPassword() {
        return "•••••••••";
    }
}