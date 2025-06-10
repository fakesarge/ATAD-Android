package com.example.atad;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGen {
    private static final SecureRandom random = new SecureRandom();

    // Character sets
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String SIMILAR_CHARS = "il1Lo0O";

    /**
     * Generates a password with customizable options
     *
     * @param length Length of the password
     * @param includeUpper Include uppercase letters
     * @param includeLower Include lowercase letters
     * @param includeNumbers Include numbers
     * @param includeSpecial Include special characters
     * @param avoidSimilar Avoid similar characters (i,l,1,L,o,0,O)
     * @return Generated password
     */
    public static String generatePassword(int length, boolean includeUpper,
                                          boolean includeLower, boolean includeNumbers,
                                          boolean includeSpecial, boolean avoidSimilar) {

        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be positive");
        }

        // Build character pool based on options
        StringBuilder charPool = new StringBuilder();
        if (includeLower) charPool.append(LOWERCASE);
        if (includeUpper) charPool.append(UPPERCASE);
        if (includeNumbers) charPool.append(NUMBERS);
        if (includeSpecial) charPool.append(SPECIAL);

        if (charPool.length() == 0) {
            throw new IllegalArgumentException("At least one character type must be selected");
        }

        // Remove similar characters if requested
        if (avoidSimilar) {
            String pool = charPool.toString();
            pool = removeCharacters(pool, SIMILAR_CHARS);
            charPool = new StringBuilder(pool);
        }

        // Ensure at least one character from each selected type is included
        List<Character> passwordChars = new ArrayList<>();
        if (includeLower) {
            passwordChars.add(getRandomChar(LOWERCASE, avoidSimilar));
        }
        if (includeUpper) {
            passwordChars.add(getRandomChar(UPPERCASE, avoidSimilar));
        }
        if (includeNumbers) {
            passwordChars.add(getRandomChar(NUMBERS, avoidSimilar));
        }
        if (includeSpecial) {
            passwordChars.add(getRandomChar(SPECIAL, avoidSimilar));
        }

        // Fill the rest of the password with random characters
        while (passwordChars.size() < length) {
            passwordChars.add(getRandomChar(charPool.toString(), false));
        }

        // Shuffle to avoid predictable patterns
        Collections.shuffle(passwordChars, random);

        // Convert to String
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private static char getRandomChar(String charSet, boolean avoidSimilar) {
        if (avoidSimilar) {
            charSet = removeCharacters(charSet, SIMILAR_CHARS);
        }

        if (charSet.isEmpty()) {
            throw new IllegalStateException("No available characters after filtering");
        }

        return charSet.charAt(random.nextInt(charSet.length()));
    }

    private static String removeCharacters(String source, String charsToRemove) {
        StringBuilder result = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (charsToRemove.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Generates a passphrase with customizable options
     *
     * @param wordCount Number of words in the passphrase
     * @param separator Word separator character
     * @param capitalize Capitalize each word
     * @param includeNumber Include a random number
     * @return Generated passphrase
     */
    public static String generatePassphrase(int wordCount, char separator,
                                            boolean capitalize, boolean includeNumber) {
        // In a real implementation, you would use a word list
        // Here's a simplified version with a small sample word list
        String[] wordList = {
                "apple", "banana", "carrot", "dog", "elephant", "flower",
                "giraffe", "house", "island", "jungle", "kangaroo", "lion",
                "mountain", "notebook", "ocean", "pencil", "queen", "river",
                "sun", "tiger", "umbrella", "violin", "water", "xylophone",
                "yellow", "zebra"
        };

        if (wordCount <= 0) {
            throw new IllegalArgumentException("Word count must be positive");
        }

        StringBuilder passphrase = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            String word = wordList[random.nextInt(wordList.length)];

            if (capitalize) {
                word = word.substring(0, 1).toUpperCase() + word.substring(1);
            }

            passphrase.append(word);

            if (i < wordCount - 1) {
                passphrase.append(separator);
            }
        }

        if (includeNumber) {
            passphrase.append(random.nextInt(90) + 10); // Append a 2-digit number
        }

        return passphrase.toString();
    }

    public static void main(String[] args) {
        // Example usage
        System.out.println("Random password:");
        System.out.println(generatePassword(16, true, true, true, true, true));

        System.out.println("\nPassphrase:");
        System.out.println(generatePassphrase(4, '-', true, true));

        System.out.println("\nDifferent configurations:");
        System.out.println("Numbers only: " + generatePassword(8, false, false, true, false, false));
        System.out.println("Letters only: " + generatePassword(12, true, true, false, false, true));
        System.out.println("Special chars only: " + generatePassword(10, false, false, false, true, false));
    }
}