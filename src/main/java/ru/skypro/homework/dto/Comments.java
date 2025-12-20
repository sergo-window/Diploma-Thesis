package ru.skypro.homework.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Comments {
    private Integer count = 0;
    private List<Comment> results = new ArrayList<>();
}
