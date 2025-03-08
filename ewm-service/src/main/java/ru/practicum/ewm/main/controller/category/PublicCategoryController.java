package ru.practicum.ewm.main.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.dto.category.CategoryDto;
import ru.practicum.ewm.main.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0", required = false) Integer from,
                                    @RequestParam(defaultValue = "10", required = false) Integer size) {
        return service.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getOne(@PathVariable("catId") Long catId) {
        return service.getOne(catId);
    }
}
