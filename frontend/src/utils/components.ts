import { Role } from "@interfaces/Role";
import { cartService } from "@services/cartService";
import { logout } from "@utils/authGuard";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";

/**
 * Función para renderizar la card de autenticación (login/register) en el
 * contenedor especificado.
 * @param containerSelector selector del contenedor donde se inyectará la card
 * @param isRegister Booleano que indica si se renderiza el formulario de
 * registro (true) o de login (false)
 */
export const renderAuthCard = (
    containerSelector: string,
    isRegister: boolean,
): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!container) return;

    // Definimos los textos dinámicos según el tipo de formulario
    const title: string = isRegister ? "Registrarse" : "Iniciar Sesión";
    const btnText: string = isRegister ? "Registrarse" : "Ingresar";
    const footerText: string = isRegister
        ? "¿Ya tienes una cuenta?"
        : "¿No tienes una cuenta?";
    const footerLinkText: string = isRegister
        ? "Ingresa acá"
        : "Registrate acá";

    // Inyectamos el HTML de la card en el contenedor
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

/**
 * Función para renderizar el Header estándar con el menú de navegación.
 * @param containerSelector selector del contenedor donde se inyectará el Header
 */
export const renderHeader = (containerSelector: string): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!container) return;

    // Obtenemos el rol y nombre del usuario para personalizar el menú
    const role: Role | null = storage.getRole();
    const name: string | undefined = storage.getUser()?.name;

    // Obtenemos la ruta actual para determinar qué enlace está activo
    const currentPath: string = window.location.pathname;
    const getActiveClass = (path: string): string =>
        currentPath.includes(path) ? "link--active" : "";

    // Construimos las partes del menú que dependen del rol
    const adminMenu =
        role === Role.ADMIN
            ? `<li class="menu__item menu__item--admin"><a href="${PATHS.ADMIN.HOME}" class="link ${getActiveClass(PATHS.ADMIN.HOME)}">Admin</a></li>`
            : "";
    const userAreaMenu =
        role === Role.CLIENT
            ? `<li class="menu__item menu__item--client"><a href="${PATHS.CLIENT.HOME}" class="link ${getActiveClass(PATHS.CLIENT.HOME)}">${name}</a></li>`
            : `<li class="menu__item menu__item--client"><a href="${PATHS.ADMIN.HOME}" class="link ${getActiveClass(PATHS.ADMIN.HOME)}">${name}</a></li>`;

    // Inyectamos el HTML del Header en el contenedor
    container.innerHTML = `
    <div class="header__content">
        <h1>Food Store</h1>
        <nav class="menu">
        <ul class="menu__list">
            <li class="menu__item"><a href="${PATHS.STORE.HOME}" class="link ${getActiveClass(PATHS.STORE.HOME)}">Tienda</a></li>
            <li class="menu__item"><a href="#" class="link ${getActiveClass("#")}">Mis Pedidos</a></li>
            ${adminMenu}
            <li class="menu__item menu__item--cart"><a href="${PATHS.STORE.CART}" class="link ${getActiveClass(PATHS.STORE.CART)}">Carrito</a></li>
            ${userAreaMenu}
        </ul>
        <button id="logoutButton" class="btn btn--secondary">Cerrar Sesión</button>
        </nav>
    </div>
    `;

    // Actualizamos el badge del carrito
    updateCartBadge();

    // Agregamos el listener para el botón de logout
    const btnLogout =
        document.querySelector<HTMLButtonElement>("#logoutButton");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!btnLogout) return;

    btnLogout.addEventListener("click", (): void => logout());
};

/**
 * Función para actualizar el badge del carrito en el menú cada vez que se
 * modifica el carrito (agregar/quitar productos). Si el carrito está vacío,
 * se elimina el badge.
 */
export const updateCartBadge = (): void => {
    const cartLink = document.querySelector<HTMLLinkElement>(
        ".menu__item--cart a",
    );

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!cartLink) return;

    // Obtenemos la cantidad total de items en el carrito
    const totalQty: number = cartService.getTotalQuantity();

    // Buscamos el badge existente (si lo hay)
    let badge =
        cartLink.parentElement?.querySelector<HTMLElement>(".menu__item-badge");

    // Si hay items, actualizamos el badge
    if (totalQty > 0) {
        // Si no existe y hay items, lo creamos
        if (!badge) {
            badge = document.createElement("span") as HTMLElement;
            badge.classList.add("menu__item-badge");
            cartLink.parentElement?.appendChild(badge);
        }
        badge.textContent = totalQty.toString();
    } else if (badge) {
        // Si no hay items, lo eliminamos si existía
        badge.remove();
    }
};

/**
 * Función para renderizar el contenido específico del Aside en el contenedor
 * @param containerSelector selector del contenedor donde se inyectará el contenido del Aside
 * @param callback Función que inyecta el contenido específico del Aside (opcional)
 */
export const renderAside = (
    containerSelector: string,
    callback?: () => void,
): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!container) return;

    // Limpiamos el contenedor
    container.innerHTML = "";

    // Ejecutamos la función inyectada para renderizar el contenido específico
    if (callback) {
        callback();
    }
};

/**
 * Función para renderizar el Footer
 * @param containerSelector selector del contenedor donde se inyectará el Footer
 */
export const renderFooter = (containerSelector: string): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!container) return;

    // Obtenemos el rol del usuario para personalizar el contenido del Footer
    const role = storage.getRole();
    const path = window.location.pathname;

    // Definimos el contenido del Footer
    const poweredBy =
        '<p>Powered by <a href="https://github.com/m415x" target="_blank">Cristian Lahoz</a></p>';

    // Contenido del Footer según el rol
    const clientInfo = `
        <div class="footer__content">
            <p>&copy; 2026 Food Store. Todos los derechos reservados.</p>
            <p>Contacto: <a href="mailto:info@foodstore.com" target="_blank">info@foodstore.com</a></p>
            ${poweredBy}
        </div>
    `;

    const adminInfo = `
        <div class="footer__content footer__content--admin">
            <p>Panel de Control v1.0</p>
            <p>Sesión iniciada como: <strong>${role}</strong></p>
            ${poweredBy}
        </div>
    `;

    // Inyectamos el HTML del Footer en el contenedor
    container.innerHTML = `
        ${path.includes("/admin/") ? adminInfo : clientInfo}
    `;
};
