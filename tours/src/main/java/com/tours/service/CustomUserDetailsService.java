package com.tours.service;

import com.tours.config.UserPrincipal;
import com.tours.entities.Users;
import com.tours.repo.UserRepo;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepo repo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Users user = repo.getUserByEmail(email);

        if(user == null)
        {
            logger.warn("User with the provided email not found:{}",email);
            throw  new UsernameNotFoundException("User with " + email + " not found");

        }


        logger.info("User found with email: {}, Role: {}", email, user.getRole());
        return new UserPrincipal(user);


    }
}
