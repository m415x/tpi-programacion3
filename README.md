# Food Store - Sistema de Gestión de Pedidos

Este repositorio contiene el ecosistema completo para **Food Store**, una plataforma e-commerce integrada de punta a punta. El proyecto unifica una interfaz de usuario web reactiva y moderna con una API REST robusta de persistencia relacional automatizada.

Desarrollado con fines educativos bajo los estándares de la cátedra de **Programación III**, el ecosistema demuestra el flujo real de datos desde la manipulación del DOM en el cliente hasta el almacenamiento en disco mediante un motor de base de datos.

---

## Arquitectura General del Ecosistema

El proyecto se encuentra dividido en dos módulos core perfectamente desacoplados que se comunican mediante solicitudes HTTP:

```text
┌─────────────────────────┐  Peticiones HTTP (JSON)  ┌─────────────────────────────────┐
│ FRONTEND (Puerto 5173)  │ ───────────────────────> │      BACKEND (Puerto 8008)      │
│ Vite + TypeScript + CSS │ <─────────────────────── │ Spring Boot + Gradle + JPA + H2 │
└─────────────────────────┘  Respuestas REST (DTOs)  └─────────────────────────────────┘
```

1. **Frontend (`/frontend`):** Una aplicación Single Page (SPA) construida con **Vite**, **TypeScript Vanilla** (manipulación directa del DOM) y CSS puro. Implementa un catálogo dinámico, gestión asíncrona de cantidades en carrito con validación perimetral y un panel de administración (Dashboard) protegido por roles.
2. **Backend (`/backend`):** Una API REST robusta construida con **Spring Boot**, **Java 21** y **Gradle**. Centraliza la lógica de negocio, validaciones estrictas de Bean Validation (JPA Groups) mediante anotaciones personalizadas, control transaccional (`@Transactional`) y un motor de base de datos relacional **H2** persistido de forma local en disco.

---

## Decisiones Técnicas Destacadas

### Frontend (Frontera Defensiva)

- **Enfoque Asíncrono de Autoabastecimiento:** Los controladores de la UI (como `productDetail` o `cart`) son 100% autónomos. No arrastran estados globales cableados; consultan de forma directa y asíncrona al backend mediante Axios ante recargas de pantalla (`F5`), garantizando la frescura de los datos.
- **Criptografía en el Cliente (SHA-256):** Las contraseñas se rompen mediante un algoritmo de hashing unidireccional nativo (`crypto.subtle.digest`) en el navegador. **Las contraseñas nunca viajan en texto plano por la red**. El backend almacena directamente el hash hexadecimal estable de 64 caracteres.
- **Validación Anticipada de Stock:** El carrito consulta en tiempo real al almacenamiento y al servicio HTTP del servidor antes de permitir un incremento, bloqueando interacciones inválidas mediante alertas atómicas.

### Backend (API Core y Persistencia)

- **Inversión de Control (IoC):** Se prescindió del uso manual de utilidades estáticas. Se delegó el ciclo de vida completo de las conexiones, el `EntityManagerFactory` y los contextos transaccionales en el contenedor de Spring Boot.
- **Estrategia Antiduplicación en Semilla:** El sistema detecta dinámicamente el estado de la base de datos en disco durante el arranque. Si ya existen registros históricos o el usuario administrador inicial, la siembra de datos semilla (`Data Seeding`) se gestiona de forma idempotente para mitigar excepciones por violación de restricciones de unicidad (`SQLState: 23505`).
- **Optimización de Consultas (Caché en RAM):** En los mapeos tabulares se implementó una indexación al vuelo en 
  memoria RAM (`Map<UUID, String>`) para resolver el nombre de las categorías en tiempo constante $O(1)$, mitigando de 
  raíz el problema clásico de rendimiento conocido como _Query N+1_.
- **Arquitectura de Validación Avanzada:** Implementación de la anotación personalizada `@ValidPassword` coordinada con interfaces de grupo (`OnCreate.class`, `OnUpdate.class`) para verificar que los datos enviados a los endpoints cumplan estrictamente con el patrón hexadecimal `^[a-f0-9]{64}$` antes del _persist time_ de Hibernate.

---

## Estructura del Repositorio

