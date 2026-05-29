package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.CategoryService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

@Component
@Order(2) // <-- PRIORIDAD 2: Se ejecuta SEGUNDO, una vez sembrada la DB
public class ConsoleMenuRunner implements CommandLineRunner {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ConsoleMenuRunner(
            CategoryService categoryService,
            ProductService productService,
            ProductRepository productRepository) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    // =======================================================================================================
    // CICLO DE VIDA DE LA APLICACIÓN (MÓDULO DE PRUEBAS DEL PARCIAL 2)
    // =======================================================================================================
    // @NonNull: Garantiza la seguridad de tipos (Type Safety) según los estándares de Spring Boot 4
    // y Java 21, evitando alertas de análisis estático por posibles punteros nulos en los argumentos
    // de inicio.
    // =======================================================================================================
    @Override
    public void run(String @NonNull ... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int opcionMain = -1;

        System.out.println("\n=== SISTEMA INICIADO EN MÓDULO DE EXAMEN PARCIAL ===");

        while (opcionMain != 0) {
            System.out.println("\n-------------------------------------------");
            System.out.println("            MENÚ PRINCIPAL (PARCIAL 2)      ");
            System.out.println("-------------------------------------------");
            System.out.println("1. ABM de Categorías");
            System.out.println("2. ABM de Productos");
            System.out.println("3. Consultar Productos por Categoría (JPQL)");
            System.out.println("0. Salir de la aplicación");
            System.out.print("Seleccione una opción: ");

            try {
                opcionMain = Integer.parseInt(scanner.nextLine());
                switch (opcionMain) {
                    case 1 -> menuCategorias(scanner);
                    case 2 -> menuProductos(scanner);
                    case 3 -> consultaJpqlProductosPorCategoria(scanner);
                    case 0 -> {
                            System.out.println(
                                    "Cerrando el sistema de pruebas del parcial. ¡Hasta luego!");
							exit(0);
                    }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingrese un número válido.");
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // ABM DE CATEGORÍAS (Sección 6.1 y 6.2 del Parcial)
    // =========================================================================
    private void menuCategorias(Scanner scanner) {
        System.out.println("\n--- ABM DE CATEGORÍAS ---");
        System.out.println("1. Dar de Alta Categoría");
        System.out.println("2. Dar de Baja Categoría (Soft Delete)");
        System.out.println("3. Listar Categorías Activas");
        System.out.print("Seleccione una opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());

        switch (opcion) {
            case 1 -> {
                System.out.print("Ingrese nombre de la categoría: ");
                String name = scanner.nextLine();
                System.out.print("Ingrese descripción: ");
                String description = scanner.nextLine();

                CategoryDto saved = categoryService.save(new CategoryCreate(name, description));
                System.out.println("¡Categoría guardada con éxito! ID asignado: " + saved.id());
            }
            case 2 -> {
                System.out.print("Ingrese el ID de la categoría a dar de baja: ");
                Long id = Long.parseLong(scanner.nextLine());
                try {
                    categoryService.deleteById(id);
                    System.out.println(
                            "¡Baja lógica realizada con éxito para la categoría ID: " + id);
                } catch (EntityNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.println("\n--- LISTADO DE CATEGORÍAS ACTIVAS ---");
                List<CategoryDto> list = categoryService.findAll();
                if (list.isEmpty()) {
                    System.out.println("No hay categorías activas registradas.");
                } else {
                    list.forEach(
                            c ->
                                    System.out.printf(
                                            "ID: %d | Nombre: %s | Descripción: %s\n",
                                            c.id(), c.name(), c.description()));
                }
            }
            default -> System.out.println("Opción cancelada o inválida.");
        }
    }

    // =========================================================================
    // ABM DE PRODUCTOS (Sección 6.3 del Parcial)
    // =========================================================================
    private void menuProductos(Scanner scanner) {
        System.out.println("\n--- ABM DE PRODUCTOS ---");
        System.out.println("1. Alta de Producto");
        System.out.println("2. Baja de Producto (Soft Delete)");
        System.out.println("3. Listar Productos Activos");
        System.out.print("Seleccione una opción: ");
        int opcion = Integer.parseInt(scanner.nextLine());

        switch (opcion) {
            case 1 -> {
                System.out.print("Ingrese nombre del producto: ");
                String name = scanner.nextLine();
                System.out.print("Ingrese precio (ej: 1500.00): ");
                BigDecimal price = new BigDecimal(scanner.nextLine());
                System.out.print("Ingrese tamaño/medida: ");
                String size = scanner.nextLine();
                System.out.print("Ingrese stock inicial: ");
                Integer stock = Integer.parseInt(scanner.nextLine());
                System.out.print("Ingrese ID de la categoría a la que pertenece: ");
                Long categoryId = Long.parseLong(scanner.nextLine());

                try {
                    ProductDto saved =
                            productService.save(
                                    new ProductCreate(
                                            name,
                                            price,
                                            size,
                                            stock,
                                            "default.png",
                                            true,
                                            categoryId));
                    System.out.println(
                            "¡Producto guardado y asignado con éxito! ID asignado: " + saved.id());
                } catch (EntityNotFoundException e) {
                    System.out.println("Error relacional: " + e.getMessage());
                }
            }
            case 2 -> {
                System.out.print("Ingrese el ID del producto a dar de baja: ");
                Long id = Long.parseLong(scanner.nextLine());
                try {
                    productService.deleteById(id);
                    System.out.println(
                            "¡Baja lógica realizada con éxito para el producto ID: " + id);
                } catch (EntityNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.println("\n--- LISTADO DE PRODUCTOS ACTIVOS ---");
                List<ProductDto> list = productService.findAll();
                if (list.isEmpty()) {
                    System.out.println("No hay productos activos registrados.");
                } else {
                    list.forEach(
                            p ->
                                    System.out.printf(
                                            "ID: %d | Nombre: %s | Precio: $%s | Stock: %d | Cat_ID: %d\n",
                                            p.id(),
                                            p.name(),
                                            p.price().toString(),
                                            p.stock(),
                                            p.categoryId()));
                }
            }
            default -> System.out.println("Opción cancelada o inválida.");
        }
    }

    // =========================================================================
    // HU-09: CONSULTA JPQL CON PARÁMETRO NOMBRADO (Sección 6.4 del Parcial)
    // =========================================================================
    private void consultaJpqlProductosPorCategoria(Scanner scanner) {
        System.out.println("\n--- HU-09: LISTAR PRODUCTOS DE UNA CATEGORÍA ---");

        // 1. Listamos las categorías para que el operador seleccione una (Criterio de aceptación 1)
        List<CategoryDto> categorias = categoryService.findAll();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas para filtrar. Genere una primero.");
            return;
        }

        System.out.println("Categorías disponibles:");
        categorias.forEach(c -> System.out.printf("  [%d] - %s\n", c.id(), c.name()));
        System.out.print("Seleccione el ID de la categoría: ");
        Long categoryId = Long.parseLong(scanner.nextLine());

        // 2. Ejecutamos la consulta JPQL requerida (Criterio de aceptación 2, 3 y 4)
        List<Product> productosFiltrados = productRepository.buscarPorCategoria(categoryId);

        // 3. Informamos explícitamente si no hay productos activos (Criterio de aceptación 6)
        if (productosFiltrados.isEmpty()) {
            System.out.println(
                    "La categoría seleccionada no posee productos activos en este momento.");
        } else {
            System.out.println("\nResultados de la búsqueda (JPQL):");
            // 4. Mostramos los campos requeridos (Criterio de aceptación 5)
            productosFiltrados.forEach(
                    p ->
                            System.out.printf(
                                    "  -> ID: %d | Nombre: %s | Precio: $%s | Stock: %d\n",
                                    p.getId(), p.getName(), p.getPrice().toString(), p.getStock()));
        }
    }
}
