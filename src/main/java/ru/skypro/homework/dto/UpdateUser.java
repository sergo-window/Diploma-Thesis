package ru.skypro.homework.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UpdateUser {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 10, message = "First name must be between 3 and 10 characters")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$", message = "First name can contain only letters, spaces and hyphens")
    private String firstName = "";

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 10, message = "Last name must be between 3 and 10 characters")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$", message = "Last name can contain only letters, spaces and hyphens")
    private String lastName = "";

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$",
            message = "Phone number must match pattern: +7 XXX XXX-XX-XX"
    )
    private String phone = "";
}
