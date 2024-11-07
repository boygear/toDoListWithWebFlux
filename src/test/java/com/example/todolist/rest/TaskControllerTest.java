package com.example.todolist.rest;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.entity.TaskStatus;
import com.example.todolist.service.TaskService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskController taskController;


    @Test
    void createTask() {
        var taskDto = new TaskDto("1", "Test Task", "Description", LocalDateTime.now(), TaskStatus.CREATED);
        when(taskService.createTask(any(TaskDto.class))).thenReturn(Mono.just(taskDto));

        var result = taskController.createTask(taskDto);

        assertEquals(taskDto, result.block());
        verify(taskService).createTask(taskDto);
    }

    @Test
    void getAllTasks() {
        var tasks = List.of(new TaskDto("1", "Test Task", "Description", LocalDateTime.now(), TaskStatus.CREATED),
                new TaskDto("2", "Test Task", "Description", LocalDateTime.now(), TaskStatus.CREATED));

        when(taskService.getAllTasks()).thenReturn(Flux.fromIterable(tasks));
        var result = taskController.getAllTasks();

        StepVerifier.create(result)
                .expectNext(tasks.get(0))
                .expectNext(tasks.get(1))
                .verifyComplete();

    }

    @Test
    void getTaskById() {
        var taskDto = new TaskDto("1", "Test Task", "Description", LocalDateTime.now(), TaskStatus.CREATED);
        when(taskService.getTaskById("1")).thenReturn(Mono.just(taskDto));

        var result = taskController.getTaskById("1");

        assertEquals(taskDto, result.block());
        verify(taskService).getTaskById("1");
    }

    @Test
    void updateTaskById() {
        var taskDto = new TaskDto("1", "Test Task", "Description", LocalDateTime.now(), TaskStatus.CREATED);
        when(taskService.updateTaskById("1",taskDto)).thenReturn(Mono.just(taskDto));

        var result = taskController.updateTaskById("1",taskDto);

        assertEquals(taskDto, result.block());
        verify(taskService).updateTaskById("1",taskDto);
    }

    @Test
    void deleteTaskById() {
        when(taskService.deleteTaskById("1")).thenReturn(Mono.empty());

        var result = taskController.deleteTaskById("1");

        assertNull(result.block());
        verify(taskService).deleteTaskById("1");
    }
}
