package com.tours.service;

import com.tours.repo.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService
{
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private UserRepo userRepo;

    private String secretKey;

    public  JwtService(){secretKey =  "myVeryLongSecureSecretKeyForJwtAuthentication123456789";}

        public String generateSecretKey()
        {
            try
            {
                logger.info("Generating the Secret Key");
                KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256"); //Generating the using HMAC-SHA256 algorithm

                 SecretKey secretKey = keyGen.generateKey();
                 logger.info("Secret Key Generated Successfully");

                 return Base64.getEncoder().encodeToString(secretKey.getEncoded()); //Convert key to base64 for storage


            }
            catch (NoSuchAlgorithmException e)
            {
                logger.warn("Error Generating Secret Key: {}" , e.getMessage(),e);
                    throw new RuntimeException("Error generating Secret key",e);
            }


        }

        public String generateToken(String userName) {
            logger.info("Generating the token for user-name: {}", userName);

            String role = userRepo.findRoleByUsername(userName);//Fetching the role from the record matching the userName

            logger.info("Role for user {}: {}", role, userName);

            HashMap<String, Object> claims = new HashMap<>();

            claims.put("role", role);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userName) //Setting the userName as subject of the token
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                    .signWith(getKey(), SignatureAlgorithm.HS256)
                    .compact();

            logger.info("Token generated successfully for user-name: {}", userName);
            return token;
        }

        private Key getKey()
        {
            logger.info("Retrieving Signing Key");
//           byte[] keyBytes = Decoders.BASE64.decode(secretKey);
           return Keys.hmacShaKeyFor(secretKey.getBytes());
        }

        public String extractUserName(String token)
        {
            logger.info("Extracting the user-name from the token");

            return extractClaim(token, Claims:: getSubject);
        }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token); // Extract all claims
        return claimResolver.apply(claims); // Resolve specific claim
    }

    private Claims extractAllClaims(String token) {
        logger.info("Extracting all claims from token.");
        return Jwts.parser()
                .setSigningKey(getKey()) // Use signing key to parse the token
                .build()
                .parseClaimsJws(token)
                .getBody(); // Retrieve the claims
    }

    public String extractUserRole(String token) {
        logger.info("Extracting user role from token.");
        return extractClaim(token, claims -> claims.get("role", String.class)); // Retrieve the "role" claim from the token
    }


    public Boolean validateToken(String token , UserDetails userDetails)
    {
        logger.info("Valid the token username with {}",userDetails.getUsername());

                     String userName = extractUserName(token);

                  boolean isValid  = userName.equals(userDetails.getUsername()) && !isTokenExpired(token);

        if (isValid) {
            logger.info("Token is valid for user: {}", userDetails.getUsername());
        } else {
            logger.warn("Token validation failed for user: {}", userDetails.getUsername());
        }
        return isValid;
    }




    private boolean isTokenExpired(String token) {
        logger.info("Checking if token is expired.");
        return extractExpiration(token).before(new Date()); // Compare token expiration with current time
    }

    private Date extractExpiration(String token) {
        logger.info("Extracting expiration date from token.");
        return extractClaim(token, Claims::getExpiration); // Retrieve expiration date from claims
    }




}
