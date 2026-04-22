/**
 * Función para navegar a una ruta específica dentro de la aplicación.
 * @param route La ruta a la que se desea navegar (ejemplo: "/store/home").
 */
export const navigate = (route: string): void => {
    window.location.replace(route);
};
