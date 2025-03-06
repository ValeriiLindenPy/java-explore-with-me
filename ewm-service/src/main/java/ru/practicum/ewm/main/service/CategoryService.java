package ru.practicum.ewm.main.service;

import jakarta.validation.Valid;
import ru.practicum.ewm.main.model.dto.category.CategoryDto;
import ru.practicum.ewm.main.model.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.model.dto.category.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getOne(Long catId);

    CategoryDto create(NewCategoryDto category);

    CategoryDto update(UpdateCategoryDto category, Long catId);

    void deleteById(Long catId);
}
