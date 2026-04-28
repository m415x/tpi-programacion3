# Proyecto: Food Store

## ✍️ Descripción

Este es un proyecto de demostración creado con fines educativos para ilustrar el desarrollo de un frontend para un E-commerce de comida utilizando **Vite**, **TypeScript Vanilla** (manipulación directa del DOM) y CSS puro.

El objetivo es mostrar la implementación de funcionalidades típicas de una tienda en línea (catálogo de productos, filtrado, búsqueda, carrito de compras) combinadas con un mecanismo básico de protección de rutas según el rol del usuario (`ADMIN` o `CLIENT`).

---

## ⚠️ ¡Importante! Nivel de Seguridad

La protección de rutas y la gestión de sesiones implementadas en este proyecto **NO SON SEGURAS** y no deben utilizarse en un entorno de producción.

- **Razón**: La lógica de autenticación, almacenamiento de usuarios y estado del carrito se basan completamente en datos guardados en el `localStorage` del navegador del usuario.
- **Riesgo**: Cualquier usuario con conocimientos técnicos básicos puede abrir las herramientas de desarrollador del navegador para inspeccionar, modificar o eliminar los datos de `localStorage` (como forzar el rol a `ADMIN`), obteniendo acceso no autorizado a rutas protegidas.

Este enfoque es útil únicamente para fines de aprendizaje, lógica de estado frontend y creación de prototipos de bajo riesgo. La seguridad y persistencia real deben implementarse en un **backend** con base de datos y tokens seguros.

---

## 🚀 Instalación y Uso

Se recomienda usar `pnpm` como gestor de paquetes para mayor eficiencia en el manejo de dependencias.

### 1. Instalar pnpm

Si no tienes `pnpm` instalado, puedes hacerlo fácilmente a través de `npm` (que viene con Node.js) ejecutando el siguiente comando en tu terminal:

```bash
npm install -g pnpm
```

### 2. Instalar Dependencias del Proyecto

Una vez en la carpeta `frontend` del proyecto, instala las dependencias necesarias con `pnpm`:

```bash
pnpm install
```

### 3. Ejecutar el Proyecto

Para iniciar el servidor de desarrollo de Vite, ejecuta:

```bash
pnpm dev
```

La aplicación estará disponible en la URL que aparezca en la terminal (generalmente `http://localhost:5173`).

---

## ⚙️ ¿Cómo Funciona la Arquitectura?

El proyecto utiliza una arquitectura basada en servicios y utilidades para separar la lógica de negocio de la interfaz gráfica:

1. **Gestión de Estado (`src/utils/storage.ts`)**: Centraliza el acceso a `localStorage` para manejar los usuarios registrados, la sesión actual y los ítems del carrito de compras.
2. **Servicios (`src/services/`)**: Contienen la lógica de negocio pura, como la encriptación de contraseñas (`authService.ts`), la lógica de suma/resta del carrito (`cartService.ts`) y el filtrado de productos (`productService.ts`).
3. **Protección de Rutas (`src/utils/authGuard.ts`)**: Cada vez que se intenta cargar una página, se ejecuta `checkAuth()`. Esta función comprueba el rol guardado en `localStorage` y redirige al usuario si no tiene los permisos necesarios para la ruta actual.
4. **Renderizado Dinámico**: Las páginas (ej. `home.controller.ts` o `productDetail.controller.ts`) leen los datos y construyen los elementos HTML (`document.createElement`) de forma dinámica, inyectándolos en el DOM.

---

## 📁 Estructura del Proyecto

```text
/src
├── data/         # Datos simulados (Mock data de productos)
├── pages/        # Controladores y vistas de la aplicación
│   ├── admin/    # Panel de control (Solo administradores)
│   ├── auth/     # Páginas de Login y Registro
│   ├── client/   # Área personal del cliente
│   └── store/    # Tienda (Home, Detalle de Producto, Carrito)
├── services/     # Lógica de negocio separada de la UI
├── types/        # Definición de tipos de TypeScript (Product, IUser, ICartItem, Role)
├── utils/        # Utilidades compartidas (storage, authGuard, componentes de UI)
└── style.css     # Estilos globales y variables CSS
```
