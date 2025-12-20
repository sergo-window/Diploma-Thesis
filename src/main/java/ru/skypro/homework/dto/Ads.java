package ru.skypro.homework.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Ads {
    private Integer count = 0;
    private List<Ad> results = new ArrayList<>();
}
