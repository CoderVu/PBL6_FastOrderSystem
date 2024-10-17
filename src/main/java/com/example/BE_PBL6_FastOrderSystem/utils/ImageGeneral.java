package com.example.BE_PBL6_FastOrderSystem.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

public class ImageGeneral {
    public static String fileToBase64(InputStream inputStream) {
        if (inputStream == null)
            return "";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static String urlToBase64(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        Path tempFile = Files.createTempFile(null, null);
        Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
        byte[] fileContent = Files.readAllBytes(tempFile);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        return encodedString;
    }

    public static byte[] decodeBase64ToImage(String base64String) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return decodedBytes;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}