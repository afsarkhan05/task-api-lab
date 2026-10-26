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

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TaskApiIntegrationTest {

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
    }

