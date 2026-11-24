package com.cafepos.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public void enviarCodigoVerificacion(String destinatario, String nombreUsuario, String nombreCafe, String codigo) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Codigo de verificacion - " + nombreCafe);

            String contenidoHtml = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                    <h2 style="color: #4A2C2A; text-align: center;">Bienvenido a %s</h2>
                    <p>Hola <strong>%s</strong>,</p>
                    <p>Usa el siguiente codigo para verificar tu cuenta y activar tu acceso al sistema POS:</p>
                    <div style="background-color: #F5EFE6; padding: 15px; text-align: center; border-radius: 6px; margin: 20px 0;">
                        <span style="font-size: 28px; font-weight: bold; letter-spacing: 6px; color: #3E2723;">%s</span>
                    </div>
                    <p style="font-size: 12px; color: #757575;">Este codigo expira en 15 minutos. Si no solicitaste esta cuenta, ignora este mensaje.</p>
                </div>
                """.formatted(nombreCafe, nombreUsuario, codigo);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);
            log.info("Correo de verificacion enviado exitosamente a {}", destinatario);

        } catch (MessagingException e) {
            log.error("Error al enviar el correo a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de verificacion. Verifica la configuracion SMTP.");
        }
    }
}