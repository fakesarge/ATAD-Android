package com.example.atad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LeakAPI {

    public interface LeakCheckCallback {
        void onResult(boolean isLeaked);
        void onError(Exception e);
    }

    public void search(String password, LeakCheckCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get passwords suffix and prefix
                    String hashedPassword = hashPasswordSHA1(password);
                    String prefix = hashedPassword.substring(0, 5);
                    String suffix = hashedPassword.substring(5);

                    String apiUrl = "https://api.pwnedpasswords.com/range/" + prefix;

                    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                    connection.setRequestMethod("GET");

                    // Read the results of the API returning
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    boolean isLeaked = false;

                    // Get the data and check if the password has been leaked
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith(suffix.toUpperCase())) {
                            isLeaked = true;
                            break;
                        }
                    }

                    reader.close();
                    callback.onResult(isLeaked);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        }).start();
    }

    // HASH THE PASSWORD
    private static String hashPasswordSHA1(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString().toUpperCase();
    }
}

