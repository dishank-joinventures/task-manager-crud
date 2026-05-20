package com.example.demo.repository;

import com.example.demo.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByUserUserId(Long userId); // fetch all teams for a user
    // All teams with their users (for showing usernames in each team)
    @Query("SELECT t FROM Team t JOIN FETCH t.user")
    List<Team> findAllWithUsers();

    // Filter teams by name
    List<Team> findByTeamNameContainingIgnoreCase(String teamName);

    // Filter teams by username
    @Query("SELECT t FROM Team t JOIN t.user u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :userName, '%'))")
    List<Team> findByUserName(@Param("userName") String userName);
}