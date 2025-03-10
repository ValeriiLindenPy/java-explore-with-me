package ru.practicum.ewm.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.service.CommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService service;

    @PatchMapping("/{commentId}/moderate")
    @ResponseStatus(HttpStatus.OK)
    public void moderateComment(@PathVariable Long commentId) {
        service.moderateComment(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        service.deleteComment(commentId);
    }
}