```text
/
├── backend/                         # Código de la API de Spring Boot (Java)
│   ├── data/                        # Base de datos H2 persistida en disco
│   ├── src/
│   │   └── main/
|   │       ├── java/.../
│   │       │   ├── config/          # Configuracion de OpenAPI y Swagger
│   │       │   ├── controller/      # Endpoints REST (Product, Category, User)
│   │       │   ├── dto/             # Objetos de Transferencia de Datos (Records)
│   │       │   ├── exception/       # Excepciones personalizadas
│   │       │   ├── mapper/          # Mapeos de Dominio a DTO
│   │       │   ├── model/           # Entidades de Dominio de JPA / Hibernate
│   │       │   ├── repository/      # Repositorios heredados de JpaRepository
│   │       │   ├── service/         # Lógica de negocio e implementaciones
│   │       │   ├── validator/       # Anotaciones de validación y regex de hash
│   │       │   └── SGPApp.java      # Clase principal de la aplicación Spring Boot
|   │       └── resources/
│   │           └── application.yaml # Configuración de Spring Boot
│   └── build.gradle                 # Configuración del motor de construcción Gradle
│
└── frontend/                        # Código de la Interfaz Gráfica (TypeScript)
    ├── public/img/products          # Imágenes de productos
    ├── src/
    │   ├── interfaces/              # Contratos de tipado de dominio del front
    │   ├── pages/                   # Controladores y vistas HTML dinámicas (Login, Admin, Store)
    │   ├── services/                # Capa HTTP (Axios) y mappers de DTO a Dominio
    │   └── utils/                   # Estado de sesión (Storage), Router (Navigate) y UI utils
    ├── package.json                 # Scripts de automatización del ecosistema
    ├── start-ecosystem.js           # Script orquestador multiplataforma de subida
    ├── tsconfig.json                # Configuración del compilador de TypeScript
    └── vite.config.ts               # Configuración del compilador de Vite
```

---

## Instalación y Uso Automático (Un Solo Comando)

El entorno cuenta con un script orquestador en Node.js que levanta el servidor de Spring Boot (Gradle), el servidor de desarrollo de Vite y **abre el navegador de forma automática** en la pantalla de la tienda.

### Requisitos Previos

- **Java Development Kit (JDK):** Versión 21 instalado y configurado en las variables de entorno.
- **Node.js:** Versión 18 o superior.
- **Gestor de paquetes:** `pnpm` (Recomendado por su eficiencia extrema con enlaces duros).

### Pasos para iniciar todo el ecosistema

1.**Instalar pnpm de forma global (si no lo tienes):**

```bash
npm install -g pnpm
```

2.**Posicionarse en la carpeta del Frontend e instalar las dependencias:**

```bash
cd frontend
pnpm install
```

3.**Ejecutar el comando de Orquestación Unificada:**

```bash
pnpm run dev:all
```

#### ¿Qué ocurrirá por detrás?

- El script detectará tu sistema operativo (Windows/Linux/Mac) y lanzará el comando nativo de Gradle (`gradlew.bat bootRun` o `./gradlew bootRun`) en segundo plano dentro de la carpeta `/backend`.
- En paralelo, levantará el compilador en frío de Vite en el puerto `5173`.
- Tras 15 segundos (permitiendo a Spring Boot inicializar el contexto de Hibernate y verificar la semilla de datos de categorías, productos y el usuario administrador), **se abrirá una pestaña automática en tu navegador** en `http://localhost:5173/`.
- **Cierre Limpio:** Al presionar `Ctrl + C` una sola vez en la terminal, el script enviará señales de apagado (`SIGINT`) matando ambos procesos de forma segura sin dejar puertos colgados en memoria.

---

## Datos de Acceso de la Semilla (Modo Administrador)

Para probar las pantallas del **Panel de Administración (CRUD de Categorías, Productos y Dashboard de Métricas)** sin necesidad de registrar una cuenta desde cero, la semilla de datos autoinyecta las credenciales del perfil Administrador Principal:

- **Email:** `em@il.com`
- **Contraseña:** `Passwd123!` _(El sistema procesará el hash SHA-256 `391f6711...` de forma automática en el Login)_
- **Rol asignado:** `ADMIN`
