package com.raksh.ToDo.event.listener;

import com.raksh.ToDo.event.RegisterationCompleteEvent;
import com.raksh.ToDo.model.User;
import com.raksh.ToDo.service.AuthService;
import com.raksh.ToDo.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegisterationCompleteEvent> {

    private final AuthService authService;
    private final EmailService emailService;

    public RegistrationCompleteEventListener(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(RegisterationCompleteEvent event) {

        User user=event.getUser();
        String token= UUID.randomUUID().toString();
        authService.saveVerificationToken(token,user);

        String email= user.getUserName();

        log.info("Sending email");
        emailService.sendVerificationEmail(email, token, user.getUserName());

        log.info("Click the link to verify your account:{}"+email);
    }
}
