package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class ExtendedAd {
    private Integer pk = 0;
    private String authorFirstName = "";
    private String authorLastName = "";
    private String description = "";
    private String email = "";
    private String image = "";
    private String phone = "";
    private Integer price = 0;
    private String title = "";
}
