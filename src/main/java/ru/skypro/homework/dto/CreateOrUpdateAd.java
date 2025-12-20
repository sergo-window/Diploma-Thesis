package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class CreateOrUpdateAd {
    private String title = "";
    private Integer price = 0;
    private String description = "";
}
