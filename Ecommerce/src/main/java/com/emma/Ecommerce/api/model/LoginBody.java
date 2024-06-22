package com.emma.Ecommerce.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginBody {

    @NotNull(message = "UserName cannot be null")
    @NotBlank(message = "Username cannot be blank")
    private String userName;

    @NotNull(message = "Password Cannot be null")
    @NotBlank(message = "Cannot Cannot be blank")
    @Pattern(regexp = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",
            message = "Password must contain between 8 and 50 characters, including uppercase, " +
                    "lowercase and one number or special character")
    private String password;
}
