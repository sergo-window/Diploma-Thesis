package ru.skypro.homework.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateOrUpdateAd {

    @NotBlank(message = "Title is required")
    @Size(min = 4, max = 32, message = "Title must be between 4 and 32 characters")
    private String title = "";

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 10000000, message = "Price must be less than or equal to 10,000,000")
    private Integer price = 0;

    @NotBlank(message = "Description is required")
    @Size(min = 8, max = 64, message = "Description must be between 8 and 64 characters")
    private String description = "";
}
