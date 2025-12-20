package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    @GetMapping
    public ResponseEntity<Comments> getComments(@PathVariable("adId") Integer adId) {
        Comments comments = new Comments(); // Пустой список комментариев
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable("adId") Integer adId,
            @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = new Comment();
        comment.setAuthor(1); // ID автора по умолчанию
        comment.setText(createOrUpdateComment.getText());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId) {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId,
            @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        Comment comment = new Comment();
        comment.setPk(commentId);
        comment.setText(createOrUpdateComment.getText());
        return ResponseEntity.ok(comment);
    }
}
