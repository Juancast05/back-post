package com.cafepos.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroCafeRequest {

    @NotBlank(message = "El nombre de la cafeteria es obligatorio")
    private String nombreCafe;

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    private String direccion;
    private String telefono;

    @NotBlank(message = "El nombre del administrador es obligatorio")
    private String nombreAdmin;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo invalido")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;
}