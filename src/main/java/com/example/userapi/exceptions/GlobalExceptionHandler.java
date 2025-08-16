package com.example.userapi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j; // Importar logger
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException; // Importar excepción de credenciales
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j // Inyectar logger
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("⚠️  Conflicto: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflicto", ex.getMessage(), request, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("⚠️  Intento de login fallido: Credenciales incorrectas.");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "No autorizado", "Credenciales inválidas.", request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        log.warn("⚠️  Error de validación en la solicitud: {}", details);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validación Fallida", "Uno o más campos no son válidos", request, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("❌  Error de formato JSON: {}", ex.getMostSpecificCause().getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "JSON Malformado", "El cuerpo de la solicitud no tiene un formato JSON válido.", request, null);
    }

    // Un manejador genérico para cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("❌  Error interno no controlado: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno del Servidor", "Ocurrió un error inesperado.", request, null);
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