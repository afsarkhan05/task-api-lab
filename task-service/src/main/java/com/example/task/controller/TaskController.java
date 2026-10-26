package com.example.task.controller;

import com.example.task.dto.CreateTaskRequest;
import com.example.task.dto.TaskResponse;
import com.example.task.dto.UpdateTaskRequest;
import com.example.task.dto.UpdateTaskStatusRequest;
import com.example.task.entity.TaskPriority;
import com.example.task.entity.TaskStatus;
import com.example.task.service.TaskService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return service.create(request);
    }

/*    @GetMapping
    public List<TaskResponse> findAll() {
        return service.findAll();
    }*/

    @GetMapping
    public Page<TaskResponse> getAllTask(
            @RequestParam(required = false, name="status") TaskStatus status,
            @RequestParam(required = false, name="priority") TaskPriority priority,
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "title",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        return service.findAll(status, priority, pageable);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable("id") UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return service.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
