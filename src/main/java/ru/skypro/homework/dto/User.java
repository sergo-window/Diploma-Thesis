package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.model.Role;

@Data
public class User {
    private Integer id = 0;
    private String email = "";
    private String firstName = "";
    private String lastName = "";
    private String phone = "";
    private Role role = Role.USER;
    private String image = "";
}
