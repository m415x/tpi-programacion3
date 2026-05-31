package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.CategoryService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
@Order(2) // <-- PRIORIDAD 2: Se ejecuta SEGUNDO, una vez sembrada la DB
public class ConsoleMenuRunner implements CommandLineRunner {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductRepository productRepository;
	private final ConfigurableApplicationContext context;

    public ConsoleMenuRunner(
		    CategoryService categoryService,
		    ProductService productService,
		    ProductRepository productRepository, ConfigurableApplicationContext context) {

        this.categoryService = categoryService;
        this.productService = productService;
        this.productRepository = productRepository;
	    this.context = context;
    }

    // =========================================================================
    // CICLO DE VIDA DE LA APLICACIÓN (MÓDULO DE PRUEBAS DEL PARCIAL 2)
    // =========================================================================
    // @NonNull: Garantiza la seguridad de tipos (Type Safety) según los estándares
    // de Spring Boot 4 y Java 21, evitando alertas de análisis estático por
    // posibles punteros nulos en los argumentos de inicio.
    // =========================================================================
    @Override
    public void run(String @NonNull ... args) throws Exception {

        Scanner sc = new Scanner(System.in);
        int opcionMain = -1;

        System.out.println("\n=== SISTEMA INICIADO EN MÓDULO DE EXAMEN PARCIAL ===");

        while (opcionMain != 0) {
            System.out.println("\n-------------------------------------------");
            System.out.println("            MENÚ PRINCIPAL      ");
            System.out.println("-------------------------------------------");
            System.out.println("1. ABM de Categorías");
            System.out.println("2. ABM de Productos");
            System.out.println("3. Consultar Productos por Categoría (JPQL)");
            System.out.println("0. Salir de la aplicación");
            System.out.print("\nSeleccione una opción -> ");

            try {
                opcionMain = scMainInt(sc);
                switch (opcionMain) {
                    case 1 -> menuCategorias(sc);
                    case 2 -> menuProductos(sc);
                    case 3 -> consultaJpqlProductosPorCategoria(sc);
                    case 0 -> {
                        System.out.println("Cerrando el sistema... ¡Hasta luego!");
	                    context.close();
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
    private void menuCategorias(Scanner sc) {

        Long id;
        String name;
        String description;

        System.out.println("\n--- ABM DE CATEGORÍAS ---");
        System.out.println("1. Alta");
        System.out.println("2. Baja lógica (Soft Delete)");
        System.out.println("3. Modificar por ID");
        System.out.println("4. Listar activas");
        System.out.println("0. Volver al menú principal");
        System.out.print("\nSeleccione una opción -> ");

        try {
            int opcion = scInt(sc);

            switch (opcion) {
                case 1 -> {
                    System.out.print("Nombre de la categoría -> ");
                    name = scStr(sc);

                    System.out.print("Descripción -> ");
                    description = scStr(sc);

                    try {
                        CategoryDto saved =
                                categoryService.save(new CategoryCreate(name, description));
                        System.out.println(
                                "¡Categoría guardada con éxito! ID asignado: " + saved.id());

                    } catch (EntityNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.print("ID de la categoría a dar de baja (0 para cancelar) -> ");
                    id = scLong(sc);

                    try {
                        categoryService.deleteById(id);
                        System.out.println(
                                "¡Baja lógica realizada con éxito para la categoría ID: " + id);

                    } catch (EntityNotFoundException e) {

                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 3 -> {
                    System.out.print("ID de la categoría a modificar (0 para cancelar) -> ");
                    id = scLong(sc);

                    try {
                        CategoryDto category = categoryService.findById(id);
                        System.out.printf(
                                "ID: %d | Nombre: %s | Descripción: %s\n",
                                category.id(), category.name(), category.description());

                        System.out.print(
                                "Nuevo nombre de la categoría (Enter para no modificar) -> ");
                        name = scStr(sc);

                        System.out.print("Nueva descripción (Enter para no modificar) -> ");
                        description = scStr(sc);

                        CategoryEdit categoryEdit = new CategoryEdit(name, description);
                        categoryService.update(categoryEdit, id);
                        System.out.println(
                                "¡Modificación realizada con éxito para la categoría ID: " + id);

                    } catch (EntityNotFoundException e) {

                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 4 -> {
					printCategoryHead();

                    Optional.ofNullable(categoryService.findAll())
                            .filter(list -> !list.isEmpty())
                            .ifPresentOrElse(
                                    list -> {
	                                    list.forEach(this::printCategoryRow);
	                                    System.out.println("=".repeat(88));
                                    },
                                    () -> {
	                                    System.out.println(" No hay categorías activas registradas en el sistema.");
	                                    System.out.println("=".repeat(88));
                                    }
                            );
                }
                case 0 -> {
                    System.out.println("Volviendo al menú principal...");
                    return;
                }
                default -> System.out.println("Opción cancelada o inválida.");
            }
            waitingEnter(sc);

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }

    // =========================================================================
    // ABM DE PRODUCTOS (Sección 6.3 del Parcial)
    // =========================================================================
    private void menuProductos(Scanner sc) {

        Long id;
        String name;
        BigDecimal price;
        String description;
        Integer stock;
        Long categoryId = 0L;

        System.out.println("\n--- ABM DE PRODUCTOS ---");
        System.out.println("1. Alta");
        System.out.println("2. Baja lógica (Soft Delete)");
        System.out.println("3. Modificar por ID");
        System.out.println("4. Listar activos");
        System.out.println("0. Volver al menú principal");
        System.out.print("\nSeleccione una opción -> ");

        int opcion = Integer.parseInt(sc.nextLine());

        switch (opcion) {
            case 1 -> {
                System.out.print("Nombre del producto -> ");
                name = scStr(sc);

                System.out.print("Precio (ej: 1500.00) -> ");
                price = scBigDec(sc);

                System.out.print("Descripción/tamaño/medida -> ");
                description = scStr(sc);

                System.out.print("Stock inicial -> ");
                stock = scInt(sc);

                while (categoryId == 0) {
                    System.out.print(
                            "ID de la categoría a la que pertenece (0 para consultar categorías) -> ");
                    categoryId = scLong(sc);

                    if (categoryId == 0) {
                        categoryService
                                .findAll()
                                .forEach(
                                        cat ->
                                                System.out.printf(
                                                        "  [%d] - %s\n", cat.id(), cat.name()));
                    }
                }

                try {
                    ProductDto saved =
                            productService.save(
                                    new ProductCreate(
                                            name,
                                            price,
                                            description,
                                            stock,
                                            "default.png",
                                            true,
                                            categoryId));
					
                    System.out.println("¡Producto guardado con éxito! ID asignado: " + saved.id());

                } catch (EntityNotFoundException e) {

                    System.out.println("Error relacional: " + e.getMessage());
                }
            }
            case 2 -> {
                System.out.print("ID del producto a dar de baja (0 para cancelar) -> ");
                id = scLong(sc);

                try {
                    productService.deleteById(id);
                    System.out.println(
                            "¡Baja lógica realizada con éxito para el producto ID: " + id);

                } catch (EntityNotFoundException e) {

                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.print("ID del producto a modificar (0 para cancelar) -> ");
                id = scLong(sc);

                try {
                    ProductDto product = productService.findById(id);
                    System.out.printf(
                            "ID: %d | Nombre: %s | Precio: $%s | Cantidad: %s\n",
                            product.id(), product.price(), product.name(), product.stock());

                    System.out.print("Nombre del producto -> ");
                    name = scStr(sc);

                    System.out.print("Precio -> ");
                    price = scBigDec(sc);

                    System.out.print("Nuevo stock -> ");
                    stock = scInt(sc);

                    ProductEdit productEdit =
                            new ProductEdit(
                                    name,
                                    price,
                                    product.description(),
                                    stock,
                                    product.image(),
                                    stock > 0,
                                    product.categoryId());
                    productService.update(productEdit, id);
                    System.out.println(
                            "¡Modificación realizada con éxito para el producto ID: " + id);

                } catch (EntityNotFoundException e) {

                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 4 -> {

                Map<Long, String> categoryMap =
                        categoryService.findAll().stream()
                                .collect(Collectors.toMap(CategoryDto::id, CategoryDto::name));

	            printProductHead();

                Optional.ofNullable(productService.findAll())
                        .filter(list -> !list.isEmpty())
                        .ifPresentOrElse(
                                list -> {
	                                list.forEach(p -> printProductRow(p, categoryMap));
	                                System.out.println("=".repeat(87));
                                },

		                        () -> {
			                        System.out.println(" No hay productos activos registrados en el sistema.");
			                        System.out.println("=".repeat(87));
		                        }
                        );
            }
            case 0 -> {
                System.out.println("Volviendo al menú principal...");
                return;
            }
            default -> System.out.println("Opción cancelada o inválida.");
        }
        waitingEnter(sc);
    }

    // =========================================================================
    // HU-09: CONSULTA JPQL CON PARÁMETRO NOMBRADO (Sección 6.4 del Parcial)
    // =========================================================================
    private void consultaJpqlProductosPorCategoria(Scanner sc) {

        System.out.println("\n--- HU-09: LISTAR PRODUCTOS DE UNA CATEGORÍA ---");

        // 1. Listamos las categorías para que el operador seleccione una (Criterio de
        // aceptación 1)
        List<CategoryDto> categorias = categoryService.findAll();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías activas para filtrar. Genere una primero.");
            return;
        }

        System.out.println("Categorías disponibles:");
        categorias.forEach(c -> System.out.printf("  [%d] - %s\n", c.id(), c.name()));
        System.out.print("Seleccione el ID de la categoría -> ");
        Long categoryId = Long.parseLong(sc.nextLine());

        // 2. Ejecutamos la consulta JPQL requerida (Criterio de aceptación 2, 3 y 4)
        List<Product> productosFiltrados = productRepository.buscarPorCategoria(categoryId);

        // 3. Informamos explícitamente si no hay productos activos (Criterio de
        // aceptación 6)
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

	private Integer scMainInt(Scanner sc) {
		return Integer.parseInt(sc.nextLine());
	}

    private Integer scInt(Scanner sc) {

        Integer value = Integer.parseInt(sc.nextLine());
        if (value == 0) {
            throw new OperationCancelledException();
        }

        return value;
    }

    private String scStr(Scanner sc) {
        return sc.nextLine();
    }

    private Long scLong(Scanner sc) {

        Long value = Long.parseLong(sc.nextLine());
        if (value == 0) {
            throw new OperationCancelledException();
        }

        return value;
    }

    private BigDecimal scBigDec(Scanner sc) {
        return new BigDecimal(sc.nextLine());
    }

	private void printCategoryHead() {

		int n = 88;
		System.out.println("\n" + "=".repeat(n));
		System.out.println(" ".repeat(29) + "LISTADO DE CATEGORÍAS ACTIVAS");
		System.out.println("=".repeat(n));
		System.out.printf("| %-6s | %-25s | %-35s | %-9s |\n", "ID", "NOMBRE", "DESCRIPCIÓN", "PRODUCTOS");
		System.out.println("-".repeat(n));
	}

	private void printProductHead() {

		int n = 87;
		System.out.println("\n" + "=".repeat(n));
		System.out.println(" ".repeat(29) + "LISTADO DE PRODUCTOS ACTIVOS");
		System.out.println("=".repeat(n));
		System.out.printf(
				"| %-6s | %-25s | %-12s | %-8s | %-20s |\n", "ID", "NOMBRE", "PRECIO", "CANTIDAD", "CATEGORÍA");
		System.out.println("-".repeat(n));
	}

	private void printCategoryRow(CategoryDto c) {

		System.out.printf("| %-6d | %-25.25s | %-35.35s | %9d |\n",
				c.id(),
				c.name(),
				c.description() != null ? c.description() : "",
				c.products() != null ? c.products().size() : 0
		);
	}

	private void printProductRow(ProductDto p, java.util.Map<Long, String> categoryMap) {

		String categoryName = categoryMap.getOrDefault(p.categoryId(), "Sin Categoría");
		System.out.printf("| %-6d | %-25.25s | $%11.2f | %8d | %-20.20s |\n",
				p.id(),
				p.name(),
				p.price(),
				p.stock(),
				categoryName
		);
	}

    private void waitingEnter(Scanner sc) {

        System.out.print("Presione Enter para continuar...");
        scStr(sc);
    }

    public static class OperationCancelledException extends RuntimeException {

        public OperationCancelledException() {
            super("Operación cancelada por el usuario.");
        }
    }
}
