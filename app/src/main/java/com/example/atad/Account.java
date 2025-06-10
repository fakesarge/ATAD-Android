package com.example.atad;

/**
 * Represents a user account with password and breach status
 */
public class Account {
    private String title;       // Website/App name (e.g. "Google")
    private String password;    // The stored password
    private boolean isBreached; // Flag if password was found in breaches

    // Constructor for new accounts (defaults to not breached)
    public Account(String title, String password) {
        this.title = title;
        this.password = password;
        this.isBreached = false;
    }

    // Constructor with explicit breach status
    public Account(String title, String password, boolean isBreached) {
        this.title = title;
        this.password = password;
        this.isBreached = isBreached;
    }

    // --- Getters and Setters ---
    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }

    public boolean isBreached() {
        return isBreached;
    }

    public void setBreached(boolean breached) {
        isBreached = breached;
    }

    // Returns masked password for display (e.g. "•••••••••")
    public String getMaskedPassword() {
        return "•••••••••";
    }
}