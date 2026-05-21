package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamRequestDTO {
    @NotBlank(message = "Team name is required")
    private String teamName;

    @NotNull(message = "User ID is required")
    private Long userId;   // client sends userId to link the team to a user
}
