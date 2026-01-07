package ru.skypro.homework.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String encodedPassword = encoder.encode(password);
        System.out.println("Original: " + password);
        System.out.println("Encoded: " + encodedPassword);
        System.out.println("Matches: " + encoder.matches(password, encodedPassword));
    }
}
