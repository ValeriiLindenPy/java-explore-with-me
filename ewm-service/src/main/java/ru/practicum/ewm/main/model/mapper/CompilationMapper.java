package ru.practicum.ewm.main.model.mapper;

import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream().map(EventMapper::toShortDto).toList())
                .pinned(compilation.getPinned())
                .build();
    }
}
