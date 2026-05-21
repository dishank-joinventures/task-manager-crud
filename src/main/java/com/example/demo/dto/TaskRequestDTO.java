package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequestDTO {
    @NotBlank(message = "Task name is required")
    private String taskName;

    @NotBlank(message = "Task status is required")
    private String taskStatus;

    @NotNull(message = "Task due date is required")
    private LocalDateTime taskDueDate;
    @NotNull(message = "User ID is required")
    private Long userId;           // assigned to
}
