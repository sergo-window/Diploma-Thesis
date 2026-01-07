package ru.skypro.homework.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class NewPassword {

    @NotBlank(message = "Current password is required")
    @Size(min = 8, max = 16, message = "Current password must be between 8 and 16 characters")
    private String currentPassword = "";

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 16, message = "New password must be between 8 and 16 characters")
    private String newPassword = "";
}