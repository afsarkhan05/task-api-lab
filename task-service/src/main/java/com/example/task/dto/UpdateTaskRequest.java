package com.example.task.dto;

import com.example.task.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        TaskPriority priority
) {
}
