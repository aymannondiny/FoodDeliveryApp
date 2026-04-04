package com.fooddelivery.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

// Shared utility methods used across services.
public final class AppUtils {

    private AppUtils() {}

    // Generate a short random ID prefixed with a type hint.
    public static String generateId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    //** SHA-256 hex hash of a plain-text password. */
    public static String hashPassword(String plainText) {
        try {
            //telling Java to use the SHA-256 (Secure Hash Algorithm 256-bit).
            // It is a standard "one-way" cryptographic function. No matter how long the password is (5 characters or 5,000),
            // SHA-256 will always produce a result that is exactly 256 bits (32 bytes) long.
            MessageDigest md     = MessageDigest.getInstance("SHA-256");

            //Computers don't hash letters; they hash numbers. This
            // converts your string (e.g., "Password123") into a sequence of bytes based on the UTF-8 standard.
            byte[]        hash   = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb     = new StringBuilder();
            //The digest() method returns a raw byte[], which looks like gibberish if you try to print it. To make it readable and database-friendly, you convert each byte into a Hexadecimal string (base-16).
            //
            //%02x means: "Format this byte as 2 lowercase hex characters."
            //
            //Since there are 32 bytes and each byte becomes 2 characters, your final string is always 64 characters long.
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    //^	Start here. The string must match from the very beginning.
    //[\\w.+\\-]+	The Username. Allows letters, numbers, underscores (\w), dots (.), plus signs (+), and hyphens (-). The + means "at least one character."
    //@	The Divider. Requires exactly one "@" symbol.
    //[a-zA-Z0-9.\\-]+	The Domain Name. Allows letters, numbers, dots, and hyphens (e.g., "gmail" or "u.northwestern").
    //\\.	The Dot. A literal period before the extension. (The double backslash is needed because . usually means "any character" in Regex).
    //[a-zA-Z]{2,}	The Extension. Requires at least 2 letters (e.g., .com, .org, .io).
    //$	The End. The string must end exactly here—no extra spaces or characters allowed after.
    //** Simple email format check. */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    }

    //** Returns true if the string is non-null and non-blank. */
    public static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
