package com.emma.Ecommerce.service;

import com.emma.Ecommerce.exception.EmailFailureException;
import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    @Autowired
    private JavaMailSender javaMailSender;

    private SimpleMailMessage saveMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    /*
    This method handles the sending of the verification email to our smtp fake email receiver.
    */
    public void sendVerificationEmail(VerificationToken verificationToken) throws
            EmailFailureException {

        SimpleMailMessage message =  saveMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify your email to activate your account");

        message.setText("Please follow the link bellow to verify your email and " +
                "activate your account. \n" + url + "/authentication/verify?token="
                + verificationToken.getToken());

        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }

    /*
    This method sends email when we want to reset our password. The email is sent to our smtp
    fake email receiver.
    */
    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException {

        SimpleMailMessage message = saveMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your Password Request Link");
        message.setText("You requested password reset on our website, Please find the link below " +
                "to be able to reset your password. \n" + url + "/authentication/reset?token=" + token);

        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }
}
