package ru.practicum.ewm.main.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.comment.CommentDto;
import ru.practicum.ewm.main.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.model.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.main.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService service;

    @GetMapping
    public List<CommentDto> getEventComments(@RequestParam Long eventId,
                                             @RequestParam(defaultValue = "0", required = false) Integer from,
                                             @RequestParam(defaultValue = "10", required = false) Integer size) {
        return service.getEventComments(eventId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @RequestParam Long eventId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {
        return service.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping
    public CommentDto updateComment(@PathVariable Long userId,
                                    @RequestParam Long eventId,
                                    @RequestBody @Valid UpdateCommentDto comment) {
        return service.updateComment(userId, eventId, comment);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @RequestParam Long eventId,
                              @PathVariable Long commentId) {
        service.deleteComment(userId, eventId, commentId);
    }
}
