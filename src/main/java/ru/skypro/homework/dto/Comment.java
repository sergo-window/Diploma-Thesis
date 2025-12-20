package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class Comment {
    private Integer author = 0;
    private String authorImage = "";
    private String authorFirstName = "";
    private Long createdAt = 0L;
    private Integer pk = 0;
    private String text = "";
}
