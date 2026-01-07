package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserService userService;

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
    public Optional<Comment> addComment(Integer adId, CreateOrUpdateComment createComment, String username) {
        Optional<AdEntity> adOpt = adRepository.findById(adId);
        UserEntity author = userService.getUserEntityByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#commentId, authentication.name)")
    public boolean deleteComment(Integer adId, Integer commentId, String username) {
        Optional<CommentEntity> commentOpt = commentRepository.findByIdWithRelations(commentId);

        if (commentOpt.isEmpty()) {
            return false;
        }

        CommentEntity comment = commentOpt.get();

        if (!comment.getAd().getId().equals(adId)) {
            return false;
        }

        commentRepository.delete(comment);
        return true;
    }

    public boolean isCommentAuthor(Integer commentId, String username) {
        UserEntity user = userService.getUserEntityByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return commentRepository.existsByIdAndAuthor(commentId, user);
    }

    @Transactional
    @PreAuthorize("@commentService.isCommentAuthor(#commentId, authentication.name)")
    public Optional<Comment> updateComment(Integer adId, Integer commentId,
                                           CreateOrUpdateComment updateComment, String username) {
        Optional<CommentEntity> commentOpt = commentRepository.findByIdWithRelations(commentId);

        if (commentOpt.isEmpty()) {
            return Optional.empty();
        }

        CommentEntity comment = commentOpt.get();

        if (!comment.getAd().getId().equals(adId)) {
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
}
