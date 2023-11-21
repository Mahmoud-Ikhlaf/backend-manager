package com.individueleproject.backendmanager.unittests.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwaggerTest {

    @Autowired
    private MockMvc api;

    @Test
    void anyoneCanAccessSwaggerEndpoint() throws Exception {
        api.perform(get("/swagger-ui/index.html#/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Swagger UI")));
    }

    @Test
    void anyoneCanAccessSwaggerApiDocsEndpoint() throws Exception {
        api.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Mahoot API")));
    }
}
