package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.usuario.UsuarioDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.Estado;
import ar.edu.tup.programacion3.SistemaGestionPedidos.enums.FormaPago;
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
            UsuarioService usuarioService,
            CategoriaService categoriaService,
            ProductoService productoService,
            PedidoService pedidoService) {

        return args -> {
            System.out.println("--- INICIANDO INSTANCIACIÓN DE DATOS REQUERIDOS ---");

            // ==========================================
            // a) Instanciar 2 Usuarios
            // ==========================================
            UsuarioDto u1 =
                    usuarioService.save(
                            new UsuarioCreate(
                                    "Juan", "Perez", "juan@gmail.com", "2644111222", "Pass123!"));

            UsuarioDto u2 =
                    usuarioService.save(
                            new UsuarioCreate(
                                    "Maria",
                                    "Gomez",
                                    "maria@gmail.com",
                                    "2644333444",
                                    "Secure456*"));

            // ==========================================
            // c) Instanciar 3 Categorías
            // ==========================================
            CategoriaDto catComida =
                    categoriaService.save(
                            new CategoriaCreate("Comida Rápida", "Hamburguesas y lomos"));

            CategoriaDto catBebidas =
                    categoriaService.save(new CategoriaCreate("Bebidas", "Gaseosas y aguas"));

            CategoriaDto catPostres =
                    categoriaService.save(new CategoriaCreate("Postres", "Helados y tortas"));

            // ==========================================
            // d) Instanciar 10 Productos y asignarles categorías
            // ==========================================
            // Comidas
            ProductoDto p1 =
                    productoService.save(
                            new ProductoCreate(
                                    "Hamburguesa Simple",
                                    new BigDecimal("1500.00"),
                                    "Mediana",
                                    20,
                                    "img_h1.png",
                                    true,
                                    catComida.id()));

            ProductoDto p2 =
                    productoService.save(
                            new ProductoCreate(
                                    "Hamburguesa Doble",
                                    new BigDecimal("2200.00"),
                                    "Con queso",
                                    15,
                                    "img_h2.png",
                                    true,
                                    catComida.id()));

            ProductoDto p3 =
                    productoService.save(
                            new ProductoCreate(
                                    "Lomo Completo",
                                    new BigDecimal("3000.00"),
                                    "Para compartir",
                                    10,
                                    "img_l1.png",
                                    true,
                                    catComida.id()));

            ProductoDto p4 =
                    productoService.save(
                            new ProductoCreate(
                                    "Papas Fritas",
                                    new BigDecimal("800.00"),
                                    "Porción grande",
                                    50,
                                    "img_papas.png",
                                    true,
                                    catComida.id()));

            // Bebidas
            ProductoDto p5 =
                    productoService.save(
                            new ProductoCreate(
                                    "Coca Cola 500ml",
                                    new BigDecimal("600.00"),
                                    "Común",
                                    100,
                                    "coca.png",
                                    true,
                                    catBebidas.id()));

            ProductoDto p6 =
                    productoService.save(
                            new ProductoCreate(
                                    "Agua Mineral",
                                    new BigDecimal("500.00"),
                                    "Sin gas",
                                    80,
                                    "agua.png",
                                    true,
                                    catBebidas.id()));

            ProductoDto p7 =
                    productoService.save(
                            new ProductoCreate(
                                    "Cerveza Quilmes",
                                    new BigDecimal("1200.00"),
                                    "Lata",
                                    40,
                                    "quilmes.png",
                                    true,
                                    catBebidas.id()));

            // Postres
            ProductoDto p8 =
                    productoService.save(
                            new ProductoCreate(
                                    "Flan con Dulce",
                                    new BigDecimal("700.00"),
                                    "Casero",
                                    12,
                                    "flan.png",
                                    true,
                                    catPostres.id()));

            ProductoDto p9 =
                    productoService.save(
                            new ProductoCreate(
                                    "Helado 1/4kg",
                                    new BigDecimal("1100.00"),
                                    "Dos gustos",
                                    25,
                                    "helado.png",
                                    true,
                                    catPostres.id()));

            ProductoDto p10 =
                    productoService.save(
                            new ProductoCreate(
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
            PedidoDto ped1 =
                    pedidoService.save(
                            new PedidoCreate(
                                    LocalDate.now(),
                                    Estado.PENDIENTE,
                                    BigDecimal.ZERO,
                                    FormaPago.EFECTIVO,
                                    u1.id()));
            pedidoService.addProductToOrder(ped1.id(), 2, p2.id());
            pedidoService.addProductToOrder(ped1.id(), 2, p5.id());

            PedidoDto ped2 =
                    pedidoService.save(
                            new PedidoCreate(
                                    LocalDate.now(),
                                    Estado.PENDIENTE,
                                    BigDecimal.ZERO,
                                    FormaPago.TARJETA,
                                    u1.id()));
            pedidoService.addProductToOrder(ped2.id(), 1, p3.id());
            pedidoService.addProductToOrder(ped2.id(), 1, p4.id());
            pedidoService.addProductToOrder(ped2.id(), 2, p7.id());

            PedidoDto ped3 =
                    pedidoService.save(
                            new PedidoCreate(
                                    LocalDate.now(),
                                    Estado.TERMINADO,
                                    BigDecimal.ZERO,
                                    FormaPago.TRANSFERENCIA,
                                    u2.id()));
            pedidoService.addProductToOrder(ped3.id(), 1, p1.id());
            pedidoService.addProductToOrder(ped3.id(), 1, p6.id());
            pedidoService.addProductToOrder(ped3.id(), 1, p8.id());

            System.out.println("--- PERSISTENCIA COMPLETADA DE FORMA EXITOSA ---");
        };
    }
}
