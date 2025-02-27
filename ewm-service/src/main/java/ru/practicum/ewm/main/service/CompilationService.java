package ru.practicum.ewm.main.service;

import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);
}
