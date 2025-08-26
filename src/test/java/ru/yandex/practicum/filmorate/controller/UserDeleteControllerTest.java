package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserDeleteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Sql(scripts = {"/test-schema.sql", "/test-data.sql"})
    public void deleteUserAfterCreateUser() throws Exception {
        String jsonString = "{\n" +
                "\"name\": \"name_common\",\n" +
                "\"login\": \"common\",\n" +
                "\"email\": \"friend@common.ru\",\n" +
                "\"birthday\": \"2000-08-20\"" +
                "}\n";

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(jsonString)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("friend@common.ru"));


        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        mvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }
}
