package com.example.demo.dto;

import lombok.Data;

@Data
public class TeamRequestDTO {
    private String teamName;
    private Long userId;   // client sends userId to link the team to a user
}