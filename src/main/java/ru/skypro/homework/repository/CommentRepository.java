package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    List<CommentEntity> findAllByAd(AdEntity ad);

    @Query("SELECT c FROM CommentEntity c LEFT JOIN FETCH c.author WHERE c.ad = :ad ORDER BY c.createdAt DESC")
    List<CommentEntity> findAllByAdWithAuthor(@Param("ad") AdEntity ad);

    @Query("SELECT c FROM CommentEntity c LEFT JOIN FETCH c.author LEFT JOIN FETCH c.ad WHERE c.id = :id")
    Optional<CommentEntity> findByIdWithRelations(@Param("id") Integer id);

    boolean existsByIdAndAuthor(Integer id, UserEntity author);

    void deleteAllByAd(AdEntity ad);
}
