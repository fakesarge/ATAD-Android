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

    Boolean leaked;
    public boolean search(String password) throws Exception {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Get passwords siffix and prefix
                String hashedPassword = null;
                try {
                    hashedPassword = hashPasswordSHA1(password);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String pf = hashedPassword.substring(0, 5);
                String sf = hashedPassword.substring(5);


                String apiUrl = "https://api.pwnedpasswords.com/range/" + pf;

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    throw new RuntimeException(e);
                }

                //Read the results of the API returning
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String line;

                // Get the data and check if the password has been leaked

                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (line.startsWith(sf.toUpperCase())) {
                        leaked = true;
                        break;
                    }
                }

                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        return leaked;
    }


    // HASH THE PASSWORD - Not my code
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
