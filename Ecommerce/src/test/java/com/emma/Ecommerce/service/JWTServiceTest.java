package com.emma.Ecommerce.service;

import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.repository.LocalUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Test
    public void testVerificationTokenNotUsableForOurLogin() {

        LocalUser user = localUserRepository.findByUsernameIgnoreCase("userA").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should" +
                "not contain username");
    }

    @Test
    public void testAuthTokenReturnsUsername() {

        LocalUser user = localUserRepository.findByUsernameIgnoreCase("userA").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for" +
                "auth should contain users username");
    }
}
