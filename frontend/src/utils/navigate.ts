/**
 * Función para navegar a una ruta específica dentro de la aplicación.
 * @param route La ruta a la que se desea navegar (ejemplo: "/store/home").
 */
export const navigate = (route: string): void => {
    window.location.href = route;
};

/**
 * Función para navegar a una ruta específica dentro de la aplicación sin
 * agregar una nueva entrada en el historial del navegador.
 * @param route La ruta a la que se desea navegar (ejemplo: "/store/cart").
 */
export const navigateClear = (route: string): void => {
    window.location.replace(route);
};
