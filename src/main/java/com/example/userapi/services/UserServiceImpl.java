package com.example.userapi.services;

import com.example.userapi.dtos.PhoneDTO;
import com.example.userapi.dtos.PhoneResponseDTO;
import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.dtos.UserResponseDTO;
import com.example.userapi.kafka.KafkaProducerService;
import com.example.userapi.models.Phone;
import com.example.userapi.models.User;
import com.example.userapi.repositories.UserRepository;
import com.example.userapi.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil,
                           KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya registrado");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setToken(jwtUtil.generateToken(user.getEmail()));
        user.setIsActive(true);
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
        try {
            String json = objectMapper.writeValueAsString(user);
            kafkaProducerService.sendUserCreated(json);
        } catch (JsonProcessingException e) {
            logger.error("Error serializando usuario: {}", e.getMessage(), e);
        }
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setToken(user.getToken());
        dto.setCreated(user.getCreated().toString());
        dto.setModified(user.getModified().toString());
        dto.setLastLogin(user.getLastLogin().toString());
        dto.setActive(user.getIsActive());

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