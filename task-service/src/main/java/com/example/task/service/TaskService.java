package com.example.task.service;

import com.example.task.dto.CreateTaskRequest;
import com.example.task.dto.TaskResponse;
import com.example.task.dto.UpdateTaskRequest;
import com.example.task.dto.UpdateTaskStatusRequest;
import com.example.task.entity.Task;
import com.example.task.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task(request.title().trim(), request.description(), request.priority());
        return TaskResponse.from(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return repository.findAll().stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(UUID id) {
        return TaskResponse.from(getTask(id));
    }

    public TaskResponse update(UUID id, UpdateTaskRequest request) {
        Task task = getTask(id);
        task.updateDetails(request.title().trim(), request.description(), request.priority());
        return TaskResponse.from(repository.save(task));
    }

    public TaskResponse updateStatus(UUID id, UpdateTaskStatusRequest request) {
        Task task = getTask(id);
        task.updateStatus(request.status());
        return TaskResponse.from(repository.save(task));
    }

    public void delete(UUID id) {
        Task task = getTask(id);
        repository.delete(task);
    }

    private Task getTask(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + id));
    }
}
