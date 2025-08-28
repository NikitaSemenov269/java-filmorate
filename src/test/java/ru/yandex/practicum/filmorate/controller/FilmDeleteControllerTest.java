package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmDeleteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void deleteFilmAfterCreateFilm() throws Exception {
        String jsonString = "{\n" +
                "    \"name\": \"Name\",\n" +
                "    \"description\": \"Descrition\",\n" +
                "    \"releaseDate\": \"1980-03-25\",\n" +
                "    \"duration\": 200,\n" +
                "    \"mpa\": {\n" +
                "        \"id\": 5\n" +
                "    },\n" +
                "    \"genres\": [\n" +
                "        {\n" +
                "            \"id\": 1\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonString)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"));

        mvc.perform(delete("/films/1"))
                .andExpect(status().isOk());

        mvc.perform(get("/films/1"))
                .andExpect(status().isNotFound());
    }
}
