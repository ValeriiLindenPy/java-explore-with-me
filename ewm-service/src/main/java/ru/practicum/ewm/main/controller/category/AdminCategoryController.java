package ru.practicum.ewm.main.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.category.CategoryDto;
import ru.practicum.ewm.main.model.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.model.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.main.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto category) {
        return service.create(category);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@RequestBody @Valid UpdateCategoryDto category,
                              @PathVariable("catId") Long catId) {
        return service.update(category, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("catId") Long catId) {
        service.deleteById(catId);
    }
}
