package ru.practicum.ewm.main.repository.filters;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.exceptions.DataValidationException;
import ru.practicum.ewm.main.model.QEvent;
import ru.practicum.ewm.main.model.dto.event.AdminEventFilterDto;
import ru.practicum.ewm.main.model.dto.event.EventFilterDto;
import ru.practicum.ewm.main.model.enums.EventState;

import java.time.LocalDateTime;

@Component
public class EventFilterBuilder {
    private final QEvent event = QEvent.event;

    public BooleanExpression buildPredicate(EventFilterDto dto) {
        BooleanExpression predicate = event.isNotNull()
                .and(event.state.eq(EventState.PUBLISHED));

        if (dto.getText() != null && !dto.getText().isBlank()) {
            predicate = predicate.and(
                    event.annotation.likeIgnoreCase("%" + dto.getText() + "%")
                            .or(event.description.likeIgnoreCase("%" + dto.getText() + "%"))
            );
        }

        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            predicate = predicate.and(event.category.id.in(dto.getCategories()));
        }

        if (dto.getPaid() != null) {
            predicate = predicate.and(event.paid.eq(dto.getPaid()));
        }

        LocalDateTime now = LocalDateTime.now();
        if (dto.getRangeStart() == null && dto.getRangeEnd() == null) {
            predicate = predicate.and(event.eventDate.after(now));
        } else if (dto.getRangeStart() != null && dto.getRangeEnd() != null) {
            if (dto.getRangeStart().isAfter(dto.getRangeEnd())) {
                throw new DataValidationException("Range start should be before range end.");
            }

            predicate = predicate.and(event.eventDate.between(dto.getRangeStart(), dto.getRangeEnd()));
        } else if (dto.getRangeStart() != null) {
            predicate = predicate.and(event.eventDate.after(dto.getRangeStart()));
        } else {
            predicate = predicate.and(event.eventDate.before(dto.getRangeEnd()));
        }

        if (dto.getOnlyAvailable() != null && dto.getOnlyAvailable()) {
            predicate = predicate.and(event.participantLimit.eq(0)
                    .or(event.confirmedRequests.lt(event.participantLimit)));
        }

        return predicate;
    }

    public BooleanExpression buildPredicate(AdminEventFilterDto dto) {
        BooleanExpression predicate = event.isNotNull();

        if (dto.getUsers() != null && !dto.getUsers().isEmpty()) {
            predicate = predicate.and(event.initiator.id.in(dto.getUsers()));
        }

        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            predicate = predicate.and(event.category.id.in(dto.getCategories()));
        }

        if (dto.getStates() != null && !dto.getStates().isEmpty()) {
            predicate = predicate.and(event.state.in(dto.getStates()));
        }

        if (dto.getRangeStart() != null && dto.getRangeEnd() != null) {
            predicate = predicate.and(event.eventDate.between(dto.getRangeStart(), dto.getRangeEnd()));
        } else if (dto.getRangeStart() != null) {
            predicate = predicate.and(event.eventDate.after(dto.getRangeStart()));
        } else if (dto.getRangeEnd() != null) {
            predicate = predicate.and(event.eventDate.before(dto.getRangeEnd()));
        }

        return predicate;
    }
}
