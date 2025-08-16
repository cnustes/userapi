package com.example.userapi.services;

import com.example.userapi.dtos.PhoneResponseDTO;
import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.dtos.UserResponseDTO;
import com.example.userapi.exceptions.UserAlreadyExistsException;
import com.example.userapi.kafka.KafkaProducerService;
import com.example.userapi.models.Phone;
import com.example.userapi.models.User;
import com.example.userapi.repositories.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {
        log.info("▶️  Iniciando proceso de registro para el correo: {}", request.getEmail());

       if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("⚠️  Intento de registro con correo duplicado: {}", request.getEmail());
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);
        user.setToken(jwtUtil.generateToken(user.getEmail()));
        user.setActive(true);
        user.setPhones(
                request.getPhones().stream().map(p -> {
                    Phone phone = new Phone();
                    phone.setNumber(p.getNumber());
                    phone.setCitycode(p.getCitycode());
                    phone.setContrycode(p.getContrycode());
                    phone.setUser(user);
                    return phone;
                }).collect(Collectors.toList())
        );

        User savedUser = userRepository.save(user);
        log.info("✅  Usuario guardado exitosamente en la BD con ID: {}", savedUser.getId());

        try {
            String json = objectMapper.writeValueAsString(savedUser);
            kafkaProducerService.sendUserCreated(json);
            log.info("📨  Evento de creación de usuario enviado a Kafka.");
        } catch (JsonProcessingException e) {
            log.error("❌  Error serializando el usuario para enviarlo a Kafka: {}", e.getMessage(), e);
        }

        return mapToResponse(savedUser);
    }

    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setToken(user.getToken());
        // CAMBIO: Asignar directamente los objetos LocalDateTime
        dto.setCreated(user.getCreated());
        dto.setModified(user.getModified());
        dto.setLastLogin(user.getLastLogin());
        dto.setActive(user.isActive());

        List<PhoneResponseDTO> phones = user.getPhones().stream().map(phone -> {
            PhoneResponseDTO p = new PhoneResponseDTO();
            p.setNumber(phone.getNumber());
            p.setCitycode(phone.getCitycode());
            p.setContrycode(phone.getContrycode());
            return p;
        }).toList();

        dto.setPhones(phones);
        return dto;
    }
}