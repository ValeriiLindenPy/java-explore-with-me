package ru.practicum.ewm.main.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.main.model.dto.user.NewUserRequest;
import ru.practicum.ewm.main.model.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    UserDto create(@Valid NewUserRequest newUserRequest);

    void deleteById(Long userId);
}
