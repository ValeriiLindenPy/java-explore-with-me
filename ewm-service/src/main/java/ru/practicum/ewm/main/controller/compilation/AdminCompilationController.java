package ru.practicum.ewm.main.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.main.model.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.main.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.service.CompilationService;


@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilation) {
        return service.create(newCompilation);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable("compId") Long compId, @RequestBody @Valid UpdateCompilationRequest compilation) {
        return service.update(compId, compilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long compId) {
        service.deleteById(compId);
    }
}
