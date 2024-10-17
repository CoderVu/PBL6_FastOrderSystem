package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.service.Impl.EmailServiceImpl;

public interface IEmailService {

    void sendEmail(String to, String subject, String content);
}
