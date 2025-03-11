package ru.practicum.ewm.main.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.main.exceptions.ConflictException;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.EventState;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.CommentRepository;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.repository.filters.EventFilterBuilder;
import ru.practicum.ewm.main.model.dto.event.*;
import ru.practicum.ewm.main.model.enums.SortOption;
import ru.practicum.ewm.main.model.mapper.EventMapper;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.service.EventService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final EventFilterBuilder filterBuilder;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto getOne(Long id, String remoteAddr) {
        Event publishedEvent = repository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(id))
        );

        EventFullDto dto = EventMapper.toDto(publishedEvent);

        dto.setViews(getViewsForEvents(List.of(publishedEvent)).get(dto.getId()));

        return dto;
    }

    @Override
    @Transactional
    public EventFullDto getOne(Long userId, Long eventId) {
        Event publishedEvent = repository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("Event with id=%d was not found".formatted(eventId))
        );

        EventFullDto dto = EventMapper.toDto(publishedEvent);

        dto.setViews(getViewsForEvents(List.of(publishedEvent)).get(dto.getId()));

        return dto;
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

        Map<Long, Long> eventsViews = getViewsForEvents(page.getContent());

        Map<Long, Long> eventsComments = getCommentsForEvents(page.getContent());

        return page.getContent()
                .stream()
                .map(EventMapper::toShortDto)
                .map(dto -> {
                            dto.setViews(eventsViews.get(dto.getId()));
                            dto.setComments(eventsComments.get(dto.getId()));
                            return dto;
                        }
                )
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

        Map<Long, Long> eventsViews = getViewsForEvents(page.getContent());

        return page.getContent()
                .stream()
                .map(EventMapper::toDto)
                .map(dto -> {
                            dto.setViews(eventsViews.get(dto.getId()));
                            return dto;
                        }
                )
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

        Map<Long, Long> eventsViews = getViewsForEvents(page.getContent());

        return page.getContent()
                .stream()
                .map(EventMapper::toShortDto)
                .map(dto -> {
                            dto.setViews(eventsViews.get(dto.getId()));
                            return dto;
                        }
                )
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

    /**
     * Retrieves the view counts for a given list of events from a statistics service.
     * <p>
     * The method identifies the earliest creation date among the provided events and uses this
     * as the start of the time range. The current time is used as the end of the time range.
     * It then constructs URIs (in the format {@code /events/<eventId>}) for each event and
     * requests view statistics from the statsClient within the calculated time interval.
     * <p>
     * If the statistics service returns results, each returned URI is mapped to its corresponding
     * event ID, and the hits (view counts) are stored in the resulting map. If the statistics
     * service returns no results, each event is assigned a view count of zero.
     *
     * @param events the list of {@code Event} objects for which to retrieve view statistics.
     * @return a map where the key is the event ID (type {@code Long}) and the value is the
     * corresponding view count (type {@code Long}).
     */
    private Map<Long, Long> getViewsForEvents(List<Event> events) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime earliestEvent = events.getFirst().getCreatedOn();

        for (Event event : events) {
            if (event.getCreatedOn().isBefore(earliestEvent)) {
                earliestEvent = event.getCreatedOn();
            }
        }

        String start = earliestEvent.format(formatter);

        String end = LocalDateTime.now().format(formatter);

        Map<String, Long> uriIdMap = new HashMap<>();

        List<String> eventsUris = events.stream()
                .map(event -> {
                    String uri = "/events/" + event.getId();

                    uriIdMap.put(uri, event.getId());

                    return uri;
                }).toList();

        ResponseEntity<Object> response = statsClient.getViewStats(start, end, eventsUris, true);

        Object responseBody = response.getBody();

        ObjectMapper mapper = new ObjectMapper();

        List<ViewStats> viewStatsList = mapper.convertValue(responseBody,
                new TypeReference<>() {
                });

        Map<Long, Long> idsToViewsMap = new HashMap<>();

        if (viewStatsList.isEmpty()) {
            for (Event event : events) {
                idsToViewsMap.put(event.getId(), 0L);
            }
        } else {
            for (ViewStats viewStats : viewStatsList) {
                idsToViewsMap.put(uriIdMap.get(viewStats.getUri()), viewStats.getHits());
            }
        }

        return idsToViewsMap;
    }

    private Map<Long, Long> getCommentsForEvents(List<Event> events) {

        List<Comment> comments = commentRepository.findByEventIdInAndIsModeratedTrue(events.stream().map(Event::getId).toList());

        Map<Long, Long> idsToCommentsMap = new HashMap<>();

        for (Event event : events) {
            idsToCommentsMap.put(event.getId(), comments
                    .stream().filter(comment -> Objects.equals(comment.getEvent()
                            .getId(), event.getId())).count());
        }

        return idsToCommentsMap;
    }
}
