package com.example.userapi.exceptions;

import com.example.userapi.dtos.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleUserExists(UserAlreadyExistsException ex) {
        log.warn("⚠️  Conflicto: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiErrorDTO(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleBadCredentials(BadCredentialsException ex) {
        log.warn("⚠️  Intento de login fallido: Credenciales incorrectas.");
        return new ResponseEntity<>(new ApiErrorDTO("Credenciales inválidas"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
        // Obtenemos el primer error de validación para un mensaje simple y claro.
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");

        log.warn("⚠️  Error de validación en la solicitud: {}", errorMessage);
        return new ResponseEntity<>(new ApiErrorDTO(errorMessage), HttpStatus.BAD_REQUEST);
    }

    // Un manejador genérico para cualquier otra excepción no controlada.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex) {
        log.error("❌  Error interno no controlado: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new ApiErrorDTO("Ocurrió un error inesperado."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Método helper para construir la respuesta de error de forma consistente
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message, HttpServletRequest request, List<String> details) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                request.getRequestURI(),
                details != null ? details : Collections.emptyList()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}