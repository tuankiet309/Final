package com.lgcns.theseven.modules.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@example.com}")
    private String from;

    @Value("${app.base-url:http://localhost:8081/test/public}")
    private String baseUrl;

    @Override
    public void sendOtp(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("OTP Verification");
        message.setText("Your OTP code is: " + code);
        mailSender.send(message);
    }

    @Override
    public void sendLoginConfirmation(String to, String token) {
        String link = baseUrl + "/auth/login/confirm?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Login Confirmation");
        message.setText("Click the following link to complete your login: " + link);
        mailSender.send(message);
    }

    @Override
    public void sendRegistrationConfirmation(String to, String token) {
        String link = baseUrl + "/auth/confirm-email?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Please verify your email by clicking the following link: " + link);
        mailSender.send(message);
    }
}
