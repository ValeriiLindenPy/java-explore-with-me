package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.model.dto.comment.CommentDto;
import ru.practicum.ewm.main.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.model.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    List<CommentDto> getEventComments(Long eventId, Integer from, Integer size);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, UpdateCommentDto comment);

    void moderateComment(Long commentId);

    void deleteComment(Long commentId);

    void deleteComment(Long userId, Long eventId, Long commentId);
}
