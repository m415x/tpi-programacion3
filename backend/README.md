# Sistema de Gestión de Pedidos (Parcial 2)

Este módulo contiene la API Core y la interfaz de pruebas interactiva para el sistema de gestión de pedidos del ecommerce **Food Store**, desarrollado bajo la arquitectura propuesta para la materia **Programación III**.

El sistema integra un esquema completo de persistencia relacional automatizada utilizando **Spring Data JPA**, **Hibernate** y un motor de base de datos **H2** persistido en archivos locales.

---

## Arquitectura y Decisiones Técnicas Destacadas

- **Inversión de Control (IoC) y Gestión de Persistencia:** Se prescindió del uso manual de utilidades estáticas como `JPAUtil.java`. En su lugar, se delegó el ciclo de vida completo de las conexiones, el `EntityManagerFactory` y los contextos transaccionales en el contenedor de Spring mediante el uso estratégico de la anotación `@Transactional`.
- **Idempotencia en Datos Semilla:** El sistema de consola detecta dinámicamente el estado real de la base de datos en disco durante el arranque. Si ya existen registros históricos, la opción de siembra de datos semilla se oculta automáticamente para mitigar excepciones por violación de restricciones de unicidad (`SQLState: 23505`).
- **Control de Flujo Orientado a Objetos:** Se implementó la excepción personalizada `OperationCancelledException` integrada en los métodos de lectura asistida del `Scanner`. Esto permite centralizar las operaciones de cancelación global (`[0] para regresar`) sin contaminar linealmente los bloques del menú con lógica de control duplicada.
- **Optimización de Consultas (Caché en RAM):** En la visualización tabular de productos, se implementó una indexación al vuelo en memoria RAM (`Map<Long, String>`) para resolver el nombre de las categorías en tiempo constante $O(1)$, mitigando de raíz el problema clásico de rendimiento conocido como _Query N+1_.

---

## Historias de Usuario Implementadas e Hitos Visuales

El módulo de consola interactivo (`ConsoleMenuRunner`) se encuentra estructurado para validar estrictamente los Criterios de Aceptación (CA) exigidos en la rúbrica del examen:

### Épica: ABM de Categorías (Secciones 6.1 y 6.2)

- **Alta de Categoría (HU-03):** Persistencia de nuevas categorías con generación de IDs secuenciales automáticos.
- **Baja Lógica / Soft Delete (HU-05):** Inactivación del registro sin destrucción física de la fila en la DB. Cumple estrictamente con el **CA-4** al recuperar el contexto previo y confirmar en pantalla el nombre de la categoría afectada.
- **Modificación por ID (HU-04):** Validación de existencia vía servicio (`CA-2`), despliegue de datos actuales (`CA-3`) y soporte elástico para conservar campos previos ante entradas en blanco (`[ENTER]`) (`CA-4`).
- **Listar Activas:** Formato tabular unificado (Bordes ASCII, simetría horizontal, alineación fija y truncado de seguridad en cadenas extensas).

### Épica: ABM de Productos (Sección 6.3)

- **Alta de Producto (HU-01):** Asistencia dinámica interactiva para asociar el producto a categorías preexistentes.
- **Listado Completo (HU-02):** Renderizado simétrico estilo hoja de cálculo con formato monetario explícito de dos decimales.

### Épica: Consultas Avanzadas (Sección 6.4)

- **Consulta JPQL por Categoría (HU-09):** Implementación de una consulta personalizada con parámetros nombrados (`@Param`) mediante la cláusula `JOIN` en el repositorio genérico. Cuenta con validación semántica diferenciada: distingue de forma precisa entre una categoría vacía y un ID de categoría inexistente.

---

## Instrucciones de Ejecución y Pruebas

### Requisitos Previos

- **Java Development Kit (JDK):** Versión 21.
- **Herramienta de Construcción:** Gradle (incluido vía wrapper).

### Pasos para iniciar el sistema:

1.  Posicionarse en el directorio del backend:
    ```bash
    cd backend
    ```
2.  Limpiar cachés y compilar el proyecto asegurando los estándares del proyecto:
    ```bash
    ./gradlew clean build -x test
    ```
3.  Ejecutar la aplicación con el modo de consola plano:
    ```bash
    ./gradlew bootRun --console=plain
    ```
