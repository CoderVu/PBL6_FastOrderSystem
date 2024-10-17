package com.example.BE_PBL6_FastOrderSystem.utils;

import java.util.Random;

public class OTPGenerator {
        public static String generateOTP(int length) {
            StringBuilder otp = new StringBuilder();
            for (int i = 0; i < length; i++) {
                int digit = new Random().nextInt(10); // random number between 0 and 9
                otp.append(digit);
            }
            return otp.toString();
        }
}
