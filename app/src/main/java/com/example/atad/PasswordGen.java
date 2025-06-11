package com.example.atad;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGen {
    private final SecureRandom random = new SecureRandom();

    // Character sets
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    /**
     * Generate password, generates a password based on the users choices.
     * @param length
     * @param includeUpper
     * @param includeLower
     * @param includeNumbers
     * @param includeSpecial
     * @return
     */
    public String generatePassword(int length, boolean includeUpper, boolean includeLower,
                                   boolean includeNumbers, boolean includeSpecial, boolean avoidSimilar) {

        //if lenght of the password is neg or 0
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be positive");
        }

        // Build character pool

        //i chosse char pool because it lets you append/remove the one object itself. In normal strings it creates new ones.
        StringBuilder charPool = new StringBuilder();
        if (includeLower) charPool.append(LOWERCASE);
        if (includeUpper) charPool.append(UPPERCASE);
        if (includeNumbers) charPool.append(NUMBERS);
        if (includeSpecial) charPool.append(SPECIAL);


        // if the user dosent check any boxes
        if (charPool.length() == 0) {
            throw new IllegalArgumentException("At least one character type must be selected");
        }




        // Ensure at least one character from each selected type
        List<Character> passwordChars = new ArrayList<>();
        if (includeLower) {
            passwordChars.add(getRandomChar(LOWERCASE));
        }
        if (includeUpper) {
            passwordChars.add(getRandomChar(UPPERCASE));
        }
        if (includeNumbers) {
            passwordChars.add(getRandomChar(NUMBERS));
        }
        if (includeSpecial) {
            passwordChars.add(getRandomChar(SPECIAL));
        }

        // Fill remaining characters
        while (passwordChars.size() < length) {
            passwordChars.add(getRandomChar(charPool.toString()));
        }

        // Shuffle the characters
        Collections.shuffle(passwordChars, random);

        // Build the final password
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private char getRandomChar(String charSet) {
        if (charSet.isEmpty()) {
            throw new IllegalStateException("No available characters after filtering");
        }
        return charSet.charAt(random.nextInt(charSet.length()));
    }


    //unchecking the box removes
    private String removeCharacters(String source, String charsToRemove) {
        StringBuilder result = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (charsToRemove.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }


}