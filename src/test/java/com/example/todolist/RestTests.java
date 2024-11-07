package com.example.todolist;

import com.example.todolist.dto.TaskDto;
import com.example.todolist.entity.TaskStatus;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTests {

    @Autowired
    WebTestClient client;

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void deleteAllDatabase() {
        taskRepository.deleteAll().block();
    }

    @Test
    void saveTest() {
        var taskDto = new TaskDto(null, "testTitle2", "testDescription2", LocalDateTime.now(), TaskStatus.IN_PROGRESS);

        var response = client.post()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDto.class)
                .returnResult();

        var task = response.getResponseBody();
        assertThat(task).isNotNull();
        assertThat(task.getId()).isNotBlank();
        assertThat(task.getTitle()).isEqualTo(taskDto.getTitle());
        assertThat(task.getDescription()).isEqualTo(taskDto.getDescription());
        assertThat(task.getCreationDate()).isEqualTo(taskDto.getCreationDate());
        assertThat(task.getTaskStatus()).isEqualTo(taskDto.getTaskStatus());
    }

    @Test
    void saveTestWithNoTitle() {
        var taskDto = new TaskDto(null, "", "testDescription2", LocalDateTime.now(), TaskStatus.IN_PROGRESS);

        client.post()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void saveTestWithWrongTime() {
        var taskDto = new TaskDto(null, "", "testDescription2", LocalDateTime.now().plusMinutes(10), TaskStatus.IN_PROGRESS);

        client.post()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateTest() {
        var taskDto = new TaskDto(null, "testTitle1", "testDescriptionUpdated", LocalDateTime.now(), TaskStatus.FINISHED);

        var createResponse = client.post()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDto.class)
                .returnResult();

        var createdTask = createResponse.getResponseBody();
        Assertions.assertNotNull(createdTask);
        var id = createdTask.getId();

        var response = client.put()
                .uri("/tasks/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDto.class)
                .returnResult();

        var task = response.getResponseBody();
        assertThat(task).isNotNull();
        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getTitle()).isEqualTo(taskDto.getTitle());
        assertThat(task.getDescription()).isEqualTo(taskDto.getDescription());
        assertThat(task.getCreationDate()).isEqualTo(taskDto.getCreationDate());
        assertThat(task.getTaskStatus()).isEqualTo(taskDto.getTaskStatus());
    }

    @Test
    void updateTestWithWrongID() {
        var taskDto = new TaskDto(null, "testTitle1", "testDescriptionUpdated", LocalDateTime.now(), TaskStatus.FINISHED);
        client.put()
                .uri("/tasks/xxx")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllTasksTest() {
        var tasks = List.of(new TaskDto(null, "testTitle1", "testDescription1", LocalDateTime.now(), TaskStatus.IN_PROGRESS),
                new TaskDto(null, "testTitle2", "testDescription2", LocalDateTime.now(), TaskStatus.IN_PROGRESS));
        var ids = new ArrayList<String>();
        for (var taskDto : tasks) {
            var response = client.post()
                    .uri("/tasks")
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(taskDto), TaskDto.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TaskDto.class)
                    .returnResult();
            var createdTask = response.getResponseBody();
            Assertions.assertNotNull(createdTask);
            ids.add(createdTask.getId());
        }

        client.get()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TaskDto.class).hasSize(ids.size());
    }

    @Test
    void getTaskByIdTest() {
        var taskDto = new TaskDto(null, "testTitle2", "testDescription2", LocalDateTime.now(), TaskStatus.IN_PROGRESS);

        var createResponse = client.post()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDto.class)
                .returnResult();

        var createdTask = createResponse.getResponseBody();
        Assertions.assertNotNull(createdTask);
        var id = createdTask.getId();

        var result = client.get()
                .uri("/tasks/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TaskDto.class).returnResult();
        Assertions.assertNotNull(result.getResponseBody());
    }

    @Test
    void getTaskByIdTestWithWrongId() {
        var result = client.get()
                .uri("/tasks/xyz")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TaskDto.class).returnResult();
        Assertions.assertNull(result.getResponseBody());
    }

    @Test
    void deleteTest() {
        var taskDto = new TaskDto(null, "testTitle2", "testDescription2", LocalDateTime.now(), TaskStatus.IN_PROGRESS);

        var createResponse = client.post()
                .uri("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskDto), TaskDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDto.class)
                .returnResult();

        var createdTask = createResponse.getResponseBody();
        Assertions.assertNotNull(createdTask);
        var id = createdTask.getId();

        client.delete()
                .uri("/tasks/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteTestWithWrongId() {
        client.delete()
                .uri("/tasks/xyz")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
