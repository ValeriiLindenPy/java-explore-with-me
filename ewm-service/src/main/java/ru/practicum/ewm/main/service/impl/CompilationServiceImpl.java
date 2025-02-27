package ru.practicum.ewm.main.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.model.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.model.mapper.CompilationMapper;
import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.service.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

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

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilation) {
        List<Long> uniqueEvents = Optional.ofNullable(newCompilation.getEvents())
                .orElse(List.of())
                .stream()
                .distinct()
                .toList();

        List<Event> events = eventRepository.findByIdIn(uniqueEvents);

        if (uniqueEvents.size() != events.size()){
            throw new NotFoundException("One or more events where not found");
        }

        Compilation compilation = Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(Optional.ofNullable(newCompilation.getPinned()).orElse(false))
                .events(events)
                .build();

        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest compilation) {
        try {
            Compilation oldCompilation = compilationRepository.getReferenceById(compId);
            if(compilation.getPinned() != oldCompilation.getPinned()) {
                oldCompilation.setPinned(compilation.getPinned());
            }

            List<Long> uniqueEvents = Optional.ofNullable(compilation.getEvents())
                    .orElse(List.of())
                    .stream()
                    .distinct()
                    .toList();

            List<Event> eventsList = eventRepository.findByIdIn(uniqueEvents);

            if (uniqueEvents.size() != eventsList.size()){
                throw new NotFoundException("One or more events where not found");
            }

            Set<Event> events = new HashSet<>(eventsList);
            Set<Event> oldEvents = new HashSet<>(oldCompilation.getEvents());

            if(!oldEvents.equals(events)) {
                oldCompilation.setEvents(eventsList);
            }

            if (!oldCompilation.getTitle().equals(compilation.getTitle())) {
                oldCompilation.setTitle(compilation.getTitle());
            }

            return CompilationMapper.toDto(compilationRepository.save(oldCompilation));

        }catch (EntityNotFoundException ex) {
            throw new NotFoundException("Compilation with id=%d was not found".formatted(compId));
        }
    }

    @Override
    public void deleteById(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        }catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Compilation with id=%d was not found".formatted(compId));
        }
    }
}
