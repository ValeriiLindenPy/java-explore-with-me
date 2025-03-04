package ru.practicum.ewm.main.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exceptions.ConflictException;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.EventState;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.repository.filters.EventFilterBuilder;
import ru.practicum.ewm.main.model.dto.event.*;
import ru.practicum.ewm.main.model.enums.SortOption;
import ru.practicum.ewm.main.model.mapper.EventMapper;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventFilterBuilder filterBuilder;

    @Override
    @Transactional
    public EventFullDto getOne(Long id, String remoteAddr) {
        Event publishedEvent = repository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(id))
        );

        publishedEvent.getViews().add(remoteAddr);

        repository.save(publishedEvent);

        return EventMapper.toDto(publishedEvent);
    }

    @Override
    @Transactional
    public EventFullDto getOne(Long userId, Long eventId) {
        Event publishedEvent = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        return EventMapper.toDto(publishedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAll(EventFilterDto filterDto) {
        BooleanExpression predicate = filterBuilder.buildPredicate(filterDto);

        Pageable pageable = PageRequest.of(
                filterDto.getFrom() / filterDto.getSize(),
                filterDto.getSize(),
                createSort(filterDto.getSort())
        );

        Page<Event> page = repository.findAll(predicate, pageable);

        return page.getContent()
                .stream()
                .map(EventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAll(AdminEventFilterDto filterDto) {
        BooleanExpression predicate = filterBuilder.buildPredicate(filterDto);

        Pageable pageable = PageRequest.of(
                filterDto.getFrom() / filterDto.getSize(),
                filterDto.getSize()
        );

        Page<Event> page = repository.findAll(predicate, pageable);

        return page.getContent()
                .stream()
                .map(EventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(
                from / size,
                size
        );

        Page<Event> page = repository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(EventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest request) {
        Event oldEvent = repository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        if (request.getTitle() != null && !request.getTitle().equals(oldEvent.getTitle())) {
            oldEvent.setTitle(request.getTitle());
        }

        if (request.getAnnotation() != null && !request.getAnnotation().equals(oldEvent.getAnnotation())) {
            oldEvent.setAnnotation(request.getAnnotation());
        }

        if (request.getCategory() != null) {
            Category newCategory = categoryRepository.findById(request.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=%d was not found".formatted(request.getCategory()))
            );

            if (!newCategory.equals(oldEvent.getCategory())) {
                oldEvent.setCategory(newCategory);
            }
        }

        if (request.getDescription() != null && !request.getDescription().equals(oldEvent.getDescription())) {
            oldEvent.setDescription(request.getDescription());
        }

        if (request.getEventDate() != null && !request.getEventDate().equals(oldEvent.getEventDate())) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Event date must be at least one hour after the current time.");
            }
            oldEvent.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null && !request.getLocation().equals(oldEvent.getLocation())) {
            oldEvent.setLocation(request.getLocation());
        }

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case REJECT_EVENT -> {
                    if (oldEvent.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Published events cannot be rejected.");
                    }
                    oldEvent.setState(EventState.CANCELED);
                }
                case PUBLISH_EVENT -> {
                    if (oldEvent.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish the event because it's not in the right state: %s".formatted(oldEvent.getState()));
                    }
                    oldEvent.setState(EventState.PUBLISHED);
                    oldEvent.setPublishedOn(LocalDateTime.now());
                }
            }
        }

        if (request.getPaid() != null && !request.getPaid().equals(oldEvent.getPaid())) {
            oldEvent.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null
                && !request.getParticipantLimit().equals(oldEvent.getParticipantLimit())) {
            oldEvent.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null
                && !request.getRequestModeration().equals(oldEvent.getRequestModeration())) {
            oldEvent.setRequestModeration(request.getRequestModeration());
        }

        return EventMapper.toDto(repository.save(oldEvent));
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event oldEvent = repository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        if (oldEvent.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getTitle() != null && !request.getTitle().equals(oldEvent.getTitle())) {
            oldEvent.setTitle(request.getTitle());
        }

        if (request.getAnnotation() != null && !request.getAnnotation().equals(oldEvent.getAnnotation())) {
            oldEvent.setAnnotation(request.getAnnotation());
        }

        if (request.getCategory() != null) {
            Category newCategory = categoryRepository.findById(request.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=%d was not found".formatted(request.getCategory()))
            );

            if (!newCategory.equals(oldEvent.getCategory())) {
                oldEvent.setCategory(newCategory);
            }
        }

        if (request.getDescription() != null && !request.getDescription().equals(oldEvent.getDescription())) {
            oldEvent.setDescription(request.getDescription());
        }

        if (request.getEventDate() != null && !request.getEventDate().equals(oldEvent.getEventDate())) {
            if (!request.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s".formatted(request.getEventDate().toString()));
            }

            oldEvent.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null && !request.getLocation().equals(oldEvent.getLocation())) {
            oldEvent.setLocation(request.getLocation());
        }

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case CANCEL_REVIEW -> oldEvent.setState(EventState.CANCELED);
                case SEND_TO_REVIEW -> oldEvent.setState(EventState.PENDING);
            }
        }

        if (request.getPaid() != null && !request.getPaid().equals(oldEvent.getPaid())) {
            oldEvent.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null
                && !request.getParticipantLimit().equals(oldEvent.getParticipantLimit())) {
            oldEvent.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null
                && !request.getRequestModeration().equals(oldEvent.getRequestModeration())) {
            oldEvent.setRequestModeration(request.getRequestModeration());
        }

        return EventMapper.toDto(repository.save(oldEvent));
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto event) {
        User initiator = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d was not found".formatted(userId))
        );

        Category category = categoryRepository.findById(event.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=%d was not found".formatted(event.getCategory()))
        );

        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s".formatted(event.getEventDate().toString()));
        }

        return EventMapper.toDto(repository.save(Event.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .category(category)
                .initiator(initiator)
                .state(EventState.PENDING)
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .build()));
    }

    private Sort createSort(SortOption sort) {
        if (sort == null) {
            return Sort.by(Sort.Direction.DESC, "id");
        }

        return switch (sort) {
            case VIEWS -> Sort.by(Sort.Direction.DESC, "views");
            case EVENT_DATE -> Sort.by(Sort.Direction.DESC, "eventDate");
        };
    }
}
