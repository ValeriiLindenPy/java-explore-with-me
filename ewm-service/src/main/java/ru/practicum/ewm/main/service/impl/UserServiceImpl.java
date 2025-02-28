package ru.practicum.ewm.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.dto.user.NewUserRequest;
import ru.practicum.ewm.main.model.dto.user.UserDto;
import ru.practicum.ewm.main.model.mapper.UserMapper;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        if (ids != null) {
            return repository.findByIdIn(ids).stream()
                    .skip(from)
                    .limit(size)
                    .map(UserMapper::toDto)
                    .toList();
        }

        return repository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        return UserMapper.toDto(repository.save(User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getName())
                .build()));
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("User with id=%d was not found".formatted(userId));
        }
    }
}
