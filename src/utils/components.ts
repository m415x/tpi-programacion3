import { sessionStore } from "@utils/sessionStore";
import { logout } from "@utils/auth";

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
      <section class="auth-card">
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
          <input type="password" name="pass" id="pass" minlength="8" autocomplete="off"
            placeholder="Mínimo 8 caracteres" required>

          <button class="btn btn__primary" type="submit">${btnText}</button>
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

    const role = sessionStore.getRole();
    const name = sessionStore.getUser()?.name;
    const path = window.location.pathname;

    // Determinamos en qué sección estamos
    const isAdminArea = path.includes("/admin/");
    const isClientArea = path.includes("/client/");

    container.innerHTML = `
    <h1>Food Store</h1>
    <nav class="menu">
      <ul class="menu__list">
        <li class="menu__item"><a href="/src/pages/store/home/home.html">Inicio</a></li>
        <li class="menu__item"><a href="#">Mis Pedidos</a></li>
        <li class="menu__item"><a href="#">Carrito</a></li>
        ${
            role === "admin" && !isAdminArea
                ? `<li class="menu__item menu__item--admin"><a href="/src/pages/admin/home/home.html">${name}</a></li>`
                : ""
        }
          
          ${
              role === "client" && !isClientArea
                  ? `<li class="menu__item menu__item--client"><a href="/src/pages/client/home/home.html">${name}</a></li>`
                  : ""
          }
          
          ${
              isAdminArea || isClientArea
                  ? '<li class="menu__item menu__item--return-to-store"><a href="/src/pages/store/home/home.html">Volver a la tienda</a></li>'
                  : ""
          }
      </ul>
      <button id="logoutButton" class="btn btn__secondary">Logout</button>
    </nav>
    `;

    // Asignamos el evento de logout una vez inyectado el HTML
    const btnLogout = document.getElementById(
        "logoutButton",
    ) as HTMLButtonElement | null;

    btnLogout?.addEventListener("click", (): void => logout());
};

// Función para renderizar el Aside con la estructura base
export const renderAside = (containerId: string): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    if (!container) return;

    container.innerHTML = `
    <h2>Categorías</h2>
    <ul id="lista-categorias">
      <!-- Inyección dinámica de categorías -->
    </ul>
    `;
};

// Función para renderizar el Footer estándar
export const renderFooter = (containerId: string): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    if (!container) return;

    container.innerHTML = `
    <p>© 2026 Food Store. Todos los derechos reservados.</p>
    <p>Contacto: <a href="mailto:info@foodstore.com" target="_blank">info@foodstore.com</a></p>
    `;
};
