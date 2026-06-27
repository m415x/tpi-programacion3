import { clientProfileController } from "@pages/client/profile/profile.controller";
import { renderHeader, renderFooter } from "@utils/components";
import { setPageTitle } from "@utils/uiUtils";

/**
 * Función principal asíncrona para inicializar la página del perfil de usuario.
 */
const initProfile = async (): Promise<void> => {
    // Establecer el título de la página
    setPageTitle("Mi Perfil");

    const container = document.querySelector<HTMLElement>("#profile-main-container");

    try {
        // Renderizar componentes base
        renderHeader("#header");
        renderFooter("#footer");

        if (container) {
            // Inicializamos el controlador pasándole el nodo del DOM
            await clientProfileController.init(container);
        }
    } catch (error) {
        console.error("Error catastrófico al inicializar el perfil de usuario:", error);

        // Manejo alternativo por si el servidor Java está caído al renderizar el perfil
        if (container) {
            container.innerHTML = `
                <p class="empty-result">
                    No se pudo conectar con el servicio de gestión de perfil.
                    Por favor, verifique que el servidor backend esté en ejecución.
                </p>
            `;
        }
    }
};

initProfile();
