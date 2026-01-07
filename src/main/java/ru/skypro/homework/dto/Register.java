package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.model.Role;
import javax.validation.constraints.*;

@Data
public class Register {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 32, message = "Username must be between 4 and 32 characters")
    private String username = "";

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    private String password = "";

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 16, message = "First name must be between 2 and 16 characters")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$", message = "First name can contain only letters, spaces and hyphens")
    private String firstName = "";

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 16, message = "Last name must be between 2 and 16 characters")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]+$", message = "Last name can contain only letters, spaces and hyphens")
    private String lastName = "";

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$",
            message = "Phone number must match pattern: +7 XXX XXX-XX-XX"
    )
    private String phone = "";

    @NotNull(message = "Role is required")
    private Role role = Role.USER;
}
