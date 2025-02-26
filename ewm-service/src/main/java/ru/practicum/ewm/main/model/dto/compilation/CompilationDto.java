package ru.practicum.ewm.main.model.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.model.dto.event.EventShortDto;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;

    private String title;

    private Boolean pinned;

    private List<EventShortDto> events;
}
