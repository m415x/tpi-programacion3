package ar.edu.tup.programacion3.SGP.infrastructure;

import ar.edu.tup.programacion3.SGP.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SGP.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SGP.model.enums.UserRole;
import ar.edu.tup.programacion3.SGP.repository.UserRepository;
import ar.edu.tup.programacion3.SGP.service.CategoryService;
import ar.edu.tup.programacion3.SGP.service.ProductService;
import ar.edu.tup.programacion3.SGP.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoad implements CommandLineRunner {

	private final UserService userService;
	private final UserRepository userRepository;
	private final CategoryService categoryService;
	private final ProductService productService;

	@Override
	public void run(String @NonNull ... args) throws Exception {

		// Verificar count() de usuarios para asegurar idempotencia
		if (userRepository.count() == 0) {

			log.info("--- BASE DE DATOS VACÍA: INICIANDO EJECUCIÓN DE CARGA INICIAL ---");

			// ===============================================================================================
			// a) Instanciar Usuario Administrador por Defecto (Garantiza Escenario 1)
			// ===============================================================================================
			UserResponseDTO userAdminDto = userService.save(
					new UserRequestDTO(
							"Cristian",
							"Lahoz",
							"admin@admin.com",
							"2644111222",
							"123456"
					)
			);

			// Mutamos el rol a ADMIN directo mediante el repositorio
			userRepository.findById(userAdminDto.id()).ifPresent(user -> {
				user.setUserRole(UserRole.ADMIN);
				userRepository.save(user);
			});

			log.info(" Usuario Administrador por defecto creado exitosamente [Email: admin@admin.com]");

			// ===============================================================================================
			// b) Tu semilla expandida de negocio (Categorías y Productos) para el Frontend de la Tienda
			// ===============================================================================================
			CategoryResponseDTO catComida = categoryService.save(new CategoryRequestDTO("Comida Rápida", "Hamburguesas y lomos", "hamburguesa1.jpg"));
			CategoryResponseDTO catBebidas = categoryService.save(new CategoryRequestDTO("Bebidas", "Gaseosas y aguas", "coca.jpg"));
			CategoryResponseDTO catPostres = categoryService.save(new CategoryRequestDTO("Postres", "Helados y tortas", "flan.jpg"));

			productService.save(new ProductRequestDTO("Hamburguesa Simple", new BigDecimal("1500.00"), "Mediana", 20, "hamburguesa1.jpg", true, catComida.id()));
			productService.save(new ProductRequestDTO("Hamburguesa Doble", new BigDecimal("2200.00"), "Con queso", 15, "hamburguesa2.jpg", true, catComida.id()));
			productService.save(new ProductRequestDTO("Lomo Completo", new BigDecimal("3000.00"), "Para compartir", 10, "lomo.jpg", true, catComida.id()));
			productService.save(new ProductRequestDTO("Papas Fritas", new BigDecimal("800.00"), "Porción grande", 50, "papa.jpg", true, catComida.id()));
			productService.save(new ProductRequestDTO("Coca Cola 500ml", new BigDecimal("600.00"), "Común", 100, "coca.jpg", true, catBebidas.id()));
			productService.save(new ProductRequestDTO("Agua Mineral", new BigDecimal("500.00"), "Sin gas", 80, "agua.jpg", true, catBebidas.id()));
			productService.save(new ProductRequestDTO("Cerveza Quilmes", new BigDecimal("1200.00"), "Lata", 40, "cerveza.jpg", true, catBebidas.id()));
			productService.save(new ProductRequestDTO("Flan con Dulce", new BigDecimal("700.00"), "Casero", 12, "flan.jpg", true, catPostres.id()));
			productService.save(new ProductRequestDTO("Helado 1/4kg", new BigDecimal("1100.00"), "Dos gustos", 25, "helado.jpg", true, catPostres.id()));
			productService.save(new ProductRequestDTO("Ensalada de Frutas", new BigDecimal("650.00"), "Estación", 8, "frutas.jpg", true, catPostres.id()));

			log.info("--- SIEMBRA DE CATEGORÍAS Y PRODUCTOS ASOCIADOS COMPLETADA CON ÉXITO ---");

		} else {
			log.info("--- CONFIGURACIÓN INICIAL COMPLETA: Se omitió la siembra (Usuarios preexistentes en DB) ---");
		}
	}
}