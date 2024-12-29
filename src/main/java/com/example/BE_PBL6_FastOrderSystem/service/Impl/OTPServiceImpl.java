package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.CodeShipper;
import com.example.BE_PBL6_FastOrderSystem.entity.OTP;
import com.example.BE_PBL6_FastOrderSystem.repository.CodeShipperRepository;
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
    private final CodeShipperRepository codeShipperRepository;

    public OTPServiceImpl(OTPRepository otpRepository, CodeShipperRepository codeShipperRepository) {
        this.otpRepository = otpRepository;
        this.codeShipperRepository = codeShipperRepository;
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
    @Override
    public String generateCodeShipper() {
        String code = OTPGenerator.generateOTP(6);
        CodeShipper codeShipper = new CodeShipper();
        codeShipper.setCode(code);
        codeShipper.setStatus(true);
        codeShipperRepository.save(codeShipper);
        return code;
    }
    @Override
    public boolean verifyCodeShipper(String code) {
        Optional<CodeShipper> codeShipperOptional = codeShipperRepository.findByCode(code);
        if (codeShipperOptional.isPresent()) {
            CodeShipper codeShipper = codeShipperOptional.get();
            if (codeShipper.getStatus()) {
                return true;
            }
        }
        return false;
    }
   @Override
   public void useCodeShipper(String code) {
        Optional<CodeShipper> codeShipperOptional = codeShipperRepository.findByCode(code);
        if (codeShipperOptional.isPresent()) {
            CodeShipper codeShipper = codeShipperOptional.get();
            codeShipper.setStatus(false);
            codeShipperRepository.save(codeShipper);
        }
    }
}
