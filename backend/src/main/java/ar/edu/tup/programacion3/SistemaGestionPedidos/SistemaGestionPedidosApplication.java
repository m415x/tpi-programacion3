package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.UserRole;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UserRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@SpringBootApplication
public class SistemaGestionPedidosApplication {

    public static void main(String[] args) {

        SpringApplication.run(SistemaGestionPedidosApplication.class, args);
    }

    @Bean
    @Order(1)
    public CommandLineRunner instantiateFromDto(
            CategoryService categoryService,
            ProductService productService,
            UserService userService,
            UserRepository userRepository) {

        return args -> {

            // =======================================================================================================
            // CONTROL DE IDEMPOTENCIA Y PROTECCIÓN DE DATOS INICIALES
            // =======================================================================================================
            // Se valida si la base de datos ya contiene registros activos antes de ejecutar la
            // siembra (seeding).
            // Esto garantiza la idempotencia del arranque: si la DB es persistente (file-based),
            // evita excepciones
            // por violación de restricciones únicas (como emails duplicados) en arranques
            // sucesivos, manteniendo
            // un estado consistente tanto para el testing de la consola como para el desarrollo del
            // TPI.
            // =======================================================================================================
            if (categoryService.findAll().isEmpty()) {

                System.out.println(
                        "--- DB VACÍA: INICIANDO INSTANCIACIÓN DE DATOS REQUERIDOS SEMILLA ---");

                // ==========================================
                // a) Instanciar Usuario Administrador
                // ==========================================
                UserResponseDTO userAdminDto =
                        userService.save(
                                new UserRequestDTO(
                                        "Cristian",
                                        "Lahoz",
                                        "m415xs@gmail.com",
                                        "2644111222",
                                        "391f671183dc6a85c334a22410450a55bf55808ea16880539705f97208312476"));

                // Cambiamos el rol a ADMIN usando el repositorio directamente en la semilla
                userRepository
                        .findById(userAdminDto.id())
                        .ifPresent(
                                user -> {
                                    user.setUserRole(UserRole.ADMIN);
                                    userRepository.save(user);
                                });

                // ==========================================
                // b) Instanciar 3 Categorías
                // ==========================================
                CategoryResponseDTO catComida =
                        categoryService.save(
                                new CategoryRequestDTO("Comida Rápida", "Hamburguesas y lomos"));

                CategoryResponseDTO catBebidas =
                        categoryService.save(new CategoryRequestDTO("Bebidas", "Gaseosas y aguas"));

                CategoryResponseDTO catPostres =
                        categoryService.save(new CategoryRequestDTO("Postres", "Helados y tortas"));

                // ==========================================
                // c) Instanciar 10 Productos y asignarles categorías
                // ==========================================
                // Comidas
                ProductResponseDTO p1 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Hamburguesa Simple",
                                        new BigDecimal("1500.00"),
                                        "Mediana",
                                        20,
                                        "hamburguesa1.jpg",
                                        true,
                                        catComida.id()));

                ProductResponseDTO p2 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Hamburguesa Doble",
                                        new BigDecimal("2200.00"),
                                        "Con queso",
                                        15,
                                        "hamburguesa2.jpg",
                                        true,
                                        catComida.id()));

                ProductResponseDTO p3 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Lomo Completo",
                                        new BigDecimal("3000.00"),
                                        "Para compartir",
                                        10,
                                        "lomo.jpg",
                                        true,
                                        catComida.id()));

                ProductResponseDTO p4 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Papas Fritas",
                                        new BigDecimal("800.00"),
                                        "Porción grande",
                                        50,
                                        "papa.jpg",
                                        true,
                                        catComida.id()));

                // Bebidas
                ProductResponseDTO p5 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Coca Cola 500ml",
                                        new BigDecimal("600.00"),
                                        "Común",
                                        100,
                                        "coca.jpg",
                                        true,
                                        catBebidas.id()));

                ProductResponseDTO p6 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Agua Mineral",
                                        new BigDecimal("500.00"),
                                        "Sin gas",
                                        80,
                                        "agua.jpg",
                                        true,
                                        catBebidas.id()));

                ProductResponseDTO p7 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Cerveza Quilmes",
                                        new BigDecimal("1200.00"),
                                        "Lata",
                                        40,
                                        "cerveza.jpg",
                                        true,
                                        catBebidas.id()));

                // Postres
                ProductResponseDTO p8 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Flan con Dulce",
                                        new BigDecimal("700.00"),
                                        "Casero",
                                        12,
                                        "flan.jpg",
                                        true,
                                        catPostres.id()));

                ProductResponseDTO p9 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Helado 1/4kg",
                                        new BigDecimal("1100.00"),
                                        "Dos gustos",
                                        25,
                                        "helado.jpg",
                                        true,
                                        catPostres.id()));

                ProductResponseDTO p10 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Ensalada de Frutas",
                                        new BigDecimal("650.00"),
                                        "Estación",
                                        8,
                                        "frutas.jpg",
                                        true,
                                        catPostres.id()));

                System.out.println("--- PERSISTENCIA SEMILLA COMPLETADA CON ÉXITO ---");
            } else {
                System.out.println(
                        "--- DB DETECTADA CON DATOS: Se omite la siembra para evitar duplicados ---");
            }
        };
    }
}
