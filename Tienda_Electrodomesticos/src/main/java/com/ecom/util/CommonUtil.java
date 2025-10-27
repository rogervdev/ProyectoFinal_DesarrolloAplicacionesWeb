package com.ecom.util;


import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendMail(String url, String recipientEmail)
            throws UnsupportedEncodingException, MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("sendigperu7@gmail.com", "Soporte ElectroPerú");
            helper.setTo(recipientEmail);
            helper.setSubject("Password Reset"); 

            String content = """
                <p>Hola,</p>
                <p>Solicitaste restablecer tu contraseña.</p>
                <p>Haz clic en el siguiente enlace:</p>
                <p><a href='%s'>Cambiar mi contraseña</a></p>
            """.formatted(url);

            helper.setText(content, true);
            mailSender.send(message);

            System.out.println("✅ Correo enviado correctamente a: " + recipientEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error enviando correo: " + e.getMessage());
        }
    }


    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(), "");
    }

}
