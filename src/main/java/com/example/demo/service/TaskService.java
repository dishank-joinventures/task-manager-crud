package com.example.demo.service;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.dto.TaskResponseDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Entity → ResponseDTO
    private TaskResponseDTO toResponseDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setTaskStatus(task.getTaskStatus());
        dto.setTaskCreatedAt(task.getTaskCreatedAt());
        dto.setTaskDueDate(task.getTaskDueDate());
        dto.setUserId(task.getUser().getUserId());
        dto.setUserName(task.getUser().getName());
        return dto;
    }

    // RequestDTO → Entity
    private Task toEntity(TaskRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        Task task = new Task();
        task.setTaskName(dto.getTaskName());
        task.setTaskStatus(dto.getTaskStatus());
        task.setTaskDueDate(dto.getTaskDueDate());
        task.setUser(user);
        return task;
    }

    // CREATE
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        return toResponseDTO(taskRepository.save(toEntity(dto)));
    }

    // READ ALL
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // READ ONE
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return toResponseDTO(task);
    }

    // READ ALL TASKS FOR A USER
    public List<TaskResponseDTO> getTasksByUserId(Long userId) {
        return taskRepository.findByUserUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // READ BY STATUS
    public List<TaskResponseDTO> getTasksByStatus(String status) {
        return taskRepository.findByTaskStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        task.setTaskName(dto.getTaskName());
        task.setTaskStatus(dto.getTaskStatus());
        task.setTaskDueDate(dto.getTaskDueDate());
        task.setUser(user);
        return toResponseDTO(taskRepository.save(task));
    }

    // DELETE
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}