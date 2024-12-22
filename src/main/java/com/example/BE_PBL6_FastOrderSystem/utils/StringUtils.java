package com.example.BE_PBL6_FastOrderSystem.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
    public static String normalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        // Loại bỏ dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay thế khoảng trắng bằng dấu gạch dưới
        normalized = normalized.replaceAll("\\s+", "_");

        // Loại bỏ ký tự không mong muốn (nếu có)
        normalized = normalized.replaceAll("[^a-zA-Z0-9_]", "");

        // Chuyển tất cả sang chữ thường
        return normalized.toLowerCase();
    }

}
