package ru.skypro.homework.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    @Size(min = 4, max = 32)
    private String username;

    @Column(nullable = false)
    @Size(min = 8, max = 16)
    private String password;

    @Column(name = "first_name", nullable = false)
    @Size(min = 2, max = 16)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(min = 2, max = 16)
    private String lastName;

    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "image_url")
    private String image;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AdEntity> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CommentEntity> comments;

    @Column(nullable = false)
    private boolean enabled = true;
}
