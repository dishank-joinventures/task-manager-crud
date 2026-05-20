package com.example.demo.dto;

import lombok.Data;

@Data
public class TeamResponseDTO {
    private Long teamId;
    private String teamName;
    private Long userId;     // just return userId, not the full user object
    private String userName; // handy to show who the team belongs to
}