package com.example.userapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO estándar para devolver mensajes de error simples en la API.
 * Cumple con el formato {"mensaje": "..."}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDTO {
    private String mensaje;
}