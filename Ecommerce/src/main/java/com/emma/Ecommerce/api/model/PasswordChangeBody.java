package com.emma.Ecommerce.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordChangeBody {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password Cannot be null")
    @NotBlank(message = "Cannot Cannot be blank")
    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",
            message = "Password must contain between 8 and 50 characters, including uppercase, " +
                    "lowercase and one number or special character")
    private String oldPassword;

    @NotNull(message = "Password Cannot be null")
    @NotBlank(message = "Cannot Cannot be blank")
    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",
            message = "Password must contain between 8 and 50 characters, including uppercase, " +
                    "lowercase and one number or special character")
    private String newPassword;
}
