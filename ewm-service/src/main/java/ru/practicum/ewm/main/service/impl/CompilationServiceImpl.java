package ru.practicum.ewm.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.model.mapper.CompilationMapper;
import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.service.CompilationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        if (pinned != null && pinned) {
            return compilationRepository.findByPinnedTrue().stream()
                    .skip(from)
                    .limit(size)
                    .map(CompilationMapper::toDto)
                    .toList();
        } else {
            return compilationRepository.findAll().stream()
                    .skip(from)
                    .limit(size)
                    .map(CompilationMapper::toDto)
                    .toList();
        }
    }

    @Override
    public CompilationDto getById(Long compId) {
        return CompilationMapper.toDto(compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id=%d was not found".formatted(compId))
        ));
    }
}
