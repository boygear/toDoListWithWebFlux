package com.example.todolist.service;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.entity.Task;
import com.example.todolist.entity.TaskStatus;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 2, 2, 2);
    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskMapper taskMapper;

    @InjectMocks
    TaskService taskService;

    @Test
    void getAllTasks() {
        var tasks = List.of(new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED),
                new Task("2", "Test Task", "Description", dateTime, TaskStatus.CREATED));

        var dtoTasks = List.of(new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED),
                new TaskDto("2", "Test Task", "Description", dateTime, TaskStatus.CREATED));

        when(taskRepository.findAll()).thenReturn(Flux.fromIterable(tasks));
        when(taskMapper.taskToTaskDto(tasks.get(0))).thenReturn(new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED));
        when(taskMapper.taskToTaskDto(tasks.get(1))).thenReturn(new TaskDto("2", "Test Task", "Description", dateTime, TaskStatus.CREATED));

        var result = taskService.getAllTasks();

        StepVerifier.create(result)
                .expectNext(dtoTasks.get(0))
                .expectNext(dtoTasks.get(1))
                .verifyComplete();
    }

    @Test
    void createTask() {
        var task = new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var taskDto = new TaskDto(null, "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var taskDtoAfterSave = new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        when(taskRepository.save(task)).thenReturn(Mono.just(task));
        when(taskMapper.taskDtoToTask(taskDto)).thenReturn(task);
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDtoAfterSave);

        var result = taskService.createTask(taskDto);

        result.subscribe(t -> {
            Assertions.assertNotNull(t.getId());
        });
    }

    @Test
    void createTaskWithEmptyTitle() {
        var taskDto = new TaskDto(null, null, "Description", dateTime, TaskStatus.CREATED);

        var result = taskService.createTask(taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Title cannot be empty\"").verify();
    }

    @Test
    void createTaskWithTooLongTitle() {
        var taskDto = new TaskDto(null, generateLongString(), "Description", dateTime, TaskStatus.CREATED);

        var result = taskService.createTask(taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Title cannot exceed 100 characters\"").verify();
    }

    @Test
    void createTaskWithTooLongDescription() {
        var taskDto = new TaskDto(null, "Test Task", generateLongString(), dateTime, TaskStatus.CREATED);

        var result = taskService.createTask(taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Description cannot exceed 500 characters\"").verify();
    }

    @Test
    void createTaskWithWrongDate() {
        var taskDto = new TaskDto(null, "Test Task", "Description", dateTime.plusYears(5), TaskStatus.CREATED);

        var result = taskService.createTask(taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Creation date cannot be in the future\"").verify();
    }

    @Test
    void createTaskWithNullDate() {
        var taskDto = new TaskDto(null, "Test Task", "Description", null, TaskStatus.CREATED);

        var result = taskService.createTask(taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Creation date is required\"").verify();
    }

    @Test
    void createTaskWithNullStatus() {
        var taskDto = new TaskDto(null, "Test Task", "Description", dateTime, null);

        var result = taskService.createTask(taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Task status is required\"").verify();
    }


    @Test
    void getTaskById() {
        var task = new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);

        var dtoTask = new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);

        when(taskRepository.findById("1")).thenReturn(Mono.just(task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(dtoTask);

        var result = taskService.getTaskById("1");

        StepVerifier.create(result)
                .expectNext(dtoTask)
                .verifyComplete();
    }

    @Test
    void getTaskByIdNoResult() {
        when(taskRepository.findById("1")).thenReturn(Mono.empty());

        var result = taskService.getTaskById("1");

        StepVerifier.create(result).verifyComplete();
        verifyNoInteractions(taskMapper);
    }


    @Test
    void updateTaskById() {
        var task = new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var dtoTask = new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);

        when(taskRepository.existsById("1")).thenReturn(Mono.just(true));
        when(taskRepository.save(task)).thenReturn(Mono.just(task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(dtoTask);
        when(taskMapper.taskDtoToTask(dtoTask)).thenReturn(task);


        var result = taskService.updateTaskById("1", dtoTask);

        StepVerifier.create(result).expectNext(dtoTask).verifyComplete();
    }

    @Test
    void updateTaskByIdWithNullId() {
        var taskDto = new TaskDto(null, "Test Task", "Description", dateTime, null);

        var result = taskService.updateTaskById(null, taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"ID cannot be empty\"").verify();
    }

    @Test
    void updateTaskByIdWhenDoesNotExist() {
        var task = new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var dtoTask = new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);

        when(taskRepository.existsById("1")).thenReturn(Mono.just(false));
        when(taskMapper.taskDtoToTask(dtoTask)).thenReturn(task);


        var result = taskService.updateTaskById("1", dtoTask);

        StepVerifier.create(result).expectErrorMessage("404 NOT_FOUND \"Task do not exist\"").verify();
    }


    @Test
    void updateTaskByIdWithNoTitle() {
        var taskDto = new TaskDto(null, null, "Description", dateTime, TaskStatus.CREATED);

        var result = taskService.updateTaskById("1", taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Title cannot be empty\"").verify();
    }

    @Test
    void updateTaskByIdWithTooLongTitle() {
        var taskDto = new TaskDto(null, generateLongString(), "Description", dateTime, TaskStatus.CREATED);

        var result = taskService.updateTaskById("1", taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Title cannot exceed 100 characters\"").verify();
    }

    @Test
    void updateTaskByIdWithTooLongDescription() {
        var taskDto = new TaskDto(null, "Test Task", generateLongString(), dateTime, TaskStatus.CREATED);

        var result = taskService.updateTaskById("1", taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Description cannot exceed 500 characters\"").verify();
    }

    @Test
    void updateTaskByIdWithNoCreationDate() {
        var taskDto = new TaskDto(null, "Test Task", "Description", null, TaskStatus.CREATED);

        var result = taskService.updateTaskById("1", taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Creation date is required\"").verify();
    }

    @Test
    void updateTaskByIdWithWrongCreationDate() {
        var taskDto = new TaskDto(null, "Test Task", "Description", dateTime.plusYears(5), TaskStatus.CREATED);

        var result = taskService.updateTaskById("1", taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Creation date cannot be in the future\"").verify();
    }

    @Test
    void updateTaskByIdWithNoStatus() {
        var taskDto = new TaskDto(null, "Test Task", "Description", dateTime, null);

        var result = taskService.updateTaskById("1", taskDto);

        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"Task status is required\"").verify();
    }

    @Test
    void deleteTaskById() {
        when(taskRepository.existsById("1")).thenReturn(Mono.just(true));
        when(taskRepository.deleteById("1")).thenReturn(Mono.empty());

        var result = taskService.deleteTaskById("1");
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void deleteTaskByIdWhenDoesNotExists() {
        when(taskRepository.existsById("1")).thenReturn(Mono.just(false));

        var result = taskService.deleteTaskById("1");
        StepVerifier.create(result).expectErrorMessage("404 NOT_FOUND \"Task do not exist\"").verify();
        verify(taskRepository, never()).deleteById("1");
    }

    @Test
    void deleteTaskByIdWithNoId() {

        var result = taskService.deleteTaskById(null);
        StepVerifier.create(result).expectErrorMessage("400 BAD_REQUEST \"ID cannot be empty\"").verify();
        verifyNoInteractions(taskRepository);
    }


    String generateLongString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 1000;
        var random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

    }
}