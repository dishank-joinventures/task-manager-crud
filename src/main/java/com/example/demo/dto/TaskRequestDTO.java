package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequestDTO {
    private String taskName;
    private String taskStatus;
    private LocalDateTime taskDueDate;
    private Long userId;           // assigned to
}