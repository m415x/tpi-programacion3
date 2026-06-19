package ar.edu.tup.programacion3.SGP.validator;

import ar.edu.tup.programacion3.SGP.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityInterceptor implements HandlerInterceptor {

	private final UserService service;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// Si la petición no va hacia un método de un controlador (ej: recursos estáticos), la dejamos pasar
		if (!(handler instanceof HandlerMethod)) {

			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;

		// Verificar si el método del controlador requiere validación de Administrador
		if (handlerMethod.hasMethodAnnotation(AdminRequired.class)) {

			String userIdStr = request.getHeader("X-User-Id");
			String userEmail = request.getHeader("X-User-Email");

			if (userIdStr == null || userEmail == null || userIdStr.isEmpty() || userEmail.isEmpty()) {

				// Si faltan los encabezados de seguridad, disparamos la excepción controlada
				throw new UnsupportedOperationException("Acceso denegado: Credenciales perimetrales ausentes.");
			}

			try {
				UUID userId = UUID.fromString(userIdStr);

				// Validamos en el servicio contra la DB
				service.validateAdminCredentialsOrThrow(userId, userEmail);

			} catch (IllegalArgumentException e) {
				throw new UnsupportedOperationException("Acceso denegado: Formato de identificador UUID inválido.");
			}
		}

		// Si pasa todas las pruebas, retorna true y la petición continúa hacia el controlador
		return true;
	}
}