package ru.practicum.ewm.main.model.mapper;

import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.dto.user.UserDto;
import ru.practicum.ewm.main.model.dto.user.UserShortDto;

public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
