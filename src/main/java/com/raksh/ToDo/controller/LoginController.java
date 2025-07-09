package com.raksh.ToDo.controller;

import com.raksh.ToDo.event.RegisterationCompleteEvent;
import com.raksh.ToDo.model.PasswordResetModel;
import com.raksh.ToDo.repository.UserRepository;
import com.raksh.ToDo.service.AuthService;
import com.raksh.ToDo.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.raksh.ToDo.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "http://127.0.0.1:5501")
@RestController
@RequestMapping("/auth")
public class LoginController {

    private final AuthService authService;

    private final ApplicationEventPublisher publisher;

    private final UserRepository userRepository;

    private final EmailService emailService;

    public LoginController(AuthService authService, ApplicationEventPublisher publisher, UserRepository userRepository, EmailService emailService) {
        this.authService = authService;
        this.publisher = publisher;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<String>  register(@Valid @RequestBody User user, final HttpServletRequest request){
        User user1=authService.register(user);
        publisher.publishEvent(new RegisterationCompleteEvent(user,applicationUrl(request)));
        return ResponseEntity.ok("Registration successful. Please check your email for verification.");
    }



    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody User user){
        String jwt=authService.login(user);
        return ResponseEntity.ok(Map.of("token",jwt));

    }
    @GetMapping("/verifyRegistration")
    public void verifyRegistration(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        String result = authService.validateToken(token);
        if (result.equalsIgnoreCase("valid")) {
            authService.enableUser(token);
            response.sendRedirect("https://todo-frontend-shreyas.vercel.app/verify-success.html?status=success");
        } else if (result.equalsIgnoreCase("expired")) {
            response.sendRedirect("https://todo-frontend-shreyas.vercel.app/verify-error.html?status=expired");
        } else {
            response.sendRedirect("https://todo-frontend-shreyas.vercel.app/verify-error.html?status=invalid");
        }
    }



    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordResetModel passwordResetModel,final HttpServletRequest request){
        User user=userRepository.findByUserName(passwordResetModel.getEmail());
        System.out.println("Processing password reset for: " +passwordResetModel.getEmail());

        String url="";
        if(user!=null){
            String token= UUID.randomUUID().toString();
            authService.createPasswordToken(token,user);
            emailService.sendPasswordResetEmail(user.getUserName(),token, user.getUserName());
            url= "https://todo-frontend-shreyas.vercel.app/password-reset.html?token=" + token;
        }
        return url;
    }

    @PostMapping("/savePassword")
    public void savePassword(@RequestParam("token") String token, @RequestBody PasswordResetModel passwordResetModel, final HttpServletResponse response) throws IOException {
        if(!authService.savePassword(token, passwordResetModel.getPassword())){
            response.sendRedirect("https://todo-frontend-shreyas.vercel.app/verify-error.html");
        }
        else{
            response.sendRedirect("https://todo-frontend-shreyas.vercel.app/index.html?passwordReset=true");
        }
    }

    private String applicationUrl(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getRequestURI(), "");
    }
}
