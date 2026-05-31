package com.tours.config;

import com.tours.service.CustomUserDetailsService;
import com.tours.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class JwtFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

//    @Autowired
//    private ApplicationContext context;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    public static final Set<String> blackListedTokens = Collections.synchronizedSet(new HashSet<>());

    public static void addBlackListedTokens(String token)
    {
        blackListedTokens.add(token);

        logger.info("Token is blacklisted: {} ",token);
    }

    private static boolean isTokenBlackListed(String token){ return blackListedTokens.contains(token);}



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
             String authHeader = request.getHeader("Authorization");
             String token = null;
             String userName = null;

             logger.info("Processing Request to URI: {}",request.getRequestURI());

             if(authHeader == null || !authHeader.startsWith("Bearer "))
             {
                 filterChain.doFilter(request,response);
                 return;
             }

             if(authHeader != null && authHeader.startsWith("Bearer "))
             {
                 token = authHeader.substring(7);

                 logger.info("Token is extracted from authHeader: {}", token);
             }


             if(isTokenBlackListed(token))
             {
                 logger.info("Token is already blacklisted: {}",token);

                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                 response.getWriter().write("Token has been invalidated. Please log in again.");

                 return; //Stop processing further

             }

             try
             {
             userName = jwtService.extractUserName(token);

              logger.info("UserName '{}' is extracted from the token",userName);
             }
             catch (Exception e)
             {
                 logger.info("Error extracting the userName: {}", e.getMessage());
             }

             if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null)
             {
                 logger.info("User '{}' is not authenticated while proceeding for token validation", userName);

                 try{
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

                    if(jwtService.validateToken(token,userDetails))
                    {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));  // Attach request details
                        SecurityContextHolder.getContext().setAuthentication(authToken);  // Set authentication context
                        logger.info("User '{}' successfully authenticated.", userName);
                    }
                    else{logger.warn("Token validation failed for user {}.",userName);}

                 }
                 catch (Exception e) {
                     logger.error("Error during token validation or user authentication: {}", e.getMessage());
                 }


             }

        logger.info("Continuing filter chain for request to URI: {}", request.getRequestURI());
        filterChain.doFilter(request, response);  // Continue the filter chain




    }
}
