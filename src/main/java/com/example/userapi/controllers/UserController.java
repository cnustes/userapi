package com.example.userapi.controllers;

import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.dtos.UserResponseDTO;
import com.example.userapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Retorna el usuario registrado y su token")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDTO request)  {
        log.info("▶️  Solicitud recibida para registrar al usuario con correo: {}", request.getEmail());
        UserResponseDTO response = userService.registerUser(request);
        log.info("✅  Usuario registrado exitosamente con ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Devolver 201 Created
    }

    @GetMapping("/secure/test")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Endpoint de prueba para JWT", description = "Verifica el acceso con un token válido")
    public ResponseEntity<String> testJwtAccess(Authentication authentication) {
        log.info("▶️  Acceso de prueba solicitado por: {}", authentication.getName());
        return ResponseEntity.ok("Hola " + authentication.getName() + " / ✅ Acceso permitido con token válido.");
    }
}
