package com.tours.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tours.entities.Users;
import com.tours.repo.UserRepo;
import com.tours.service.CustomOAuth2UserService;
import com.tours.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    @Autowired
    private com.tours.service.JwtService jwtService;

    @Autowired
    private com.tours.repo.UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String contactNumber = oauth2User.getAttribute("contactNumber");

        logger.debug("Extracted user details: email={}, name={}, contactNumber={}", email, name, contactNumber);

        // Find or create user
        com.tours.entities.Users user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    logger.info("User not found in the database. Creating a new user with email: {}", email);

                    com.tours.entities.Users newUser = new com.tours.entities.Users();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setContactNo(contactNumber);
                    newUser.setRole("ROLE_CUSTOMER"); // Default role
                    newUser.setEnabled(true);

                    // Generate password: 01in + username (encoded)
                    String username = name.replaceAll("\\s+", "").toLowerCase();
                    String rawPassword = "01in" + username;
                    newUser.setPassword(passwordEncoder.encode(rawPassword));
                    logger.debug("Generated password for new user: {}", rawPassword);

                    com.tours.entities.Users savedUser = userRepository.save(newUser);
                    logger.info("New user created with ID: {}", savedUser.getId());
                    return savedUser;
                });

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());
        logger.info("JWT token generated for user: {}", email);

        // Build URL with token
        String redirectUrl = "http://localhost:5173/authSuccess";
        String finalRedirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", token)
                .build().toUriString();
        logger.debug("Redirect URL constructed: {}", finalRedirectUrl);

        // Redirect to appropriate dashboard
        logger.info("Redirecting user to: {}", finalRedirectUrl);
        getRedirectStrategy().sendRedirect(request, response, finalRedirectUrl);
    }
}
