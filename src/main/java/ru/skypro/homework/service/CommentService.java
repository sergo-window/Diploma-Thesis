package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;

    public Optional<Comments> getCommentsByAdId(Integer adId) {
        Optional<AdEntity> adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Optional.empty();
        }

        List<CommentEntity> comments = commentRepository.findAllByAdWithAuthor(adOpt.get());
        Comments commentsDto = commentMapper.toCommentsDto(comments);
        return Optional.of(commentsDto);
    }

    @Transactional
    public Optional<Comment> addComment(Integer adId, CreateOrUpdateComment createComment, UserEntity author) {
        Optional<AdEntity> adOpt = adRepository.findById(adId);
        if (adOpt.isEmpty()) {
            return Optional.empty();
        }

        CommentEntity commentEntity = commentMapper.toEntity(createComment);
        commentEntity.setAd(adOpt.get());
        commentEntity.setAuthor(author);

        CommentEntity saved = commentRepository.save(commentEntity);
        return Optional.of(commentMapper.toDto(saved));
    }

    @Transactional
    public boolean deleteComment(Integer adId, Integer commentId, UserEntity user) {
        Optional<CommentEntity> commentOpt = commentRepository.findByIdWithRelations(commentId);

        if (commentOpt.isEmpty()) {
            return false;
        }

        CommentEntity comment = commentOpt.get();

        if (!comment.getAd().getId().equals(adId)) {
            return false;
        }

        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals(ru.skypro.homework.model.Role.ADMIN);

        if (!isAuthor && !isAdmin) {
            return false;
        }

        commentRepository.delete(comment);
        return true;
    }

    @Transactional
    public Optional<Comment> updateComment(Integer adId, Integer commentId,
                                           CreateOrUpdateComment updateComment, UserEntity user) {
        Optional<CommentEntity> commentOpt = commentRepository.findByIdWithRelations(commentId);

        if (commentOpt.isEmpty()) {
            return Optional.empty();
        }

        CommentEntity comment = commentOpt.get();

        if (!comment.getAd().getId().equals(adId)) {
            return Optional.empty();
        }

        if (!comment.getAuthor().getId().equals(user.getId())) {
            return Optional.empty();
        }

        commentMapper.updateEntityFromDto(updateComment, comment);
        CommentEntity saved = commentRepository.save(comment);
        return Optional.of(commentMapper.toDto(saved));
    }

    @Transactional
    public void deleteAllCommentsByAd(AdEntity ad) {
        commentRepository.deleteAllByAd(ad);
    }

    public boolean isCommentAuthor(Integer commentId, UserEntity user) {
        return commentRepository.existsByIdAndAuthor(commentId, user);
    }
}
