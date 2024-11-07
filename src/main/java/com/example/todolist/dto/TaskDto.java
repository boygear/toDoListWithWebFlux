package com.example.todolist.dto;

import com.example.todolist.entity.TaskStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TaskDto {

    @Setter(value=AccessLevel.NONE)
    String id;
    String title;
    String description;
    LocalDateTime creationDate;
    TaskStatus taskStatus;

}
