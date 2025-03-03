package ru.practicum.ewm.main.model.mapper;


import ru.practicum.ewm.main.model.Participation;
import ru.practicum.ewm.main.model.dto.ParticipationRequestDto;

public class ParticipationMapper {

    public static ParticipationRequestDto toDto(Participation participation) {
        return ParticipationRequestDto.builder()
                .id(participation.getId())
                .event(participation.getEvent().getId())
                .requester(participation.getRequester().getId())
                .status(participation.getStatus())
                .created(participation.getCreated())
                .build();
    }
}
