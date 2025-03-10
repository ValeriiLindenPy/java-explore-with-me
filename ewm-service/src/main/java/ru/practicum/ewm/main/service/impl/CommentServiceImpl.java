package ru.practicum.ewm.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exceptions.ConflictException;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.dto.comment.CommentDto;
import ru.practicum.ewm.main.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.main.model.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.main.model.mapper.CommentMapper;
import ru.practicum.ewm.main.repository.CommentRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.service.CommentService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d was not found".formatted(userId))
        );

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        return CommentMapper.toDto(repository.save(Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .build()));
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Comment> page = repository.findByEventIdAndIsModeratedTrue(eventId, pageable);

        return page.getContent().stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, UpdateCommentDto comment) {
        User author = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d was not found".formatted(userId))
        );

        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        Comment oldComment = repository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with id=%d was not found".formatted(commentId))
        );

        if (!oldComment.getAuthor().equals(author)) {
            throw new ConflictException("User with id=%d is not the author of the comment with id=%d".formatted(userId, commentId));
        }

        if (!oldComment.getText().equals(comment.getText())) {
            oldComment.setText(comment.getText());
        }

        return CommentMapper.toDto(repository.save(oldComment));
    }

    @Override
    public void moderateComment(Long commentId) {
        Comment comment = repository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with id=%d was not found".formatted(commentId))
        );

        if (comment.getIsModerated()) {
            throw new ConflictException("Comment with id=%d has already been moderated.".formatted(commentId));
        }

        comment.setIsModerated(true);

        repository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        repository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with id=%d was not found".formatted(commentId))
        );

        repository.deleteById(commentId);
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        User author = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d was not found".formatted(userId))
        );

        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        Comment comment = repository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with id=%d was not found".formatted(commentId))
        );

        if (!comment.getAuthor().equals(author)) {
            throw new ConflictException("User with id=%d is not the author of the comment with id=%d".formatted(userId, commentId));
        }

        repository.deleteById(commentId);
    }
}
