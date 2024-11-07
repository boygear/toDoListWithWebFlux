package com.example.todolist.service;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.repository.TaskRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TaskService {
    TaskRepository taskRepository;
    TaskMapper taskMapper;

    public Flux<TaskDto> getAllTasks() {
        return taskRepository.findAll().map(taskMapper::taskToTaskDto);
    }

    public Mono<TaskDto> createTask(TaskDto taskDto) {
        return validateTaskDto(taskDto)
                .flatMap(validatedTaskDto -> {
                    var task = taskMapper.taskDtoToTask(validatedTaskDto);
                    return taskRepository.save(task)
                            .map(taskMapper::taskToTaskDto);
                });
    }

    public Mono<TaskDto> getTaskById(String id) {
        return taskRepository.findById(id).map(taskMapper::taskToTaskDto);
    }

    public Mono<TaskDto> updateTaskById(String id, TaskDto taskDto) {
        if (id == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be empty"));
        }
        var validateResult = validateTaskDto(taskDto);

        return validateResult.map(taskDto1 -> {
                    var task = taskMapper.taskDtoToTask(taskDto);
                    task.setId(id);
                    return task;
                }).flatMap(task -> taskRepository.existsById(id)
                        .filter(exists -> exists)
                        .flatMap(exists -> taskRepository.save(task))
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task do not exist"))))
                .map(taskMapper::taskToTaskDto);
    }

    public Mono<Void> deleteTaskById(String id) {
        if (id == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be empty"));
        }
        return taskRepository.existsById(id)
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task do not exist")))
                .flatMap(exists -> taskRepository.deleteById(id));

    }

    private Mono<TaskDto> validateTaskDto(TaskDto taskDto) {
        if (taskDto.getTitle() == null || taskDto.getTitle().isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be empty"));
        }

        if (taskDto.getTitle().length() > 100) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot exceed 100 characters"));
        }

        if (taskDto.getDescription() != null && taskDto.getDescription().length() > 500) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description cannot exceed 500 characters"));
        }

        if (taskDto.getCreationDate() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creation date is required"));
        }

        if (taskDto.getCreationDate().isAfter(LocalDateTime.now())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Creation date cannot be in the future"));
        }

        if (taskDto.getTaskStatus() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task status is required"));
        }
        return Mono.just(taskDto);
    }
}
