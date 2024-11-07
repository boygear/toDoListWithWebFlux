package com.example.todolist.service;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.entity.Task;
import com.example.todolist.entity.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 2, 2, 2);
    @Test
    void taskToTaskDto() {

        var task = new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var taskDto = new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var taskMapper = new TaskMapper();
        var result = taskMapper.taskToTaskDto(task);

        Assertions.assertEquals(result,taskDto);

    }

    @Test
    void taskDtoToTask() {
        var task = new Task("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var taskDto = new TaskDto("1", "Test Task", "Description", dateTime, TaskStatus.CREATED);
        var taskMapper = new TaskMapper();
        var result = taskMapper.taskDtoToTask(taskDto);

        Assertions.assertEquals(result,task);
    }
}