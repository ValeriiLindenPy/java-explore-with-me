package ru.practicum.ewm.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exceptions.ConflictException;
import ru.practicum.ewm.main.exceptions.NotFoundException;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.dto.category.CategoryDto;
import ru.practicum.ewm.main.model.dto.category.NewCategoryDto;
import ru.practicum.ewm.main.model.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.main.model.mapper.CategoryMapper;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        return repository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getOne(Long catId) {
        return CategoryMapper.toDto(repository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id=%d was not found".formatted(catId))
        ));
    }

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto category) {
        return CategoryMapper.toDto(repository.save(Category.builder()
                .name(category.getName())
                .build()));
    }

    @Override
    @Transactional
    public CategoryDto update(UpdateCategoryDto dto, Long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=%d was not found".formatted(catId)));

        if (!dto.getName().equals(category.getName())) {
            category.setName(dto.getName());
        }
        Category savedCategory = repository.save(category);
        repository.flush();
        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }

        repository.deleteById(catId);
    }
}
