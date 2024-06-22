package com.emma.Ecommerce.api.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationBody {

    //This Model get users information from the frontend.
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 200, message = "Username must be between 3 and 200 characters")
    private String userName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    //Eight Characters, including one uppercase, one lowercase and one number or special character.
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",
    message = "Password must contain between 8 and 50 characters, including uppercase, " +
            "lowercase and one number or special character")
    private String password;

    @NotBlank(message = "Firstname cannot be blank")
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank")
    private String lastName;
}
