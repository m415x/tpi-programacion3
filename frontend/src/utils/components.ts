import { storage } from "@utils/storage";
import { logout } from "@utils/auth";
import type { ICartItem } from "@/types/ICartItem";
import { PATHS } from "@utils/paths";

// Función para renderizar la tarjeta de autenticación (Login/Registro)
export const renderAuthCard = (
    containerId: string,
    isRegister: boolean,
): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    if (!container) return;

    const title: string = isRegister ? "Registrarse" : "Iniciar Sesión";
    const btnText: string = isRegister ? "Registrarse" : "Ingresar";
    const footerText: string = isRegister
        ? "¿Ya tienes una cuenta?"
        : "¿No tienes una cuenta?";
    const footerLinkText: string = isRegister
        ? "Ingresa acá"
        : "Registrate acá";

    container.innerHTML = `
      <section class="card auth-card">
        <h1 class="auth-card__title">Food Store</h1>
        <h2 class="auth-card__subtitle">${title}</h2>

        <form id="form" class="auth-card__form">
          ${
              isRegister
                  ? `
          <label for="name">Nombre</label>
          <input type="text" name="name" id="name" autocomplete="off" placeholder="Ingrese su nombre completo" required>
        `
                  : ""
          }

          <label for="email">Email</label>
          <input type="email" name="email" id="email" autocomplete="off" placeholder="tucorreo@email.com" required>

          <label for="pass">Contraseña</label>
          <input type="password" name="pass" id="pass" autocomplete="off"
            placeholder="Mínimo 8 caracteres" required>

          <button class="btn btn--primary" type="submit">${btnText}</button>
        </form>

        <p class="auth-card__switch-link">
            ${footerText} <a href="#" id="auth-switch-link">${footerLinkText}</a>
        </p>
      </section>
    `;
};

// Función para renderizar el Header estándar con menú y botón de logout
export const renderHeader = (containerId: string): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    if (!container) return;

    const role = storage.getRole();
    const name = storage.getUser()?.name;
    const path = window.location.pathname;

    // Determinamos en qué sección estamos
    const isAdminArea = path.includes("/admin/");
    const isClientArea = path.includes("/client/");

    container.innerHTML = `
    <div class="header__content">
        <h1>Food Store</h1>
        <nav class="menu">
        <ul class="menu__list">
            <li class="menu__item"><a href="${PATHS.STORE.HOME}">Inicio</a></li>
            <li class="menu__item"><a href="#">Mis Pedidos</a></li>
            <li class="menu__item menu__item--cart"><a href="${PATHS.STORE.CART}">Carrito</a></li>
            ${
                role === "admin" && !isAdminArea
                    ? `<li class="menu__item menu__item--admin"><a href="${PATHS.ADMIN.HOME}">${name}</a></li>`
                    : ""
            }
            
            ${
                role === "client" && !isClientArea
                    ? `<li class="menu__item menu__item--client"><a href="${PATHS.CLIENT.HOME}">${name}</a></li>`
                    : ""
            }
            
            ${
                isAdminArea || isClientArea
                    ? `<li class="menu__item menu__item--return-to-store"><a href="${PATHS.STORE.HOME}">Volver a la tienda</a></li>`
                    : ""
            }
        </ul>
        <button id="logoutButton" class="btn btn--secondary">Cerrar Sesión</button>
        </nav>
    </div>
    `;

    // Actualizamos el badge del carrito
    updateCartBadge();

    // Asignamos el evento de logout una vez inyectado el HTML
    const btnLogout = document.getElementById(
        "logoutButton",
    ) as HTMLButtonElement | null;

    btnLogout?.addEventListener("click", (): void => logout());
};

// Función para actualizar el badge del carrito en el menú cada vez que se modifica el carrito
export const updateCartBadge = (): void => {
    // 1. Buscamos el contenedor del badge
    const cartLink = document.querySelector(
        ".menu__item--cart a",
    ) as HTMLLinkElement | null;
    if (!cartLink) return;

    // 2. Calculamos la cantidad actual
    const cartItems: ICartItem[] = storage.getCartItems();
    const totalQty: number = cartItems.reduce(
        (acc: number, item: ICartItem): number => acc + item.qty,
        0,
    );

    // 3. Buscamos si ya existe el badge
    let badge = cartLink.parentElement?.querySelector(
        ".menu__item-badge",
    ) as HTMLElement | null;

    if (totalQty > 0) {
        if (!badge) {
            // Si no existe y hay items, lo creamos
            badge = document.createElement("span");
            badge.classList.add("menu__item-badge");
            cartLink.parentElement?.appendChild(badge);
        }
        badge.textContent = totalQty.toString();
    } else {
        // Si no hay items, lo eliminamos si existía
        badge?.remove();
    }
};

// Función para renderizar el Aside con la estructura base
export const renderAside = (
    containerId: string,
    callback?: () => void,
): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    if (!container) return;

    // Limpiamos el contenedor
    container.innerHTML = "";

    // Ejecutamos la función inyectada para renderizar el contenido específico
    if (callback) {
        callback();
    }
};

// Función para renderizar el Footer estándar
export const renderFooter = (containerId: string): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    if (!container) return;

    const role = storage.getRole();
    const path = window.location.pathname;

    const clientInfo = `
        <div class="footer__content">
            <p>&copy; 2026 Food Store. Todos los derechos reservados.</p>
            <p>Contacto: <a href="mailto:info@foodstore.com" target="_blank">info@foodstore.com</a></p>
            <p>Powered by <a href="https://github.com/m415x" target="_blank">Cristian Lahoz</a></p>
        </div>
    `;

    const adminInfo = `
        <div class="footer__content footer__content--admin">
            <p>Panel de Control v1.0</p>
            <p>Sesión iniciada como: <strong>${role}</strong></p>
            <p>Powered by <a href="https://github.com/m415x" target="_blank">Cristian Lahoz</a></p>
        </div>
    `;

    container.innerHTML = `
        ${path.includes("/admin/") ? adminInfo : clientInfo}
    `;
};
