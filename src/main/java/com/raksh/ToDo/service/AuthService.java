package com.raksh.ToDo.service;

import com.raksh.ToDo.model.User;
import com.raksh.ToDo.model.VerificationToken;
import com.raksh.ToDo.repository.UserRepository;
import com.raksh.ToDo.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, VerificationTokenRepository verificationTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public User register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser=userRepository.save(user);
        return savedUser;
    }

    public String login(User user){
        User userDetails=userRepository.findByUserName(user.getUserName());
        if(userDetails==null){
            throw new RuntimeException("User not found");
        }
        if(!userDetails.isEnabled()){
            throw new RuntimeException("Please verify your email before logging in");
        }
        if(!passwordEncoder.matches(user.getPassword(), userDetails.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }
        return jwtService.generateToken(userDetails);
    }

    public void saveVerificationToken(String token, User user) {
        VerificationToken verificationToken=new VerificationToken(token,user);
        verificationTokenRepository.save(verificationToken);
    }


    public String validateToken(String token) {
        VerificationToken verificationToken=verificationTokenRepository.findByToken(token);
        if(verificationToken==null){
            return "invalid";
        }

        User user=verificationToken.getUser();
        Calendar calendar=Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime()-calendar.getTime().getTime()<=0){
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }

        return "valid";
    }

    @Transactional
    public void createPasswordToken(String token, User user) {
            verificationTokenRepository.deleteByUser(user);
            VerificationToken verificationToken=new VerificationToken(token,user);
            verificationTokenRepository.save(verificationToken);

    }

    public boolean savePassword(String token,String password) {
        VerificationToken verificationToken=verificationTokenRepository.findByToken(token);
        if(verificationToken==null){
            return false;
        }

        User user=verificationToken.getUser();
        Calendar calendar=Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime()-calendar.getTime().getTime()<=0){
            verificationTokenRepository.delete(verificationToken);
            return false;
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }
    public void enableUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken!= null && "valid".equals(validateToken(token))) {
            User user = verificationToken.getUser();
            user.setEnabled(true);
            userRepository.save(user);
            verificationTokenRepository.delete(verificationToken);
        }
    }

}
