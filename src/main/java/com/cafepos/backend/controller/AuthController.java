package com.cafepos.backend.controller;

import com.cafepos.backend.dto.*;
import com.cafepos.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro-cafe")
    public ResponseEntity<AuthResponse> registrarCafe(@Valid @RequestBody RegistroCafeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarCafe(request));
    }

    @PostMapping("/verificar")
    public ResponseEntity<AuthResponse> verificarCuenta(@Valid @RequestBody VerificarCodigoRequest request) {
        return ResponseEntity.ok(authService.verificarCuenta(request));
    }

    @PostMapping("/reenviar-codigo")
    public ResponseEntity<AuthResponse> reenviarCodigo(@RequestParam String email) {
        authService.reenviarCodigo(email);
        return ResponseEntity.ok(AuthResponse.builder()
                .mensaje("Se ha enviado un nuevo codigo de verificacion a tu correo")
                .email(email)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro-barista")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registrarBarista(
            @Valid @RequestBody CrearBaristaRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrarBarista(request, authentication.getName()));
    }
}