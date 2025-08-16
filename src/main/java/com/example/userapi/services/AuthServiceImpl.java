package com.example.userapi.services;

import com.example.userapi.dtos.LoginRequestDTO;
import com.example.userapi.dtos.LoginResponseDTO;
import com.example.userapi.models.User;
import com.example.userapi.repositories.UserRepository;
import com.example.userapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("▶️  Intentando autenticar al usuario: {}", request.getEmail());

        // Spring Security se encarga de validar el email y la contraseña (con el password hasheado)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Si la autenticación es exitosa, buscamos al usuario para generar un nuevo token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado tras una autenticación exitosa"));

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("✅  Autenticación exitosa. Nuevo token generado para {}", request.getEmail());

        return LoginResponseDTO.builder().token(token).build();
    }
}