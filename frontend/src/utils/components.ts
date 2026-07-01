import { UserRole } from "@interfaces/Enums";
import { cartService } from "@services/cart.service";
import { userService } from "@services/user.service";
import { logout } from "@utils/authGuard";
import { PATHS } from "@utils/paths";
import { storage } from "@utils/storage";
import { APP_VERSION, BRAND_NAME } from "@utils/constants";
import type { IUser } from "@interfaces/User.interface";

/**
 * Función para renderizar la card de autenticación (login/register) en el
 * contenedor especificado.
 * @param containerSelector selector del contenedor donde se inyectará la card
 * @param isRegister Booleano que indica si se renderiza el formulario de
 * registro (true) o de login (false)
 */
export const renderAuthCard = (containerSelector: string, isRegister: boolean): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);
    if (!container) return;

    // Definimos los textos dinámicos según el tipo de formulario
    const title: string = isRegister ? "Registrarse" : "Iniciar Sesión";
    const btnText: string = isRegister ? "Registrarse" : "Ingresar";
    const footerText: string = isRegister ? "¿Ya tienes una cuenta?" : "¿No tienes una cuenta?";
    const footerLinkText: string = isRegister ? "Ingresa acá" : "Registrate acá";

    // Inyectamos el HTML de la card en el contenedor
    container.innerHTML = `
    <section class="card auth-card">
        <h1 class="auth-card__title">Food Store</h1>
        <h2 class="auth-card__subtitle">${title}</h2>

        <form id="form" class="auth-card__form">
            ${
                isRegister
                    ? `
            <label for="firstName">Nombre</label>
            <input type="text" name="firstName" id="firstName" autocomplete="off" placeholder="Ingrese su nombre" required>

            <label for="lastName">Apellido</label>
            <input type="text" name="lastName" id="lastName" autocomplete="off" placeholder="Ingrese su apellido" required>

            <label for="phone">Teléfono</label>
            <input type="tel" name="phone" id="phone" autocomplete="off" placeholder="Ingrese su teléfono (opcional)">
            `
                    : ""
            }

            <label for="email">Email</label>
            <input type="email" name="email" id="email" autocomplete="off" placeholder="tucorreo@email.com" required>

            <label for="pass">Contraseña</label>
            <input type="password" name="pass" id="pass" autocomplete="off"
                placeholder="Mínimo 6 caracteres" required>

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
    if (!container) return;

    // Obtenemos el rol y nombre del usuario para personalizar el menú
    const role: UserRole | null = storage.getRole();
    const name: string | undefined = storage.getUser()?.firstName;

    // Obtenemos la ruta actual para determinar qué enlace está activo
    const currentPath: string = window.location.pathname;
    const getActiveClass = (path: string): string => (currentPath.includes(path) ? "link--active" : "");

    // Construimos las partes del menú que dependen del rol
    const dedicatedArea: string =
        role === UserRole.ADMIN
            ? `<li class="menu__item"><a href="${PATHS.ADMIN.HOME}" class="link ${getActiveClass(PATHS.ADMIN.HOME)}">Panel Admin</a></li>`
            : `<li class="menu__item"><a href="${PATHS.CLIENT.ORDERS}" class="link ${getActiveClass(PATHS.CLIENT.ORDERS)}">Mis Pedidos</a></li>`;
    const userAreaMenu: string =
        role === UserRole.CLIENT
            ? `<li class="menu__item menu__item--cart"><a href="${PATHS.STORE.CART}" class="link ${getActiveClass(PATHS.STORE.CART)}">Carrito</a></li>
               <li class="menu__item"><a href="${PATHS.CLIENT.PROFILE}" class="link ${getActiveClass(PATHS.CLIENT.PROFILE)}">${name}</a></li>`
            : `<li class="menu__item"><a href="${PATHS.ADMIN.PROFILE}" class="link ${getActiveClass(PATHS.ADMIN.PROFILE)}">${name}</a></li>`;

    // Inyectamos el HTML del Header en el contenedor
    container.innerHTML = `
    <div class="header__content">
        <h1>${BRAND_NAME}</h1>
        <nav class="menu">
        <ul class="menu__list">
            <li class="menu__item"><a href="${PATHS.STORE.HOME}" class="link ${getActiveClass(PATHS.STORE.HOME)}">Tienda</a></li>
            ${dedicatedArea}
            ${userAreaMenu}
        </ul>
        <button id="logoutButton" class="btn btn--secondary">Cerrar Sesión</button>
        </nav>
    </div>
    `;

    // Actualizamos el badge del carrito
    updateCartBadge();

    // Agregamos el listener para el botón de logout
    const btnLogout = document.querySelector<HTMLButtonElement>("#logoutButton");
    if (!btnLogout) return;

    btnLogout.addEventListener("click", (): void => logout());
};

/**
 * Función para actualizar el badge del carrito en el menú cada vez que se
 * modifica el carrito (agregar/quitar productos). Si el carrito está vacío,
 * se elimina el badge.
 */
export const updateCartBadge = (): void => {
    const cartLink = document.querySelector<HTMLLinkElement>(".menu__item--cart a");
    if (!cartLink) return;

    // Obtenemos la cantidad total de items en el carrito
    const totalQty: number = cartService.getTotalQuantity();

    // Buscamos el badge existente (si lo hay)
    let badge = cartLink.parentElement?.querySelector<HTMLElement>(".menu__item-badge");

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
export const renderAside = (containerSelector: string, callback?: () => void): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);
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
    if (!container) return;

    // Obtenemos el rol del usuario para personalizar el contenido del Footer
    const role = storage.getRole();
    const path = window.location.pathname;

    // Definimos el contenido del Footer
    const poweredBy = '<p>Powered by <a href="https://github.com/m415x" target="_blank">Cristian Lahoz</a></p>';

    // Contenido del Footer según el rol
    const clientInfo = `
        <div class="footer__content">
            <p>&copy; 2026 ${BRAND_NAME}. Todos los derechos reservados.</p>
            <p>Contacto: <a href="mailto:info@foodstore.com" target="_blank">info@foodstore.com</a></p>
            ${poweredBy}
        </div>
    `;

    const adminInfo = `
        <div class="footer__content footer__content--admin">
            <p>Panel de Control ${APP_VERSION}</p>
            <p>Sesión iniciada como: <strong>${role}</strong></p>
            ${poweredBy}
        </div>
    `;

    // Inyectamos el HTML del Footer en el contenedor
    container.innerHTML = `
        ${path.includes("/admin/") ? adminInfo : clientInfo}
    `;
};

/**
 * Función para inyectar el favicon mediante Data URI para optimizar la carga
 */
export const initFavicon = (): void => {
    const faviconUri: string =
        "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Ctext x='50%25' y='50%25' dominant-baseline='central' text-anchor='middle' font-family='Roboto, sans-serif' font-size='60' font-weight='bold' fill='%23ff6347'%3EFS%3C/text%3E%3C/svg%3E";

    let link: HTMLLinkElement | null = document.querySelector("link[rel~='icon']");

    if (!link) {
        link = document.createElement("link") as HTMLLinkElement;
        link.rel = "icon";
        document.head.appendChild(link);
    }

    link.type = "image/svg+xml";
    link.href = faviconUri;
};

/**
 * Componente para renderizar el formulario de perfil de usuario.
 */
export const profileComponent = {
    /**
     * Renderiza e inicializa el formulario de perfil en el contenedor provisto.
     */
    async render(targetContainer: HTMLElement): Promise<void> {
        // 1. Recuperamos el usuario activo desde el storage local
        const currentUser: IUser | null = storage.getUser();
        if (!currentUser) {
            targetContainer.innerHTML = `<div class="text-center error-text"><p>No se encontró una sesión activa de usuario.</p></div>`;
            return;
        }

        try {
            // Buscamos los datos reales y frescos del backend
            storage.setUser(await userService.getProfile()); // Mantenemos el storage al día
        } catch (error) {
            console.warn("No se pudo refrescar el perfil desde el servidor, usando datos locales.");
        }

        // 2. Inyectamos la estructura del formulario reutilizando tus clases semánticas
        targetContainer.innerHTML = `
            <div class="admin-container">
                <div class="admin-header-row">
                    <div>
                        <h2>Mi Perfil de Usuario</h2>
                        <p class="form-help">Visualizá y modificá tus datos personales de contacto</p>
                    </div>
                </div>

                <div class="form-container profile-card">
                    <form id="profile-form" novalidate>

                        <div class="form-group-grid">
                            <div class="form-group">
                                <label for="profile-firstname">Nombre</label>
                                <input type="text" id="profile-firstname" value="${currentUser.firstName}" required placeholder="Tu nombre">
                            </div>
                            <div class="form-group">
                                <label for="profile-lastname">Apellido</label>
                                <input type="text" id="profile-lastname" value="${currentUser.lastName}" required placeholder="Tu apellido">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="profile-phone">Teléfono de Contacto</label>
                            <input type="tel" id="profile-phone" value="${currentUser.phone || ""}" placeholder="Ej: 2644111222">
                        </div>

                        <div class="form-group-grid">
                            <div class="form-group">
                                <label for="profile-email">Dirección de Email</label>
                                <input type="email" id="profile-email" value="${currentUser.email}" required placeholder="Tu email">
                            </div>
                            <div class="form-group">
                                <label for="profile-password">Contraseña</label>
                                <input type="password" id="profile-password" value="" placeholder="Tu nueva contraseña">
                            </div>
                        </div>

                        <div class="form-actions-profile">
                            <button type="submit" class="btn btn--tertiary btn-block" id="btn-profile-submit">
                                Actualizar Mis Datos
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        `;

        // 3. Enlazamos los eventos del formulario
        const form = targetContainer.querySelector<HTMLFormElement>("#profile-form")!;
        form.addEventListener("submit", async (e: Event) => {
            e.preventDefault();

            const inputFirstName = targetContainer.querySelector<HTMLInputElement>("#profile-firstname")!;
            const inputLastName = targetContainer.querySelector<HTMLInputElement>("#profile-lastname")!;
            const inputPhone = targetContainer.querySelector<HTMLInputElement>("#profile-phone")!;
            const inputEmail = targetContainer.querySelector<HTMLInputElement>("#profile-email")!;
            const inputPass = targetContainer.querySelector<HTMLInputElement>("#profile-password")!;

            const firstName = inputFirstName.value.trim();
            const lastName = inputLastName.value.trim();
            const phone = inputPhone.value.trim();
            const email = inputEmail.value.trim();
            const password = inputPass.value.trim();

            // Validamos

            if (!firstName || !lastName || !email) {
                alert("Por favor, completá los campos obligatorios (Nombre, Apellido y Email).");
                return;
            }

            if (!userService.validateEmail(email)) {
                alert("El email ingresado no es válido.");
                return;
            }

            if (password && !userService.validatePasswordStrength(password)) {
                alert("La contraseña debe tener al menos 6 caracteres, una letra mayúscula y un número.");
                return;
            }

            try {
                // Armamos el payload base con los datos obligatorios de contacto
                const profilePayload: any = {
                    firstName,
                    lastName,
                    phone,
                    email,
                };

                // Si escribió algo y pasó el filtro, se la mandamos.
                // Si lo dejó en blanco, la propiedad no se envía.
                if (password.length > 0) {
                    profilePayload.password = password;
                }

                // Ejecutamos el PATCH enviando el payload dinámico limpio
                const updatedUser = await userService.updateProfile(profilePayload);

                // Sincronizamos la memoria local con los nuevos datos devueltos por el servidor
                storage.setUser(updatedUser);

                alert("¡Perfil actualizado con éxito en la base de datos!");

                // Vaciamos el input explícitamente para que no queden los caracteres ocultos
                inputPass.value = "";

                // Volvemos a disparar el render del perfil para que actualice la UI de manera limpia
                this.render(targetContainer);

                // Forzamos un refresco parcial del encabezado para pintar el nuevo nombre si cambió
                const headerContainer = document.querySelector<HTMLElement>("#header");
                if (headerContainer) {
                    renderHeader("#header");
                }
            } catch (error: any) {
                console.error("Error al guardar perfil de usuario:", error);
                alert(error.response?.data?.message || "No se pudieron guardar las modificaciones.");
            }
        });
    },
};
