package ru.practicum.ewm.main.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0", required = false) Integer from,
                                       @RequestParam(defaultValue = "10", required = false) Integer size) {
        return service.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getAllById(@PathVariable("compId") Long compId) {
        return service.getById(compId);
    }
}
