package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<AdEntity, Integer> {

    List<AdEntity> findAllByAuthor(UserEntity author);

    @Query("SELECT a FROM AdEntity a LEFT JOIN FETCH a.author WHERE a.id = :id")
    Optional<AdEntity> findByIdWithAuthor(@Param("id") Integer id);

    @Query("SELECT a FROM AdEntity a LEFT JOIN FETCH a.author")
    List<AdEntity> findAllWithAuthor();

    boolean existsByIdAndAuthor(Integer id, UserEntity author);
}
