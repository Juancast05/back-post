package com.cafepos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String mensaje;
    private String email;
    private String nombre;
    private String rol;
    private Long cafeId;
    private String nombreCafe;
}