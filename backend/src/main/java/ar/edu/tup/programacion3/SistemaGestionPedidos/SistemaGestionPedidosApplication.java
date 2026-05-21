package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.user.UserDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.OrderStatus;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.PaymentMethod;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class SistemaGestionPedidosApplication {

    public static void main(String[] args) {

        SpringApplication.run(SistemaGestionPedidosApplication.class, args);
    }

    @Bean
    public CommandLineRunner instantiateFromDto(
            UserService userService,
            CategoryService categoryService,
            ProductService productService,
            OrderService orderService) {

        return args -> {
            System.out.println("--- INICIANDO INSTANCIACIÓN DE DATOS REQUERIDOS ---");

            // ==========================================
            // a) Instanciar 2 Usuarios
            // ==========================================
            UserDto u1 =
                    userService.save(
                            new UserCreate(
                                    "Juan", "Perez", "juan@gmail.com", "2644111222", "Pass123!"));

            UserDto u2 =
                    userService.save(
                            new UserCreate(
                                    "Maria",
                                    "Gomez",
                                    "maria@gmail.com",
                                    "2644333444",
                                    "Secure456*"));

            // ==========================================
            // c) Instanciar 3 Categorías
            // ==========================================
            CategoryDto catComida =
                    categoryService.save(
                            new CategoryCreate("Comida Rápida", "Hamburguesas y lomos"));

            CategoryDto catBebidas =
                    categoryService.save(new CategoryCreate("Bebidas", "Gaseosas y aguas"));

            CategoryDto catPostres =
                    categoryService.save(new CategoryCreate("Postres", "Helados y tortas"));

            // ==========================================
            // d) Instanciar 10 Productos y asignarles categorías
            // ==========================================
            // Comidas
            ProductDto p1 =
                    productService.save(
                            new ProductCreate(
                                    "Hamburguesa Simple",
                                    new BigDecimal("1500.00"),
                                    "Mediana",
                                    20,
                                    "img_h1.png",
                                    true,
                                    catComida.id()));

            ProductDto p2 =
                    productService.save(
                            new ProductCreate(
                                    "Hamburguesa Doble",
                                    new BigDecimal("2200.00"),
                                    "Con queso",
                                    15,
                                    "img_h2.png",
                                    true,
                                    catComida.id()));

            ProductDto p3 =
                    productService.save(
                            new ProductCreate(
                                    "Lomo Completo",
                                    new BigDecimal("3000.00"),
                                    "Para compartir",
                                    10,
                                    "img_l1.png",
                                    true,
                                    catComida.id()));

            ProductDto p4 =
                    productService.save(
                            new ProductCreate(
                                    "Papas Fritas",
                                    new BigDecimal("800.00"),
                                    "Porción grande",
                                    50,
                                    "img_papas.png",
                                    true,
                                    catComida.id()));

            // Bebidas
            ProductDto p5 =
                    productService.save(
                            new ProductCreate(
                                    "Coca Cola 500ml",
                                    new BigDecimal("600.00"),
                                    "Común",
                                    100,
                                    "coca.png",
                                    true,
                                    catBebidas.id()));

            ProductDto p6 =
                    productService.save(
                            new ProductCreate(
                                    "Agua Mineral",
                                    new BigDecimal("500.00"),
                                    "Sin gas",
                                    80,
                                    "agua.png",
                                    true,
                                    catBebidas.id()));

            ProductDto p7 =
                    productService.save(
                            new ProductCreate(
                                    "Cerveza Quilmes",
                                    new BigDecimal("1200.00"),
                                    "Lata",
                                    40,
                                    "quilmes.png",
                                    true,
                                    catBebidas.id()));

            // Postres
            ProductDto p8 =
                    productService.save(
                            new ProductCreate(
                                    "Flan con Dulce",
                                    new BigDecimal("700.00"),
                                    "Casero",
                                    12,
                                    "flan.png",
                                    true,
                                    catPostres.id()));

            ProductDto p9 =
                    productService.save(
                            new ProductCreate(
                                    "Helado 1/4kg",
                                    new BigDecimal("1100.00"),
                                    "Dos gustos",
                                    25,
                                    "helado.png",
                                    true,
                                    catPostres.id()));

            ProductDto p10 =
                    productService.save(
                            new ProductCreate(
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
            OrderDto ped1 =
                    orderService.save(
                            new OrderCreate(
                                    LocalDate.now(),
                                    OrderStatus.PENDING,
                                    BigDecimal.ZERO,
                                    PaymentMethod.CASH,
                                    u1.id()));
            orderService.addProductToOrder(ped1.id(), 2, p2.id());
            orderService.addProductToOrder(ped1.id(), 2, p5.id());

            OrderDto ped2 =
                    orderService.save(
                            new OrderCreate(
                                    LocalDate.now(),
                                    OrderStatus.PENDING,
                                    BigDecimal.ZERO,
                                    PaymentMethod.CARD,
                                    u1.id()));
            orderService.addProductToOrder(ped2.id(), 1, p3.id());
            orderService.addProductToOrder(ped2.id(), 1, p4.id());
            orderService.addProductToOrder(ped2.id(), 2, p7.id());

            OrderDto ped3 =
                    orderService.save(
                            new OrderCreate(
                                    LocalDate.now(),
                                    OrderStatus.COMPLETED,
                                    BigDecimal.ZERO,
                                    PaymentMethod.TRANSFER,
                                    u2.id()));
            orderService.addProductToOrder(ped3.id(), 1, p1.id());
            orderService.addProductToOrder(ped3.id(), 1, p6.id());
            orderService.addProductToOrder(ped3.id(), 1, p8.id());

            System.out.println("--- PERSISTENCIA COMPLETADA DE FORMA EXITOSA ---");
        };
    }
}
