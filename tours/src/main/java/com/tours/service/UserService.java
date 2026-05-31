package com.tours.service;


import com.tours.entities.Users;
import com.tours.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService
{

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder;

    public void register(Users user)
    {
        logger.info("Attempting to register user with email: " + user.getEmail());

        if(user.getRole() == null || user.getRole().isEmpty())
        {
            user.setRole("ROLE_CUSTOMER");
        }


        if(userRepo.existsByEmail(user.getEmail()))
        {
            logger.warning("Email already in use: " + user.getEmail());
            throw new RuntimeException("Email is already in use. Please use a different email.");

        }


        user.setPassword(encoder.encode(user.getPassword()));

        userRepo.save(user);

        logger.info("User successfully registered with email: " + user.getEmail());


    }

    public Users login(String email, String password)
    {

        Users user = userRepo.getUserByEmail(email);

        if(user == null)
        {
            System.out.println("Invalid email entered");
        }

        if(!encoder.matches(password,user.getPassword()))
        {
            System.out.println("Invalid Password Entered");
        }


        System.out.println("Login successful");

        return user;


    }

}
