package com.cafepos.backend.service;

import com.cafepos.backend.dto.*;
import com.cafepos.backend.entity.Cafe;
import com.cafepos.backend.entity.Rol;
import com.cafepos.backend.entity.Usuario;
import com.cafepos.backend.repository.CafeRepository;
import com.cafepos.backend.repository.UsuarioRepository;
import com.cafepos.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CafeRepository cafeRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthResponse registrarCafe(RegistroCafeRequest request) {
        if (cafeRepository.existsByNit(request.getNit())) {
            throw new RuntimeException("Ya existe una cafeteria registrada con ese NIT");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya se encuentra registrado");
        }

        Cafe cafe = Cafe.builder()
                .nombre(request.getNombreCafe())
                .nit(request.getNit())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .activo(true)
                .build();
        cafe = cafeRepository.save(cafe);

        String codigoOtp = generarCodigoOtp();

        Usuario admin = Usuario.builder()
                .nombre(request.getNombreAdmin())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.ADMIN)
                .activo(true)
                .verificado(false)
                .codigoVerificacion(codigoOtp)
                .codigoExpiracion(LocalDateTime.now().plusMinutes(15))
                .cafe(cafe)
                .build();
        usuarioRepository.save(admin);

        emailService.enviarCodigoVerificacion(admin.getEmail(), admin.getNombre(), cafe.getNombre(), codigoOtp);

        return AuthResponse.builder()
                .mensaje("Cafeteria registrada. Revisa tu correo e ingresa el codigo de 6 digitos para activar tu cuenta.")
                .email(admin.getEmail())
                .nombre(admin.getNombre())
                .rol(admin.getRol().name())
                .cafeId(cafe.getId())
                .nombreCafe(cafe.getNombre())
                .build();
    }

    @Transactional
    public AuthResponse registrarBarista(CrearBaristaRequest request, String emailAdminAutenticado) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo del barista ya se encuentra registrado");
        }

        Usuario admin = usuarioRepository.findByEmail(emailAdminAutenticado)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        String codigoOtp = generarCodigoOtp();

        Usuario barista = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.BARISTA)
                .activo(true)
                .verificado(false)
                .codigoVerificacion(codigoOtp)
                .codigoExpiracion(LocalDateTime.now().plusMinutes(15))
                .cafe(admin.getCafe())
                .build();
        usuarioRepository.save(barista);

        emailService.enviarCodigoVerificacion(
                barista.getEmail(),
                barista.getNombre(),
                admin.getCafe().getNombre(),
                codigoOtp
        );

        return AuthResponse.builder()
                .mensaje("Barista creado. Se envio un codigo de verificacion a " + barista.getEmail())
                .email(barista.getEmail())
                .nombre(barista.getNombre())
                .rol(barista.getRol().name())
                .cafeId(admin.getCafe().getId())
                .nombreCafe(admin.getCafe().getNombre())
                .build();
    }

    @Transactional
    public AuthResponse verificarCuenta(VerificarCodigoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getVerificado()) {
            throw new RuntimeException("Esta cuenta ya se encuentra verificada");
        }
        if (usuario.getCodigoVerificacion() == null || !usuario.getCodigoVerificacion().equals(request.getCodigo())) {
            throw new RuntimeException("El codigo de verificacion es incorrecto");
        }
        if (usuario.getCodigoExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El codigo ha expirado. Solicita uno nuevo.");
        }

        usuario.setVerificado(true);
        usuario.setCodigoVerificacion(null);
        usuario.setCodigoExpiracion(null);
        usuarioRepository.save(usuario);

        String jwtToken = jwtService.generarToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .mensaje("Cuenta verificada exitosamente")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .cafeId(usuario.getCafe().getId())
                .nombreCafe(usuario.getCafe().getNombre())
                .build();
    }

    @Transactional
    public void reenviarCodigo(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getVerificado()) {
            throw new RuntimeException("Esta cuenta ya esta verificada");
        }

        String nuevoCodigo = generarCodigoOtp();
        usuario.setCodigoVerificacion(nuevoCodigo);
        usuario.setCodigoExpiracion(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        emailService.enviarCodigoVerificacion(
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getCafe().getNombre(),
                nuevoCodigo
        );
    }
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwtToken = jwtService.generarToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .mensaje("Inicio de sesion exitoso")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .cafeId(usuario.getCafe().getId())
                .nombreCafe(usuario.getCafe().getNombre())
                .build();
    }

    private String generarCodigoOtp() {
        SecureRandom random = new SecureRandom();
        int numero = 100000 + random.nextInt(900000);
        return String.valueOf(numero);
    }
}