package ar.edu.tup.programacion3.SistemaGestionPedidos;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.CategoryService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;

// @Component
// @Order(1) // PRIORIDAD 1: Toma el control absoluto de la consola en el arranque
public class ConsoleMenuRunner implements CommandLineRunner {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ConfigurableApplicationContext context;

    // Bandera de control para la siembra dinámica
    private boolean isDbSeeded;

    public ConsoleMenuRunner(
            CategoryService categoryService,
            ProductService productService,
            ProductRepository productRepository,
            ConfigurableApplicationContext context) {

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

	    // =========================================================================
	    // CONTROL DE PERSISTENCIA INICIAL (Sincronización con H2)
	    // =========================================================================
	    // Evaluamos el estado real de la DB. Si ya tiene categorías, asumimos
	    // que el proceso de siembra histórica ya fue ejecutado previamente.
	    this.isDbSeeded = !categoryService.findAll().isEmpty();

	    System.out.println("\n=== SISTEMA INICIADO EN MÓDULO DE EXAMEN PARCIAL 2 ===");
	    if (isDbSeeded) {
		    System.out.println("[INFO] Se detectaron datos preexistentes en la DB. Opción semilla oculta.");
	    }

        while (opcionMain != 0) {
            System.out.println("\n-------------------------------------------");
            System.out.println("            MENÚ PRINCIPAL      ");
            System.out.println("-------------------------------------------");
            System.out.println("[1] - ABM de Categorías");
            System.out.println("[2] - ABM de Productos");
            System.out.println("[3] - Productos por Categoría (JPQL)");
            if (!isDbSeeded) {
                System.out.println("[4] - Poblar base de datos");
            }
            System.out.println("[0] - Salir de la aplicación");
            System.out.print("\nSeleccione una opción -> ");

            try {
                opcionMain = scMainInt(sc);
                switch (opcionMain) {
                    case 1 -> menuCategorias(sc);
                    case 2 -> menuProductos(sc);
                    case 3 -> consultaJpqlProductosPorCategoria(sc);
                    case 4 -> {
                        if (isDbSeeded) {
                            System.out.println("Opción inválida. Intente nuevamente.");
                        } else {
                            runDataInitialization(sc);
                        }
                    }
                    case 0 -> {
                        System.out.println("Cerrando el sistema... ¡Hasta luego!");
	                    context.close();
	                    break;
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
    // ABM DE CATEGORÍAS (HU-03, HU-04, HU-05)
    // =========================================================================
    private void menuCategorias(Scanner sc) {

        Long id;
        String name;
        String description;

        System.out.println("\n--- ABM DE CATEGORÍAS ---");
        System.out.println("[1] - Alta");
        System.out.println("[2] - Baja lógica (Soft Delete)");
        System.out.println("[3] - Modificar por ID");
        System.out.println("[4] - Listar activas");
        System.out.println("[0] - Volver al menú principal");
        System.out.print("\nSeleccione una opción -> ");

        try {
            Integer opcion = scInt(sc);
            if (opcion == null) throw new NumberFormatException();

            switch (opcion) {
                case 1 -> {
					// HU-03: Dar de alta una categoría
                    // CA-1: El sistema solicita nombre y descripcion por consola
                    System.out.print("Nombre de la categoría -> ");
                    name = scStr(sc);

                    System.out.print("Descripción -> ");
                    description = scStr(sc);

                    // CA-2: Se aborda a nivel DTO con el validator @ValidName en CategoryCreate
                    // y/o a nivel Service validando que no esté vacío. Aquí se delega la lógica al Service, 
                    // el cual arrojará una excepción que será atrapada y notificada sin persistir.
                    try {
                        CategoryResponseDTO saved =
                                categoryService.save(new CategoryRequestDTO(name, description));
                        // CA-3: Al guardar exitosamente, se muestra el ID generado por la base de datos
                        System.out.println(
                                "¡Categoría guardada con éxito! ID asignado: " + saved.id());

                        // CA-4: Cumplido por BaseEntity (que asigna createdAt) y el mapping de JPA 
                        // que por defecto deja deleted=false.
                    } catch (EntityNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error de validación: " + e.getMessage());
                    }
                }
                case 2 -> {
                    // HU-05: Dar de baja lógica una categoría
	                List<CategoryResponseDTO> auxCategories = listAndValidateAvailableCategories();
	                if(auxCategories.isEmpty()) {
		                System.out.println("¡Operación cancelada! Cree una categoría primero");
		                waitingEnter(sc);

		                return;
	                }

                    System.out.print("ID de la categoría a dar de baja ([0] para cancelar) -> ");
                    id = scLong(sc);
                    if (id == null) throw new NumberFormatException();

                    try {
	                    // CA-4: Buscamos la categoría en la lista en memoria para obtener su nombre antes de la baja.
	                    CategoryResponseDTO categoryToDelete = auxCategories.stream()
			                    .filter(c -> c.id().equals(id))
			                    .findFirst()
			                    .orElseThrow(
										() -> new EntityNotFoundException(
												"Categoría no encontrada con ID: " + id));

	                    // CA-1: La baja es lógica (se maneja en el servicio).
	                    // CA-2: El servicio arroja EntityNotFoundException si el ID no existe.
                        categoryService.deleteById(id);

	                    // CA-4: Se confirma la operación mostrando el nombre de la categoría afectada.
	                    System.out.print(" ¡Baja lógica realizada con éxito!\n");
	                    System.out.printf(" Categoría afectada -> ID: %d | Nombre: \"%s\"\n", id, categoryToDelete.name());

	                    // CA-3: La categoría ya no aparecerá en la próxima llamada a `listAndValidateAvailableCategories()`
	                    // o `categoryService.findAll()` debido al filtro @SQLRestriction("deleted = false").
                    } catch (EntityNotFoundException e) {

                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 3 -> {
                    // HU-04: Modificar una categoria existente
                    // CA-1: El sistema lista las categorias activas antes de pedir el ID.
                    List<CategoryResponseDTO> auxCategories = listAndValidateAvailableCategories();
                    if(auxCategories.isEmpty()) {
                        System.out.println("¡Operación cancelada! No hay categorías para modificar.");
                        waitingEnter(sc);

                        return;
                    }
                    
                    System.out.print("ID de la categoría a modificar ([0] para cancelar) -> ");
                    id = scLong(sc);
                    if (id == null) throw new NumberFormatException();

                    try {
                        // CA-2: Se cumple en el service.findById(id), que arroja EntityNotFoundException si no existe.
                        CategoryResponseDTO category = categoryService.findById(id);
                        
                        // CA-3: Se muestran los valores actuales antes de pedir los nuevos.
                        System.out.printf(
                                "ID: %d | Nombre: %s | Descripción: %s\n",
                                category.id(), category.name(), category.description());

                        System.out.print(
                                "Nuevo nombre de la categoría ([ENTER] para no modificar) -> ");
                        name = scStr(sc);

                        System.out.print("Nueva descripción ([ENTER] para no modificar) -> ");
                        description = scStr(sc);

                        // CA-4: La lógica de mantener el valor anterior si el campo está en blanco
                        // se maneja en el CategoryServiceImpl.update()
                        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO(name, description);
                        
                        // CA-5: El cambio se persiste correctamente en la base de datos.
                        categoryService.update(categoryRequestDTO, id);
                        System.out.println(
                                "¡Modificación realizada con éxito para la categoría ID: " + id);

                    } catch (EntityNotFoundException e) {
                        // Atrapa el error si el ID no es válido (CA-2)
                        System.out.println("Error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        // Atrapa el error cuando no hay cambios que guardar en base de datos.
                        System.out.println("Aviso: " + e.getMessage());
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
                                        System.out.println(
                                                " No hay categorías activas registradas en el sistema.");
                                        System.out.println("=".repeat(88));
                                    });
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
    // ABM DE PRODUCTOS (HU-06, HU-07, HU-08)
    // =========================================================================
    private void menuProductos(Scanner sc) {

        Long id;
        String name;
        BigDecimal price;
        String description;
        Integer stock;

        System.out.println("\n--- ABM DE PRODUCTOS ---");
        System.out.println("[1] - Alta");
        System.out.println("[2] - Baja lógica (Soft Delete)");
        System.out.println("[3] - Modificar por ID");
        System.out.println("[4] - Listar activos");
        System.out.println("[0] - Volver al menú principal");
        System.out.print("\nSeleccione una opción -> ");

        Integer opcion = scInt(sc);
        if (opcion == null) {
            System.out.println("Error: Por favor ingrese un número válido.");
            return;
        }

        switch (opcion) {
            case 1 -> {
                // HU-06: Dar de alta un producto
                // CA-1: El sistema lista las categorias activas para que el operador seleccione una.
                List<CategoryResponseDTO> auxCategories = listAndValidateAvailableCategories();

                // CA-2: Si no hay categorias activas, se informa y se cancela la operacion.
                if(auxCategories.isEmpty()) {
                    System.out.println("¡Operación cancelada! Cree una categoría primero.");
                    waitingEnter(sc);
                    return;
                }

                System.out.print("ID de la categoría a la que se asignará el nuevo producto -> ");
                final Long categoryId = scLong(sc);
                if (categoryId == null) {
                    System.out.println("Error: Por favor ingrese un número válido.");
                    return;
                }

                // CA-3: Se solicitan los datos del producto.
                System.out.print("Nombre del producto -> ");
                name = scStr(sc);

                System.out.print("Precio -> ");
                price = scBigDec(sc);

                System.out.print("Descripción/tamaño/medida -> ");
                description = scStr(sc);

                System.out.print("Stock inicial -> ");
                stock = scInt(sc);

                try {
                    // CA-4: La validación de precio y stock se delega al ProductService/Entity.
                    ProductResponseDTO saved =
                            productService.save(
                                    new ProductRequestDTO(
                                            name,
                                            price,
                                            description,
                                            stock,
                                            "default.png",
                                            true,
                                            categoryId));

                    // CA-5: Al guardar, se muestra el ID generado y la categoria asignada.
                    String categoryName = auxCategories.stream()
                            .filter(c -> c.id().equals(categoryId))
                            .findFirst()
                            .map(CategoryResponseDTO::name)
                            .orElse("Desconocida");
                    
                    System.out.println("¡Producto guardado con éxito!");
                    System.out.printf("ID asignado: %d | Categoría: %s\n", saved.id(), categoryName);

                } catch (EntityNotFoundException e) {
                    System.out.println("Error relacional: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Error de validación: " + e.getMessage());
                }
            }
            case 2 -> {
                // HU-08: Dar de baja un producto
                System.out.print("ID del producto a dar de baja ([0] para cancelar) -> ");
                id = scLong(sc);
                if (id == null) {
                    System.out.println("Error: Por favor ingrese un número válido.");
                    return;
                }

                try {
                    // CA-4: Buscamos el producto en memoria antes de la baja para obtener su nombre
                    ProductResponseDTO productToDelete = productService.findById(id);

                    // CA-1: La baja es lógica, el servicio simplemente actualiza deleted = true
                    // CA-2: Si el ID no existe o ya está dado de baja, findById o deleteById arrojará una excepción
                    productService.deleteById(id);

                    // CA-4: Se muestra confirmación con el nombre del producto afectado
                    System.out.print(" ¡Baja lógica realizada con éxito!\n");
                    System.out.printf(" Producto afectado -> ID: %d | Nombre: \"%s\"\n", id, productToDelete.name());

                    // CA-3: El producto ya no aparecerá en el listado debido al filtro en el repositorio (@SQLRestriction("deleted = false"))
                } catch (EntityNotFoundException e) {

                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 3 -> {
                // HU-07: Modificar un producto
                // CA-1: El sistema lista los productos activos antes de pedir el ID.
	            List<CategoryResponseDTO> auxCategories = listAndValidateAvailableCategories();

	            if(auxCategories.isEmpty()) {
		            System.out.println("¡Operación cancelada! Cree una categoría primero.");
		            waitingEnter(sc);
		            return;
	            }

                System.out.print("ID del producto a modificar ([0] para cancelar) -> ");
                id = scLong(sc);
                if (id == null) {
                    System.out.println("Error: Por favor ingrese un número válido.");
                    return;
                }

                try {
                    // CA-2: El servicio arroja EntityNotFoundException si el ID no existe.
                    ProductResponseDTO product = productService.findById(id);
                    
                    // CA-3: Se muestran los valores actuales antes de pedir los nuevos.
                    System.out.printf(
                            "ID: %d | Nombre: %s | Precio: $%s | Cantidad: %s\n",
                            product.id(), product.name(), product.price(), product.stock());

                    System.out.print("Nuevo nombre del producto ([ENTER] para no modificar) -> ");
                    name = scStr(sc);

                    System.out.print("Nuevo precio ([ENTER] para no modificar) -> ");
                    price = scBigDec(sc);

                    System.out.print("Nuevo stock ([ENTER] para no modificar) -> ");
                    stock = scInt(sc);

                    // CA-4, 5, 6: La lógica se delega al ProductServiceImpl.update()
                    ProductRequestDTO productRequestDTO =
                            new ProductRequestDTO(
                                    name,
                                    price,
                                    product.description(),
                                    stock,
                                    product.image(),
                                    stock != null ? stock > 0 : product.available(),
                                    product.categoryId());
                    productService.update(productRequestDTO, id);

                    System.out.println(
                            "¡Modificación realizada con éxito para el producto ID: " + id);

                } catch (EntityNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Aviso: " + e.getMessage());
                }
            }
            case 4 -> {
                Map<Long, String> categoryMap =
                        categoryService.findAll().stream()
                                .collect(Collectors.toMap(CategoryResponseDTO::id, CategoryResponseDTO::name));

                int n = 87;
                printProductHead(n);

                Optional.ofNullable(productService.findAll())
                        .filter(list -> !list.isEmpty())
                        .ifPresentOrElse(
                                list -> {
                                    list.forEach(p -> printProductRow(p, categoryMap));
                                    System.out.println("=".repeat(n));
                                },
                                () -> {
                                    System.out.println(
                                            " No hay productos activos registrados en el sistema.");
                                    System.out.println("=".repeat(n));
                                });
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
    // CONSULTA JPQL CON PARÁMETRO NOMBRADO (HU-09)
    // =========================================================================
    private void consultaJpqlProductosPorCategoria(Scanner sc) {

        System.out.println("\n--- LISTAR PRODUCTOS DE UNA CATEGORÍA ---");

        // HU-09: Listar productos de una categoría
        // CA-1: El sistema lista las categorías activas para que el operador seleccione una.
        List<CategoryResponseDTO> categories = listAndValidateAvailableCategories();

	    if (categories.isEmpty()) {

		    waitingEnter(sc);

		    return;
	    }

        System.out.print("\nSeleccione el ID de la categoría ([0] para cancelar) -> ");
        Long categoryId = scLong(sc);
        if (categoryId == null) {
            System.out.println("Error: Por favor ingrese un número válido.");
            return;
        }

	    boolean categoryExists = categories.stream().anyMatch(c -> c.id().equals(categoryId));

	    if (!categoryExists) {

		    System.out.println("\n" + "=".repeat(87));
		    System.out.printf(" Error: El ID de categoría [%d] no corresponde a ninguna categoría existente.\n", categoryId);
		    System.out.println("=".repeat(87));

			waitingEnter(sc);

			return;
	    }
		
        // CA-2: La consulta está implementada en ProductoRepository con JPQL y parámetro nombrado :categoriaId.
        // CA-3: Se usa TypedQuery<Product>; no hay casteos manuales en el código (esto está en el repositorio/entity manager).
        // CA-4: Solo se incluyen productos con eliminado = false (esto está garantizado por @SQLRestriction en Product).
        List<Product> filteredProducts = productRepository.findByCategoryId(categoryId);

        // CA-6: Si la categoría no tiene productos activos, se informa explícitamente.
        if (filteredProducts.isEmpty()) {

            System.out.println("\n" + "=".repeat(87));
            System.out.println(
                    " La categoría seleccionada no posee productos activos en este momento.");
            System.out.println("=".repeat(87));
        } else {

            Map<Long, String> categoryMap =
                    categories.stream()
                            .collect(Collectors.toMap(CategoryResponseDTO::id, CategoryResponseDTO::name));

            int n = 87;
            printProductHead(n);

            // CA-5: El resultado muestra: ID, nombre, precio y stock de cada producto.
            filteredProducts.forEach(
                    p -> {
                        ProductResponseDTO dtoEquivalent =
                                new ProductResponseDTO(
                                        p.getId(),
                                        p.getName(),
                                        p.getPrice(),
                                        p.getDescription(),
                                        p.getStock(),
                                        p.getImage(),
                                        p.getAvailable(),
                                        categoryId);
                        printProductRow(dtoEquivalent, categoryMap);
                    });
            System.out.println("=".repeat(n));
        }
        waitingEnter(sc);
    }

    private Integer scMainInt(Scanner sc) {
        return Integer.parseInt(sc.nextLine());
    }

    private Integer scInt(Scanner sc) {

        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        Integer value = Integer.parseInt(input);
        if (value == 0) {
            throw new OperationCancelledException();
        }

        return value;
    }

    private String scStr(Scanner sc) {
        return sc.nextLine();
    }

    private Long scLong(Scanner sc) {

        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }

        Long value = Long.parseLong(input);
        if (value == 0) {
            throw new OperationCancelledException();
        }

        return value;
    }

    private BigDecimal scBigDec(Scanner sc) {
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        return new BigDecimal(input);
    }

    private void printCategoryHead() {

        int n = 88;
        System.out.println("\n" + "=".repeat(n));
        System.out.println(" ".repeat(29) + "LISTADO DE CATEGORÍAS ACTIVAS");
        System.out.println("=".repeat(n));
        System.out.printf(
                "| %-6s | %-25s | %-35s | %-9s |\n", "ID", "NOMBRE", "DESCRIPCIÓN", "PRODUCTOS");
        System.out.println("-".repeat(n));
    }

    private void printProductHead(int n) {

        System.out.println("\n" + "=".repeat(n));
        System.out.println(" ".repeat(29) + "LISTADO DE PRODUCTOS ACTIVOS");
        System.out.println("=".repeat(n));
        System.out.printf(
                "| %-6s | %-25s | %-12s | %-8s | %-20s |\n",
                "ID", "NOMBRE", "PRECIO", "CANTIDAD", "CATEGORÍA");
        System.out.println("-".repeat(n));
    }

    private void printCategoryRow(CategoryResponseDTO c) {

        System.out.printf(
                "| %-6d | %-25.25s | %-35.35s | %9d |\n",
                c.id(),
                c.name(),
                c.description() != null ? c.description() : "",
                c.products() != null ? c.products().size() : 0);
    }

    private void printProductRow(ProductResponseDTO p, java.util.Map<Long, String> categoryMap) {

        String categoryName = categoryMap.getOrDefault(p.categoryId(), "Sin Categoría");
        System.out.printf(
                "| %-6d | %-25.25s | $%11.2f | %8d | %-20.20s |\n",
                p.id(), p.name(), p.price(), p.stock(), categoryName);
    }

    private void waitingEnter(Scanner sc) {

        System.out.print("\nPresione [ENTER] para regresar al Menú Principal..");

        String input = sc.nextLine();

        while (input == null) {
            input = sc.nextLine();
        }
    }

    public static class OperationCancelledException extends RuntimeException {

        public OperationCancelledException() {
            super("Operación cancelada por el usuario.");
        }
    }

	public List<CategoryResponseDTO> listAndValidateAvailableCategories() {

		List<CategoryResponseDTO> categories = categoryService.findAll();
		if (categories.isEmpty()) {
			System.out.println("No hay categorías activas. Genere una primero.");

			return categories;
		}

		System.out.println("Categorías disponibles:");
		categories.forEach(c -> System.out.printf("  [%d] - %s\n", c.id(), c.name()));

		return categories;
	}

    private void runDataInitialization(Scanner sc) {
        System.out.println(
                "\n=================================================================================");
        System.out.println(
                "                  INICIANDO PROCESO DE SIEMBRA DE DATOS SEMILLA                  ");
        System.out.println(
                "=================================================================================");

        try {
            // ==========================================
            // a) Instanciar 3 Categorías
            // ==========================================
            CategoryResponseDTO catComida =
                    categoryService.save(
                            new CategoryRequestDTO("Comida Rápida", "Hamburguesas y lomos"));

            CategoryResponseDTO catBebidas =
                    categoryService.save(new CategoryRequestDTO("Bebidas", "Gaseosas y aguas"));

            CategoryResponseDTO catPostres =
                    categoryService.save(new CategoryRequestDTO("Postres", "Helados y tortas"));

            // ==========================================
            // b) Instanciar 10 Productos y asignarles categorías
            // ==========================================
            // Comidas
            ProductResponseDTO p1 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Hamburguesa Simple",
                                    new BigDecimal("5500.00"),
                                    "Mediana",
                                    20,
                                    "img_h1.png",
                                    true,
                                    catComida.id()));

            ProductResponseDTO p2 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Hamburguesa Doble",
                                    new BigDecimal("13000.00"),
                                    "Con queso",
                                    15,
                                    "img_h2.png",
                                    true,
                                    catComida.id()));

            ProductResponseDTO p3 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Lomo Completo",
                                    new BigDecimal("20800.00"),
                                    "Para compartir",
                                    10,
                                    "img_l1.png",
                                    true,
                                    catComida.id()));

            ProductResponseDTO p4 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Papas Fritas",
                                    new BigDecimal("3500.00"),
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
                                    new BigDecimal("1550.00"),
                                    "Común",
                                    100,
                                    "coca.png",
                                    true,
                                    catBebidas.id()));

            ProductResponseDTO p6 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Agua Mineral 500ml",
                                    new BigDecimal("1890.00"),
                                    "Sin gas",
                                    80,
                                    "agua.png",
                                    true,
                                    catBebidas.id()));

            ProductResponseDTO p7 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Cerveza Quilmes",
                                    new BigDecimal("1800.00"),
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
                                    new BigDecimal("8100.00"),
                                    "Casero",
                                    12,
                                    "flan.png",
                                    true,
                                    catPostres.id()));

            ProductResponseDTO p9 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Helado 1/4kg",
                                    new BigDecimal("7300.00"),
                                    "Dos gustos",
                                    25,
                                    "helado.png",
                                    true,
                                    catPostres.id()));

            ProductResponseDTO p10 =
                    productService.save(
                            new ProductRequestDTO(
                                    "Ensalada de Frutas",
                                    new BigDecimal("5000.00"),
                                    "Frutas de estación",
                                    8,
                                    "frutas.png",
                                    true,
                                    catPostres.id()));

            // Cambiamos el estado de la bandera para ocultar la opción en el próximo ciclo del
            // bucle
            this.isDbSeeded = true;

            System.out.println("¡Base de datos poblada con éxito de forma segura!");
            System.out.println(
                    "\nA partir de este momento, la opción de siembra ha sido deshabilitada.");
            System.out.println(
                    "=================================================================================");
        } catch (Exception e) {

            System.out.println(" Error durante la siembra de datos: " + e.getMessage());
            System.out.println(
                    "=================================================================================");
        } finally {
            waitingEnter(sc);
        }
    }
}