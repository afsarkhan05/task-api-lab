
package com.example.task.service;

import com.example.task.dto.CreateTaskRequest;
import com.example.task.dto.TaskResponse;
import com.example.task.dto.UpdateTaskRequest;
import com.example.task.dto.UpdateTaskStatusRequest;
import com.example.task.entity.Task;
import com.example.task.entity.TaskPriority;
import com.example.task.entity.TaskStatus;
import com.example.task.exception.TaskNotFoundException;
import com.example.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private UUID taskId;
    private Task task;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        task = new Task(
                "Original title",
                "Original description",
                TaskPriority.MEDIUM
        );
    }

    @Test
    void createShouldTrimTitleAndSaveTask() {
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskRequest request = new CreateTaskRequest(
                "  New task  ",
                "Task description",
                TaskPriority.HIGH
        );

        TaskResponse response = service.create(request);

        assertEquals("New task", response.title());
        assertEquals("Task description", response.description());
        assertEquals(TaskPriority.HIGH, response.priority());
        assertEquals(TaskStatus.OPEN, response.status());

        verify(repository).save(any(Task.class));
    }

    @Test
    void createShouldUseMediumPriorityWhenPriorityIsNull() {
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskRequest request = new CreateTaskRequest(
                "New task",
                "Task description",
                null
        );

        TaskResponse response = service.create(request);

        assertEquals(TaskPriority.MEDIUM, response.priority());
        verify(repository).save(any(Task.class));
    }

    @Test
    void findByIdShouldReturnExistingTask() {
        when(repository.findById(taskId)).thenReturn(Optional.of(task));

        TaskResponse response = service.findById(taskId);

        assertEquals("Original title", response.title());
        assertEquals("Original description", response.description());
        assertEquals(TaskStatus.OPEN, response.status());
        assertEquals(TaskPriority.MEDIUM, response.priority());

        verify(repository).findById(taskId);
    }

    @Test
    void findByIdShouldThrowWhenTaskDoesNotExist() {
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.findById(taskId)
        );

        verify(repository).findById(taskId);
    }

    @Test
    void findAllShouldReturnAllTasks() {
        when(repository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> responses = service.findAll();

        assertEquals(1, responses.size());
        assertEquals("Original title", responses.get(0).title());

        verify(repository).findAll();
    }

    @Test
    void updateShouldChangeTaskDetailsAndTrimTitle() {
        when(repository.findById(taskId)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateTaskRequest request = new UpdateTaskRequest(
                "  Updated title  ",
                "Updated description",
                TaskPriority.HIGH
        );

        TaskResponse response = service.update(taskId, request);

        assertEquals("Updated title", response.title());
        assertEquals("Updated description", response.description());
        assertEquals(TaskPriority.HIGH, response.priority());

        verify(repository).findById(taskId);
        verify(repository).save(task);
    }

    @Test
    void updateShouldThrowWhenTaskDoesNotExist() {
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated title",
                "Updated description",
                TaskPriority.HIGH
        );

        assertThrows(
                TaskNotFoundException.class,
                () -> service.update(taskId, request)
        );

        verify(repository, never()).save(any(Task.class));
    }

    @Test
    void updateStatusShouldChangeTaskStatus() {
        when(repository.findById(taskId)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateTaskStatusRequest request =
                new UpdateTaskStatusRequest(TaskStatus.IN_PROGRESS);

        TaskResponse response = service.updateStatus(taskId, request);

        assertEquals(TaskStatus.IN_PROGRESS, response.status());

        verify(repository).findById(taskId);
        verify(repository).save(task);
    }

    @Test
    void deleteShouldDeleteExistingTask() {
        when(repository.findById(taskId)).thenReturn(Optional.of(task));

        service.delete(taskId);

        verify(repository).findById(taskId);
        verify(repository).delete(task);
    }

    @Test
    void deleteShouldThrowWhenTaskDoesNotExist() {
        when(repository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.delete(taskId)
        );

        verify(repository, never()).delete(any(Task.class));
    }

    @Test
    void findAllWithNoFiltersShouldUsePagedRepositoryMethod() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(taskPage);

        Page<TaskResponse> response =
                service.findAll(null, null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Original title", response.getContent().get(0).title());

        verify(repository).findAll(pageable);
    }

    @Test
    void findAllWithStatusFilterShouldUseStatusQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(repository.findByStatus(TaskStatus.OPEN, pageable))
                .thenReturn(taskPage);

        Page<TaskResponse> response =
                service.findAll(TaskStatus.OPEN, null, pageable);

        assertEquals(1, response.getTotalElements());
        verify(repository).findByStatus(TaskStatus.OPEN, pageable);
    }

    @Test
    void findAllWithPriorityFilterShouldUsePriorityQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(repository.findByPriority(TaskPriority.MEDIUM, pageable))
                .thenReturn(taskPage);

        Page<TaskResponse> response =
                service.findAll(null, TaskPriority.MEDIUM, pageable);

        assertEquals(1, response.getTotalElements());
        verify(repository).findByPriority(TaskPriority.MEDIUM, pageable);
    }

    @Test
    void findAllWithBothFiltersShouldUseCombinedQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(repository.findByStatusAndPriority(
                TaskStatus.OPEN,
                TaskPriority.MEDIUM,
                pageable
        )).thenReturn(taskPage);

        Page<TaskResponse> response = service.findAll(
                TaskStatus.OPEN,
                TaskPriority.MEDIUM,
                pageable
        );

        assertEquals(1, response.getTotalElements());
        verify(repository).findByStatusAndPriority(
                TaskStatus.OPEN,
                TaskPriority.MEDIUM,
                pageable
        );
    }
}
