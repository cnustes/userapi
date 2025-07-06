package com.example.userapi.controllers;

import com.example.userapi.dtos.UserRequestDTO;
import com.example.userapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Retorna el usuario registrado y su token")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDTO request)  {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @GetMapping("/secure/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> testJwtAccess(Authentication authentication) {
        return ResponseEntity.ok("Hola " + authentication.getName() +"/ ✅ Acceso permitido con token válido.");
    }
}
