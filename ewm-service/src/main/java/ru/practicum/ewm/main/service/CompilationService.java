package ru.practicum.ewm.main.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.model.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.model.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);

    CompilationDto create(NewCompilationDto newCompilation);

    CompilationDto update(Long compId, @Valid UpdateCompilationRequest compilation);

    void deleteById(Long compId);
}
