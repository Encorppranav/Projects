package com.tours.controller;

import com.tours.entities.Users;
import com.tours.service.JwtService;
import com.tours.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController
{
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody Users user)//To register the user
    {
        if(!user.isEnabled())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please agree to the terms and condition");
        }

        userService.register(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Users loginUser)
    {
          Authentication authentication =  authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(loginUser.getEmail(),
                            loginUser.getPassword()));

            if(authentication.isAuthenticated())
            {
                String token = jwtService.generateToken(loginUser.getEmail());

                return ResponseEntity.ok(token);//Return Token to the client
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed!");
            }
    }



    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminDashboard(Users user)
    {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String userName = getUsername(authentication);

       return   ResponseEntity.ok("Welcome to admin Dashboard " + userName);
    }

    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> customerDashboard(Users user)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = getUsername(authentication);

        return   ResponseEntity.ok("Welcome to customer Dashboard " + userName);
    }



    private String getUsername(Authentication authentication)
    {
        if(authentication == null && !authentication.isAuthenticated())
        {
            return "Unknown User";
        }

         Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails)
        {
            return ((UserDetails)principal).getUsername();
        }

        return principal.toString();

    }

}
