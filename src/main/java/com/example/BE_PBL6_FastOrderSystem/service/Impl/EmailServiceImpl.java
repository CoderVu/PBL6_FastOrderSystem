package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Data
@Service
public class EmailServiceImpl implements IEmailService {

    private JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @Override
    public void sendEmail(String to, String subject, String content) {
        if (to == null || to.isEmpty() || subject == null || subject.isEmpty() || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Email parameters cannot be null or empty.");
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            content = content.replace("\n", "<br>");
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }



}
