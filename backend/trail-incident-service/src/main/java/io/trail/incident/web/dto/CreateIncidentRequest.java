package io.trail.incident.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateIncidentRequest(@NotBlank(message = "title is required")
    @Size(max = 255)
    String title,

    @Size(max = 4000)
    String description,

    @NotBlank(message = "source is required")
    String source
) {    
}
