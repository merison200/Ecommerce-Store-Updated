package com.emma.Ecommerce.service;

import com.emma.Ecommerce.api.model.LoginBody;
import com.emma.Ecommerce.api.model.PasswordResetBody;
import com.emma.Ecommerce.api.model.RegistrationBody;
import com.emma.Ecommerce.exception.EmailFailureException;
import com.emma.Ecommerce.exception.EmailNotFoundException;
import com.emma.Ecommerce.exception.UserAlreadyExistException;
import com.emma.Ecommerce.exception.UserNotVerifiedException;
import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.model.VerificationToken;
import com.emma.Ecommerce.repository.LocalUserRepository;
import com.emma.Ecommerce.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;



    /*
    This service method call the local user and accept the registration body to register a user.
    We create an exception class, and throws it here if username and email already existed.
    */

    public void registerUser(RegistrationBody registrationBody)
            throws UserAlreadyExistException, EmailFailureException {

        if (localUserRepository.findByEmailIgnoreCase
                (registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email is already taken");
        }
        if (localUserRepository.findByUsernameIgnoreCase
                (registrationBody.getUserName()).isPresent()) {
            throw new UserAlreadyExistException("Username is already taken");
        }

        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setUsername(registrationBody.getUserName());

        //TODO: Encrypt Password!!
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        //TODO: this code make sure the that an email that's not verified is rejected.
        //TODO: Verifies verification email.
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        localUserRepository.save(user);
    }

    /*
    This method create and return the verification token; email verification token
    */
    private VerificationToken createVerificationToken(LocalUser user) {

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationToken().add(verificationToken);
        return verificationToken;
    }

    /*
    This loginUser handles the user logging in our service layer.
    */
    @Transactional
    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> upUser = localUserRepository
                .findByUsernameIgnoreCase(loginBody.getUserName());

        if (upUser.isPresent()) {
            LocalUser user = upUser.get();

            if (encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {

                //The verification email is checked here.
                if (user.getEmailVerified()) {
                    return jwtService.generateJWT(user);
                } else {
                    List<VerificationToken> verificationTokens = user.getVerificationToken();

                    //I used verificationTokens.isEmpty instead of verificationTokens.size() == 0;
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimeStamp()
                                    .before(new Timestamp(System.currentTimeMillis() -
                                            (60 * 60 * 1000)));

                    if (resend) {
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    /* This method tries to find the token in the database, and if its in the database it should
    be valid. We take the token out and we get the user from the token. We checked if the user is
    verified, if its true, we set the email verified to true, if not it returns false.
    @Transactional is used because we are not only querying the data but also changing the state
    of the data */

    @Transactional
    public Boolean verifyUser(String token) {

       Optional<VerificationToken> upToken = verificationTokenRepository.findByToken(token);
       if (upToken.isPresent()) {
           VerificationToken verificationToken = upToken.get();
           LocalUser user = verificationToken.getUser();

           if (!user.getEmailVerified()) {
               user.setEmailVerified(true);
               localUserRepository.save(user);
               verificationTokenRepository.deleteByUser(user);
               return true;
           }
       }
       return false;
    }


    public void forgetPassword(String email) throws EmailNotFoundException, EmailFailureException {

        Optional<LocalUser> upUser = localUserRepository.findByEmailIgnoreCase(email);

        if (upUser.isPresent()) {
            LocalUser user = upUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);
        } else {
            throw new EmailNotFoundException();
        }
    }

    public boolean resetPassword(PasswordResetBody body) {

        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<LocalUser> upUser = localUserRepository.findByEmailIgnoreCase(email);

        if (upUser.isPresent()) {
            LocalUser user = upUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserRepository.save(user);
            return true;
        }
        return false;
    }

    public void changePassword(String email, String oldPassword, String newPassword) {

        LocalUser user = localUserRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!encryptionService.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(encryptionService.encryptPassword(newPassword));
        localUserRepository.save(user);
    }

    /*
    This method makes sure that is only user that is login can change the password.
    If not, a user can use a valid jwt token to change any user password.
    */
    public String getCurrentUserEmail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}