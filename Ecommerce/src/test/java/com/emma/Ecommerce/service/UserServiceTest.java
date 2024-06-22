package com.emma.Ecommerce.service;

import com.emma.Ecommerce.api.model.LoginBody;
import com.emma.Ecommerce.api.model.RegistrationBody;
import com.emma.Ecommerce.exception.EmailFailureException;
import com.emma.Ecommerce.exception.UserAlreadyExistException;
import com.emma.Ecommerce.exception.UserNotVerifiedException;
import com.emma.Ecommerce.model.VerificationToken;
import com.emma.Ecommerce.repository.VerificationTokenRepository;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension =
            new GreenMailExtension(ServerSetupTest.SMTP)
                    .withConfiguration(GreenMailConfiguration.aConfig()
                            .withUser("springboot", "secret"))
                    .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {

        RegistrationBody body = new RegistrationBody();
        body.setUserName("userA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("firstName");
        body.setLastName("lastName");
        body.setPassword("mySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistException.class,
                () -> userService.registerUser(body),
                "Username has already been used");

        body.setUserName("UserServiceTest$testRegisterUser");
        body.setEmail("userA@junit.com");
        Assertions.assertThrows(UserAlreadyExistException.class,
                () -> userService.registerUser(body),
                "Email has already been used");

        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
                "User Should register successfully");

        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                        .getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testVerifyUser() throws EmailFailureException {

        Assertions.assertFalse(userService.verifyUser("Add Token"), "Token that is " +
                "bad or does not exist should return false");
        LoginBody body = new LoginBody();
        body.setUserName("userB");
        body.setPassword("passwordB123");

        try {
            userService.loginUser(body);
            Assertions.fail("User should not have email verified");
        } catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokens = verificationTokenRepository.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            assertTrue(userService.verifyUser(token), "Token should be valid");
            Assertions.assertNotNull(body); //The user should now be verified.
        }
    }
}