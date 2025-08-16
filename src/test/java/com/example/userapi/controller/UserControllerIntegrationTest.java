package com.example.userapi.controller;

import com.example.userapi.dtos.PhoneDTO;
import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserRequestDTO userRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        PhoneDTO phone = new PhoneDTO();
        phone.setNumber("12345678");
        phone.setCitycode("1");
        phone.setContrycode("57");

        userRequest = new UserRequestDTO();
        userRequest.setName("Integration Test User");
        userRequest.setEmail("integration@test.com");
        userRequest.setPassword("validpass123");
        userRequest.setPhones(List.of(phone));
    }

    @Test
    void debeRegistrarUsuarioYDevolver201Created() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test User"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                // CORRECCIÓN: Buscar el campo "isactive" en minúsculas
                .andExpect(jsonPath("$.isactive").value(true));
    }

    @Test
    void debeDevolver400BadRequestSiElEmailEsInvalido() throws Exception {
        userRequest.setEmail("email-invalido");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                // CORRECCIÓN: Esperar el mensaje de error en español.
                .andExpect(jsonPath("$.error").value("Validación Fallida"));
    }

    @Test
    void debeDevolver409ConflictSiElEmailYaExiste() throws Exception {
        // Primero, registramos un usuario para que el email ya exista
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)));

        // Segundo, intentamos registrarlo de nuevo
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict())
                // CORRECCIÓN: Esperar el mensaje de error en español.
                .andExpect(jsonPath("$.error").value("Conflicto"));
    }
}