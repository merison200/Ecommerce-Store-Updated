package com.emma.Ecommerce.api.controller.auth;

import com.emma.Ecommerce.api.model.LoginBody;
import com.emma.Ecommerce.api.model.PasswordChangeBody;
import com.emma.Ecommerce.api.model.PasswordResetBody;
import com.emma.Ecommerce.api.model.RegistrationBody;
import com.emma.Ecommerce.exception.EmailFailureException;
import com.emma.Ecommerce.exception.EmailNotFoundException;
import com.emma.Ecommerce.exception.UserAlreadyExistException;
import com.emma.Ecommerce.exception.UserNotVerifiedException;
import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.response.LoginResponse;
import com.emma.Ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    /*
    This Api register a new user in our website. https://localhost/8080/register
    NB: The function of @Valid that is used in this method is that it makes sure that all the
    conditions set in our RegistrationBody is met before a user can be allowed to register in
    our website, some of the conditions include @NotNull, @Email and others.
    How to register: input userName, password, email, firstName and lastName.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationBody registrationBody) {

        try {
            userService.registerUser(registrationBody);
            return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
        } catch (UserAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
    This method intercepts all the errors resulting from our model package which include the
    followings RegistrationBody, LoginBody, PasswordResetBody.
    It intercepts the errors resulting from the failure to meet the requirements and display
    in our model package and display it to our api tester like postman in a json format.
    */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {

            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /*
    This api allows the user to login after registration, the try and catch method in this api
    is used to check if the user is verified or not.
    If the user is verified, he will be allowed to log in, but if the user is not verified, a
    verification link is sent to the user to his email in order to get verified.
    How to login: input userName and password. A verification link will be sent to your email,
    copy the verification token, go to our api tester like postman, click the param subsection,
    under key input token and under the value input the token.
    https://localhost/8080/login and https://localhost/8080/verify.
    */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {

        String jwt = null;

        //Try and catch is used to catch if the user email is verified or not.
        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
           LoginResponse response = new LoginResponse();
           response.setSuccess(false);
           String reason = "USER_NOT_VERIFIED";

           if (ex.isNewEmailSent()) {
               reason += "_EMAIL_RESENT";
           }
           response.setFailureReason(reason);
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    /*
    This api verifies the user and allow him to log in after registration, It is only a verified
    user that has access to some of the endpoints like /orders and others because the user needs
    a jwt token to access those endpoints. The method of application is also treated above.
    */
    @PostMapping("/verify")
    public ResponseEntity<Object> verifyEmail(@RequestParam String token) {

        try {
            if (userService.verifyUser(token)) {
                return ResponseEntity.ok().body("User verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid Verification Token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }

    /*
    This api returns the information of a user when he is log in, a bearer token is needed to
    access this endpoint, hence the user must be login to access it.
    https://localhost/8080/auth/me
     */
    @GetMapping("/me")
    public LocalUser getLoginUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user;
    }

    /*
    In this Api, when you click forget password, it will send a verification token or link to
    your email. How to test this Api by using tool like postman is by opening the param section
    under postman, under key input email like "email". Under value input the actual email like
    "merison1@gmail.com. https://localhost/8080/auth/forget
    */
    @PostMapping("/forget")
    public ResponseEntity<Object> forgetPassword(@RequestParam String email) {
        try {
            userService.forgetPassword(email);
            return ResponseEntity.ok().body("Link sent to your email");
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
    In this Api, it resets the password to a new password. It is a continuation of the forget
    Api. How too test this api, in the body of the postman, input the new token sent to the
    email and input the new password, both in a json format. https://localhost/8080/reset
    */
    @PostMapping("/reset")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody PasswordResetBody body) {

        try {
            if (userService.resetPassword(body)) {
                return ResponseEntity.ok().body("Password reset successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid Verification token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }

    /*
    This Api allows the user to change his old password to a new password by inputing his email,
    oldPassword and newPassword in a json format. Before a user will use this api, he must be
    login, so authentication token is needed to access this api. https://localhost/8080/change
    NB: the old password must match the old password you registered before this api will work.
    */
    @PostMapping("/change")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeBody
                                                         passwordChangeBody) {
        /*
        String currentUserEmail = userService.getCurrentUserEmail();
        if (!currentUserEmail.equals(passwordChangeBody.getEmail())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not your account");
        }
        */

        try {
            userService.changePassword(passwordChangeBody.getEmail(),
                    passwordChangeBody.getOldPassword(), passwordChangeBody.getNewPassword());
            return ResponseEntity.ok("Password change successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}