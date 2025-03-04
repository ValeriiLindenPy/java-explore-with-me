package ru.practicum.ewm.main.model.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;

    @Builder.Default
    private Boolean pinned = false;

    private List<Long> events;
}
