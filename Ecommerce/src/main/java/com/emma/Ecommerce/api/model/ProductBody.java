package com.emma.Ecommerce.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductBody {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String shortDescription;

    @NotNull
    @NotBlank
    private String longDescription;

    @NotNull
    @NotBlank
    private Double price;
}
