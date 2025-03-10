package ru.practicum.ewm.main.model.mapper;

import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.dto.comment.CommentDto;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .createdOn(comment.getCreatedOn())
                .isModerated(comment.getIsModerated())
                .build();
    }
}
