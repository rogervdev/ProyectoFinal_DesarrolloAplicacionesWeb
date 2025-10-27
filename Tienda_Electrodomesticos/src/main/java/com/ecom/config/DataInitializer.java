package com.ecom.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecom.model.Usuario;
import com.ecom.service.UsuarioService;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner initAdmin(UsuarioService usuarioService, PasswordEncoder encoder) {
		return args -> {
			// Verifica si ya existe un admin
			if (!usuarioService.existsByEmail("admin@correo.com")) {
				Usuario admin = new Usuario();
				admin.setNombre("Admin");
				admin.setApellido("Principal");
				admin.setEmail("admin@correo.com");
				admin.setPassword(encoder.encode("123456")); // ✅ encriptado
				admin.setRol("ROLE_ADMIN");
				admin.setIsEnable(true);
				admin.setProfileImage("default.png");

				// 🔹 Nuevos campos de control de cuenta
				admin.setCuentaNoBloqueada(true); // ✅ La cuenta está activa
				admin.setIntentoFallido(0); // ✅ Cero intentos fallidos
				admin.setLockTime(null); // ✅ No está bloqueada actualmente

				usuarioService.saveUsuario(admin); // guarda en la DB

				System.out.println("Administrador inicial creado: admin@correo.com / 123456");
			}
		};
	}
}
