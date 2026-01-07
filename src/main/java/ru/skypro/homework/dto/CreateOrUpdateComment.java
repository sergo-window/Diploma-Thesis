package ru.skypro.homework.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class CreateOrUpdateComment {

    @NotBlank(message = "Text is required")
    @Size(min = 8, max = 64, message = "Text must be between 8 and 64 characters")
    private String text = "";
}
