package com.example.task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TaskApiIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("tasktest")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void applicationContextLoads() {
        // If Spring starts successfully, this test passes.
    }

    @Test
    void createTaskThenRetrieveIt() throws Exception {
        // 1. Prepare a valid request body
        String requestBody = """
            {
                "title": "Integration test task",
                "description": "Created using MockMvc and PostgreSQL Testcontainers",
                "priority": "HIGH"
            }
            """;

        // 2. Create the task through the REST API
        String createResponse = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.description")
                        .value("Created using MockMvc and PostgreSQL Testcontainers"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 3. Extract the generated UUID from the response
        JsonNode createdTask = objectMapper.readTree(createResponse);
        String id = createdTask.path("id").asText();

        assertFalse(id.isBlank(), "The created task should have an ID");
        assertNotNull(UUID.fromString(id), "The ID should be a valid UUID");

        // 4. Retrieve the task using its generated UUID
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.description")
                        .value("Created using MockMvc and PostgreSQL Testcontainers"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void createTaskWithBlankTitleReturnsBadRequest() throws Exception {
        String requestBody = """
            {
                "title": "   ",
                "description": "This request should fail validation",
                "priority": "HIGH"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getNonexistentTaskReturnsNotFound() throws Exception {
        UUID nonexistentId =
                UUID.fromString("00000000-0000-0000-0000-000000000999");

        mockMvc.perform(get("/api/tasks/{id}", nonexistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path")
                        .value("/api/tasks/" + nonexistentId));
    }

    }

