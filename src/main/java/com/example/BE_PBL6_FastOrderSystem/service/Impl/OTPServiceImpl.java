package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.OTP;
import com.example.BE_PBL6_FastOrderSystem.repository.OTPRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IOTPService;
import com.example.BE_PBL6_FastOrderSystem.utils.OTPGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class OTPServiceImpl implements IOTPService {
    private final OTPRepository otpRepository;

    public OTPServiceImpl(OTPRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public String generateOTP(String email, Long userId) {
        String otp = OTPGenerator.generateOTP(6);
        Optional<OTP> otpOptional = otpRepository.findByEmail(email);
        if (otpOptional.isPresent()) {
            OTP o = otpOptional.get();
            o.setOtp(otp);
            o.setUserId(userId);
            otpRepository.save(o);
        } else {
            OTP o = new OTP();
            o.setEmail(email);
            o.setOtp(otp);
            o.setUserId(userId);
            otpRepository.save(o);
        }
        return otp;
    }

    @Override
    public boolean verifyOTP(String email, String otp) {
        Optional<OTP> otpOptional = otpRepository.findByEmail(email);
        if (otpOptional.isPresent()) {
            OTP otpEntity = otpOptional.get();
            long currentTime = System.currentTimeMillis();
            Instant otpCreatedInstant = otpEntity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
            long otpCreatedTime = otpCreatedInstant.toEpochMilli();
            long timeElapsed = currentTime - otpCreatedTime;
            if (timeElapsed <= 120000) { // 2 phút
                if (otpEntity.getOtp().equals(otp)) {
                    return true;
                }
            }
        }
        return false;
    }

}
