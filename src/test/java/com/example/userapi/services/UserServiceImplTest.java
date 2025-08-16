package com.example.userapi.services;

import com.example.userapi.dtos.PhoneDTO;
import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.dtos.UserResponseDTO;
import com.example.userapi.exceptions.UserAlreadyExistsException;
import com.example.userapi.kafka.KafkaProducerService;
import com.example.userapi.models.User;
import com.example.userapi.repositories.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Habilita la inyección de mocks de Mockito
public class UserServiceImplTest {

    @Mock // Mockito creará un mock de esta dependencia
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Mockito inyectará los mocks de arriba en esta instancia
    private UserServiceImpl userService;

    private UserRequestDTO userRequest;

    @BeforeEach
    void setUp() {
        // Preparamos un DTO de solicitud estándar para usar en las pruebas
        userRequest = new UserRequestDTO();
        userRequest.setName("Camilo Test");
        userRequest.setEmail("camilo@test.com");
        userRequest.setPassword("ValidPass123");
        PhoneDTO phone = new PhoneDTO();
        phone.setNumber("1234567");
        phone.setCitycode("1");
        phone.setCountrycode("57");
        userRequest.setPhones(Collections.singletonList(phone));
    }

    @Test
    void debeRegistrarUsuarioExitosamente() throws Exception {
        // Arrange
        String mockToken = "mock-jwt-token";
        String hashedPassword = "hashedPassword123";

        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(hashedPassword);
        when(jwtUtil.generateToken(userRequest.getEmail())).thenReturn(mockToken);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(objectMapper.writeValueAsString(any(User.class))).thenReturn("{}");

        // Act
        UserResponseDTO response = userService.registerUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(userRequest.getName(), response.getName());
        assertEquals(mockToken, response.getToken());
        assertTrue(response.isActive());

        // Verificamos que los métodos clave fueron llamados
        verify(passwordEncoder).encode(userRequest.getPassword()); // ¡NUEVA VERIFICACIÓN!
        verify(userRepository).save(any(User.class));
        verify(kafkaProducerService).sendUserCreated(anyString());
    }

    @Test
    void debeLanzarExcepcionSiCorreoYaExiste() {
        // Arrange
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(userRequest);
        });
    }
}