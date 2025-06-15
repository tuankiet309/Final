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

    @Override
    public void sendOtp(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("OTP Verification");
        message.setText("Your OTP code is: " + code);
        mailSender.send(message);
    }
}
