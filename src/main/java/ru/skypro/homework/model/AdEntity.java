package ru.skypro.homework.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "ads")
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Size(min = 4, max = 32)
    private String title;

    @Column(nullable = false)
    @Min(0)
    @Max(10000000)
    private Integer price;

    @Column(nullable = false)
    @Size(min = 8, max = 64)
    private String description;

    @Column(name = "image_url")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude
    private UserEntity author;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CommentEntity> comments;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
