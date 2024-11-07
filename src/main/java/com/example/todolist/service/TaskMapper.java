package com.example.todolist.service;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDto taskToTaskDto(Task task) {
        return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getCreationDate(), task.getTaskStatus());
    }

    public Task taskDtoToTask(TaskDto taskDto) {
        return new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getCreationDate(), taskDto.getTaskStatus());
    }
}
