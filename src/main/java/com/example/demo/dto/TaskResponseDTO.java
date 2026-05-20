package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long taskId;
    private String taskName;
    private String taskStatus;
    private LocalDateTime taskCreatedAt;   // auto generated, shown in response
    private LocalDateTime taskDueDate;
    private Long userId;
    private String userName;               // who the task is assigned to
}