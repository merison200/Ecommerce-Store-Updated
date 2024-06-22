package com.emma.Ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emma.Ecommerce.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.algorithmkey}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiresInSeconds}")
    private int expiresInSeconds;
    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL";

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    /* Anytime we call this method, it will create an encrypted string that expires in certain
    time, with an issuer, with a username embedded into it, and it's going to sign in with an
    encryption algorithm. When we decrypt this we know exactly know when it expires, we know who
    issue it and who the user is.*/
    public String generateJWT(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiresInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /*
    This technology is for email verification, it returns the email jwt token. It generates and
    returns the email verification jwt token.
    */
    public String generateVerificationJWT(LocalUser user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiresInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /*
    This technology is for password reset email verification, it returns the password jwt token.
    It generates and returns the password reset jwt token.
    */
    public String generatePasswordResetJWT(LocalUser user) {
        return JWT.create()
                .withClaim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getResetPasswordEmail(String token) {

        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        return jwt.getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
    }

    /*
    This method decrypt the user information. It also verifies the exact token issued by our jwt
    so that someone cant figure out a username and verifies the login.
    */
    public String getUsername(String token) {

        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        //return JWT.decode(token).getClaim(USERNAME_KEY).asString();
        return jwt.getClaim(USERNAME_KEY).asString();
    }
}
