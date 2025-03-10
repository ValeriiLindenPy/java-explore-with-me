package ru.practicum.ewm.main.model.mapper;

import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.dto.category.CategoryDto;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
