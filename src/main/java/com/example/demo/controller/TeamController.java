package com.example.demo.controller;

import com.example.demo.dto.TeamRequestDTO;
import com.example.demo.dto.TeamResponseDTO;
import com.example.demo.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // POST /api/teams
    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO request) {
        return ResponseEntity.ok(teamService.createTeam(request));
    }

    // GET /api/teams
    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/all-with-users")
    public ResponseEntity<List<TeamResponseDTO>> getAllTeamsWithUsers() {
        return ResponseEntity.ok(teamService.getAllTeamsWithUsers());
    }

    // GET /api/teams/{id}
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    // GET /api/teams/user/{userId} — all teams for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(teamService.getTeamsByUserId(userId));
    }

    // PUT /api/teams/{id}
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequestDTO request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    // DELETE /api/teams/{id}
    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamResponseDTO>> searchTeamsByName(@RequestParam String teamName) {
        return ResponseEntity.ok(teamService.searchTeamsByName(teamName));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByUserName(@RequestParam String userName) {
        return ResponseEntity.ok(teamService.getTeamsByUserName(userName));
    }
}
