package com.example.genaiidemo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {



    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationCode(String toEmail, String confirmationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cheralathannandha9098@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Your Confirmation Code");
        message.setText("Your confirmation code is: " + confirmationCode);
        mailSender.send(message);
    }
}
