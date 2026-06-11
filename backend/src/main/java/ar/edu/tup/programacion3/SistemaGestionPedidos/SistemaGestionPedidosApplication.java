package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.UserResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.enums.PaymentMethod;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
            UserService userService,
            CategoryService categoryService,
            ProductService productService,
            OrderService orderService) {

        return args -> {

	        // =======================================================================================================
	        // CONTROL DE IDEMPOTENCIA Y PROTECCIÓN DE DATOS (EVALUACIÓN PARCIAL 2)
	        // =======================================================================================================
	        // Se valida si la base de datos ya contiene registros activos antes de ejecutar la siembra (seeding).
	        // Esto garantiza la idempotencia del arranque: si la DB es persistente (file-based), evita excepciones
	        // por violación de restricciones únicas (como emails duplicados) en arranques sucesivos, manteniendo
	        // un estado consistente tanto para el testing de la consola como para el desarrollo del TPI.
	        // =======================================================================================================
            if (categoryService.findAll().isEmpty()) {

                System.out.println(
                        "--- DB VACÍA: INICIANDO INSTANCIACIÓN DE DATOS REQUERIDOS SEMILLA ---");

                // ==========================================
                // a) Instanciar 2 Usuarios
                // ==========================================
                UserResponseDTO u1 =
                        userService.save(
                                new UserRequestDTO(
                                        "Juan José",
                                        "Perez",
                                        "juan@gmail.com",
                                        "2644111222",
                                        "Passwd123!"));

                UserResponseDTO u2 =
                        userService.save(
                                new UserRequestDTO(
                                        "Maria",
                                        "Gomez",
                                        "maria@gmail.com",
                                        "2644333444",
                                        "Secure456*"));

                // ==========================================
                // c) Instanciar 3 Categorías
                // ==========================================
                CategoryResponseDTO catComida =
                        categoryService.save(
                                new CategoryRequestDTO("Comida Rápida", "Hamburguesas y lomos"));

                CategoryResponseDTO catBebidas =
                        categoryService.save(new CategoryRequestDTO("Bebidas", "Gaseosas y aguas"));

                CategoryResponseDTO catPostres =
                        categoryService.save(new CategoryRequestDTO("Postres", "Helados y tortas"));

                // ==========================================
                // d) Instanciar 10 Productos y asignarles categorías
                // ==========================================
                // Comidas
                ProductResponseDTO p1 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Hamburguesa Simple",
                                        new BigDecimal("1500.00"),
                                        "Mediana",
                                        20,
                                        "img_h1.png",
                                        true,
                                        catComida.id()));

                ProductResponseDTO p2 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Hamburguesa Doble",
                                        new BigDecimal("2200.00"),
                                        "Con queso",
                                        15,
                                        "img_h2.png",
                                        true,
                                        catComida.id()));

                ProductResponseDTO p3 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Lomo Completo",
                                        new BigDecimal("3000.00"),
                                        "Para compartir",
                                        10,
                                        "img_l1.png",
                                        true,
                                        catComida.id()));

                ProductResponseDTO p4 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Papas Fritas",
                                        new BigDecimal("800.00"),
                                        "Porción grande",
                                        50,
                                        "img_papas.png",
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
                                        "coca.png",
                                        true,
                                        catBebidas.id()));

                ProductResponseDTO p6 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Agua Mineral",
                                        new BigDecimal("500.00"),
                                        "Sin gas",
                                        80,
                                        "agua.png",
                                        true,
                                        catBebidas.id()));

                ProductResponseDTO p7 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Cerveza Quilmes",
                                        new BigDecimal("1200.00"),
                                        "Lata",
                                        40,
                                        "quilmes.png",
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
                                        "flan.png",
                                        true,
                                        catPostres.id()));

                ProductResponseDTO p9 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Helado 1/4kg",
                                        new BigDecimal("1100.00"),
                                        "Dos gustos",
                                        25,
                                        "helado.png",
                                        true,
                                        catPostres.id()));

                ProductResponseDTO p10 =
                        productService.save(
                                new ProductRequestDTO(
                                        "Ensalada de Frutas",
                                        new BigDecimal("650.00"),
                                        "Estación",
                                        8,
                                        "frutas.png",
                                        true,
                                        catPostres.id()));

                // ==========================================
                // b) Instanciar 3 Pedidos (con composición de al menos 2 detalles por cada uno)
                // ==========================================
                OrderResponseDTO ped1 =
                        orderService.save(
                                new OrderRequestDTO(
                                        LocalDate.now(),
                                        OrderStatus.PENDING,
                                        BigDecimal.ZERO,
                                        PaymentMethod.CASH,
                                        u1.id()));
                orderService.addProductToOrder(ped1.id(), 2, p2.id());
                orderService.addProductToOrder(ped1.id(), 2, p5.id());

                OrderResponseDTO ped2 =
                        orderService.save(
                                new OrderRequestDTO(
                                        LocalDate.now(),
                                        OrderStatus.PENDING,
                                        BigDecimal.ZERO,
                                        PaymentMethod.CARD,
                                        u1.id()));
                orderService.addProductToOrder(ped2.id(), 1, p3.id());
                orderService.addProductToOrder(ped2.id(), 1, p4.id());
                orderService.addProductToOrder(ped2.id(), 2, p7.id());

                OrderResponseDTO ped3 =
                        orderService.save(
                                new OrderRequestDTO(
                                        LocalDate.now(),
                                        OrderStatus.COMPLETED,
                                        BigDecimal.ZERO,
                                        PaymentMethod.TRANSFER,
                                        u2.id()));
                orderService.addProductToOrder(ped3.id(), 1, p1.id());
                orderService.addProductToOrder(ped3.id(), 1, p6.id());
                orderService.addProductToOrder(ped3.id(), 1, p8.id());

                System.out.println("--- PERSISTENCIA SEMILLA COMPLETADA CON ÉXITO ---");
            } else {
                System.out.println(
                        "--- DB DETECTADA CON DATOS: Se omite la siembra para evitar duplicados ---");
            }
        };
    }
}
