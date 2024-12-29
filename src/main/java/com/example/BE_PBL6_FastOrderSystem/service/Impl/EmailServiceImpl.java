package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.EmailReponse;
import com.example.BE_PBL6_FastOrderSystem.service.IEmailService;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

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
            helper.setFrom("vunguyen.17082003@gmail.com");
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
    @Override
    public ResponseEntity<APIRespone> receiveEmail(String user, String password, String senderEmail) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        Session emailSession = Session.getInstance(properties);
        Store store = null;
        Folder emailFolder = null;

        try {
            store = emailSession.getStore("imaps");
            store.connect(user, password);

            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            System.out.println("Total messages: " + messages.length);

            // Lấy thời gian hiện tại
            long currentTimeMillis = System.currentTimeMillis();
            List<EmailReponse> emailReponses = new ArrayList<>();

            for (Message message : messages) {
                String fromEmail = message.getFrom()[0].toString();
                Date receivedDate = message.getReceivedDate();

                // Kiểm tra email từ người gửi và trong vòng 1 giờ
                if (receivedDate != null
                        && (currentTimeMillis - receivedDate.getTime()) <= 3600000
                        && fromEmail.contains(senderEmail)) {

                    emailReponses.add(new EmailReponse(
                            message.getMessageNumber(),
                            message.getSubject(),
                            message.getFrom()[0].toString(),
                            message.getContent().toString()
                    ));

                    System.out.println("Email Number: " + message.getMessageNumber());
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    System.out.println("Text: " + message.getContent().toString());
                }
            }

            return ResponseEntity.ok(new APIRespone(true, "Emails from the past hour received successfully", emailReponses));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to receive email: " + e.getMessage());
        } finally {
            try {
                if (emailFolder != null && emailFolder.isOpen()) {
                    emailFolder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResponseEntity<APIRespone> receiveAllEmail(String user, String password) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        Session emailSession = Session.getInstance(properties);
        Store store = null;
        Folder emailFolder = null;

        try {

            store = emailSession.getStore("imaps");
            store.connect(user, password);


            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);


            Message[] messages = emailFolder.getMessages();
            System.out.println("Total messages: " + messages.length);

            List<EmailReponse> emailReponses = new ArrayList<>();
            for (Message message : messages) {
                emailReponses.add(new EmailReponse(
                        message.getMessageNumber(),
                        message.getSubject(),
                        message.getFrom()[0].toString(),
                        message.getContent().toString()
                ));
                System.out.println("Email Number: " + message.getMessageNumber());
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }
            return ResponseEntity.ok(new APIRespone(true, "Email received successfully", emailReponses));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to receive email: " + e.getMessage());
        } finally {
            try {
                if (emailFolder != null && emailFolder.isOpen()) {
                    emailFolder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
