package ar.edu.tup.programacion3.SistemaGestionPedidos.mapper;

import org.mapstruct.Condition;
import org.springframework.stereotype.Component;

@Component
public class StringValidationSupport {

	@Condition
	public boolean isNotEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
