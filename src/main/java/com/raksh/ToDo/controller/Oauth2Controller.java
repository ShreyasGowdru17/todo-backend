package com.raksh.ToDo.controller;

import com.raksh.ToDo.model.User;
import com.raksh.ToDo.repository.UserRepository;
import com.raksh.ToDo.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class Oauth2Controller implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtService jwtService;



    public Oauth2Controller(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = findOrCreateUser(email, oAuth2User);
        String jwt = jwtService.generateToken(user);

        response.sendRedirect("http://127.0.0.1:5501/dashboard.html?token=" + jwt);

    }

    private User findOrCreateUser(String email, OAuth2User oAuth2User) {

        User user=userRepository.findByUserName(email);

        if(user==null){
            user=new User();
            user.setUserName(email);
            user.setPassword("");
            user.setEnabled(true);

            userRepository.save(user);
        }
        return user;
    }
}
