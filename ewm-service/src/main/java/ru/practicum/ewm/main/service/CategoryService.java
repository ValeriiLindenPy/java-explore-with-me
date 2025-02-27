package ru.practicum.ewm.main.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.main.model.dto.category.CategoryDto;
import ru.practicum.ewm.main.model.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getOne(Long catId);

    CategoryDto create(@Valid NewCategoryDto category);

    CategoryDto update(@Valid NewCategoryDto category, Long catId);

    void deleteById(Long catId);
}
