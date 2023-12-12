package com.individueleproject.backendmanager.integration_tests;

import com.individueleproject.backendmanager.BackendManagerApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BackendManagerApplication.class)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties"
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SwaggerTest {

    @Autowired
    private MockMvc api;

    @Test
    public void anyoneCanAccessSwaggerEndpoint() throws Exception {
        api.perform(get("/swagger-ui/index.html#/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Swagger UI")));
    }

    @Test
    public void anyoneCanAccessSwaggerApiDocsEndpoint() throws Exception {
        api.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Mahoot API")));
    }
}
