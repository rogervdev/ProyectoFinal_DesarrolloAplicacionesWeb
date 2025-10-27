package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.Usuario;
import com.ecom.repository.UsuarioRepository;
import com.ecom.service.UsuarioService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String email = request.getParameter("username");
		Usuario usuario = usuarioRepository.findByEmail(email);

		if (usuario == null) {
			exception = new BadCredentialsException("Correo o contraseña incorrectos");
			super.setDefaultFailureUrl("/signin?error");
			super.onAuthenticationFailure(request, response, exception);
			return;
		}

		if (!usuario.getIsEnable()) {
			exception = new DisabledException("Tu cuenta está inactiva");
			super.setDefaultFailureUrl("/signin?error");
			super.onAuthenticationFailure(request, response, exception);
			return;
		}

		// Si la cuenta está bloqueada actualmente
		if (!usuario.getCuentaNoBloqueada()) {
			// Si lookTime es null por seguridad, tratamos como bloqueada sin tiempo
			if (usuario.getLockTime() == null) {
				exception = new LockedException("Tu cuenta está bloqueada. Por favor inténtalo más tarde.");
			} else {
				long lockTime = usuario.getLockTime().getTime();
				long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
				long currentTime = System.currentTimeMillis();

				if (unlockTime <= currentTime) {
					// Ya pasó el tiempo -> desbloqueamos
					usuarioService.desbloquearCuentaTiempoExpirado(usuario); // debe restablecer intentos y guardar
					exception = new LockedException(
							"Tu cuenta ha sido desbloqueada. Por favor intenta iniciar sesión nuevamente.");
				} else {
					// Calculamos tiempo restante
					long remainingMillis = unlockTime - currentTime;
					long remainingSeconds = (remainingMillis / 1000) % 60;
					long remainingMinutes = (remainingMillis / (60 * 1000));

					String tiempoRestante;
					if (remainingMinutes > 0) {
						tiempoRestante = String.format("%d min %d s", remainingMinutes, remainingSeconds);
					} else {
						tiempoRestante = String.format("%d s", remainingSeconds);
					}

					String mensaje = "Tu cuenta está bloqueada. Intenta de nuevo en " + tiempoRestante + ".";
					exception = new LockedException(mensaje);
				}
			}

			super.setDefaultFailureUrl("/signin?error");
			super.onAuthenticationFailure(request, response, exception);
			return;
		}

		// Si la cuenta está activa y desbloqueada, tratamos la contraseña incorrecta /
		// conteo
		if (usuario.getIntentoFallido() < AppConstant.ATTEMPT_TIME - 1) {
			usuarioService.incrementaContadorIntentoFallido(usuario);
			exception = new BadCredentialsException("Contraseña incorrecta");
		} else {
			usuarioService.cuentaUsuarioBloqueado(usuario); // debe establecer lookTime = now(), cuentaNoBloqueada =
															// false, guardar
			String mensaje = "Tu cuenta ha sido bloqueada por intentos fallidos. Intenta de nuevo en "
					+ (AppConstant.UNLOCK_DURATION_TIME / (60 * 1000)) + " minutos.";
			exception = new LockedException(mensaje);
		}

		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
