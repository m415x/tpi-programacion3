package ar.edu.tup.programacion3.SGP.infrastructure;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder {

	// Usamos el codificador estándar de BCrypt recomendado por la industria
	private final PasswordEncoder bcrypt = new BCryptPasswordEncoder();

	/**
	 * Encripta una contraseña en texto plano usando BCrypt.
	 * Generará un hash completamente diferente en cada invocación gracias al salt aleatorio interno.
	 */
	public String encode(String password) {
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("La contraseña a encriptar no puede estar vacía.");
		}
		return bcrypt.encode(password);
	}

	/**
	 * Compara la contraseña en texto plano contra el hash almacenado en la DB.
	 */
	public boolean matches(String rawPassword, String encodedPassword) {
		if (rawPassword == null || encodedPassword == null) {
			return false;
		}
		return bcrypt.matches(rawPassword, encodedPassword);
	}
}