package com.raksh.ToDo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {


    private final JavaMailSender javaMailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String to, String token, String userName) {
        sendEmail(to, "Verify Your ToDo Account", buildVerificationEmailTemplate(userName, token));
    }

    public void sendPasswordResetEmail(String to, String token, String userName) {
        sendEmail(to, "Reset Your ToDo Password", buildPasswordResetEmailTemplate(userName, token));
    }

    @Async
    public  void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String buildVerificationEmailTemplate(String userName, String token) {
        String link = frontendUrl + "/verify-success.html?token=" + token;
        return """
            <html>
            <body>
                <h2>Hello %s,</h2>
                <p>Please verify your email to activate your ToDo account.</p>
                <a href="%s">Verify Email</a>
                <p>If this link doesn't work, copy and paste it into your browser:</p>
                <p>%s</p>
                <p>This link expires in 10 minutes.</p>
            </body>
            </html>
        """.formatted(userName, link, link);
    }

    private String buildPasswordResetEmailTemplate(String userName, String token) {
        String link = frontendUrl + "/password-reset.html?token=" + token;
        return """
            <html>
            <body>
                <h2>Hello %s,</h2>
                <p>You requested to reset your password. Click the link below:</p>
                <a href="%s">Reset Password</a>
                <p>If this link doesn't work, copy and paste it into your browser:</p>
                <p>%s</p>
                <p>This link expires in 10 minutes.</p>
            </body>
            </html>
        """.formatted(userName, link, link);
    }

}

