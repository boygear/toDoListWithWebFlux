package com.example.todolist.rest;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Create new task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created task",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "400", description = "Problem with data validation", content = @Content(mediaType = "application/json",
                            schema = @Schema(hidden = true)))})
    @PostMapping
    public Mono<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public Flux<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @Operation(summary = "Get task by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task with specified ID or empty when task not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "400", description = "ID cannot be empty", content = @Content(mediaType = "application/json",
                            schema = @Schema(hidden = true)))})
    @GetMapping("/{id}")
    public Mono<TaskDto> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Update existing task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDto.class))),
                    @ApiResponse(responseCode = "400", description = "Problem with data validation", content = @Content(mediaType = "application/json",
                            schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json",
                            schema = @Schema(hidden = true)))})
    @PutMapping("/{id}")
    public Mono<TaskDto> updateTaskById(@PathVariable String id, @RequestBody TaskDto taskDto) {
        return taskService.updateTaskById(id, taskDto);
    }

    @Operation(summary = "Delete task by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task deleted",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "ID cannot be empty", content = @Content(mediaType = "application/json",
                            schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Task do not exist", content = @Content(mediaType = "application/json",
                            schema = @Schema(hidden = true)))})
    @DeleteMapping("/{id}")
    public Mono<Void> deleteTaskById(@PathVariable String id) {
        return taskService.deleteTaskById(id);
    }

}
