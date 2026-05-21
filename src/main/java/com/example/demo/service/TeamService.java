package com.example.demo.services;

import com.example.demo.dto.TeamRequestDTO;
import com.example.demo.dto.TeamResponseDTO;
import com.example.demo.model.Team;
import com.example.demo.model.User;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService{
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    private TeamResponseDTO toResponseDTO (Team team)
    {
        TeamResponseDTO dto=new TeamResponseDTO();
        dto.setTeamId(team.getTeamId());
        dto.setTeamName(team.getTeamName());
        dto.setUserId(team.getUser().getUserId());
        dto.setUserName(team.getUser().getName());
        return dto;
    }

    private Team toEntity(TeamRequestDTO dto)
    {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + dto.getUserId()));
        Team team = new Team();
        team.setTeamName(dto.getTeamName());
        team.setUser(user);
        return team;
    }

    public TeamResponseDTO createTeam(TeamRequestDTO dto)
    {
        return toResponseDTO(teamRepository.save(toEntity(dto)));
    }

    // READ ALL
    public List<TeamResponseDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TeamResponseDTO> getAllTeamsWithUsers() {
        return teamRepository.findAllWithUsers()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // READ ONE
    public TeamResponseDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
        return toResponseDTO(team);
    }

    // READ ALL TEAMS FOR A USER
    public List<TeamResponseDTO> getTeamsByUserId(Long userId) {
        return teamRepository.findByUserUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + dto.getUserId()));
        team.setTeamName(dto.getTeamName());
        team.setUser(user);
        return toResponseDTO(teamRepository.save(team));
    }

    // DELETE
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    public List<TeamResponseDTO> searchTeamsByName(String teamName) {
        return teamRepository.findByTeamNameContainingIgnoreCase(teamName)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TeamResponseDTO> getTeamsByUserName(String userName) {
        return teamRepository.findByUserName(userName)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}


