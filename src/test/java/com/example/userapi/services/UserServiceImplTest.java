package com.example.userapi.services;

import com.example.userapi.dtos.PhoneDTO;
import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.dtos.UserResponseDTO;
import com.example.userapi.kafka.KafkaProducerService;
import com.example.userapi.models.User;
import com.example.userapi.repositories.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private KafkaProducerService kafkaProducerService;
    private ObjectMapper objectMapper;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        kafkaProducerService = mock(KafkaProducerService.class);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        userService = new UserServiceImpl(userRepository, jwtUtil, kafkaProducerService, objectMapper);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Camilo Ñustes");
        request.setEmail("camilo@example.com");
        request.setPassword("securePass123");

        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber("321321321");
        phoneDTO.setCitycode("2");
        phoneDTO.setContrycode("57");
        request.setPhones(Collections.singletonList(phoneDTO));

        when(userRepository.findByEmail("camilo@example.com")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken("camilo@example.com")).thenReturn("jwt-mock-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponseDTO savedUser = userService.registerUser(request);

        // Assert
        assertNotNull(savedUser);
        assertEquals("Camilo Ñustes", savedUser.getName());
        assertEquals("camilo@example.com", savedUser.getEmail());
        assertEquals("jwt-mock-token", savedUser.getToken());
        assertTrue(savedUser.isActive());
        assertEquals(1, savedUser.getPhones().size());

        verify(kafkaProducerService).sendUserCreated(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("camilo@example.com");

        when(userRepository.findByEmail("camilo@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(request));
        assertEquals("El correo ya registrado", ex.getMessage());
    }
}
