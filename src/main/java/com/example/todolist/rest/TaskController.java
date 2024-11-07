package com.example.todolist.rest;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tasks")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TaskController {

    TaskService taskService;

    @PostMapping
    public Mono<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @GetMapping
    public Flux<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Mono<TaskDto> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public Mono<TaskDto> updateTaskById(@PathVariable String id, @RequestBody TaskDto taskDto) {
        return taskService.updateTaskById(id, taskDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteTaskById(@PathVariable String id) {
        return taskService.deleteTaskById(id);
    }

}
