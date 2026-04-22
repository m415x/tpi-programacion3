/**
 * Este módulo contiene funciones de utilidad para la interfaz de usuario
 * relacionadas con la visualización de productos, formateo de precios y
 * generación de enlaces a detalles de productos. Estas funciones abstraen la
 * lógica de presentación y permiten una separación clara entre la lógica de
 * datos (servicios) y la lógica de interfaz (manipulación del DOM). Esto
 * facilita el mantenimiento y la reutilización del código en diferentes partes
 * de la aplicación.
 */
import { productService as ps } from "@/services/productService";
import { PATHS } from "@utils/paths";

/**
 * Busca la imagen de un producto aleatorio en la API y la asigna al elemento
 * <img> correspondiente.
 * @param id ID del producto para localizar el elemento en el DOM.
 */
export const updateProductImageUI = (id: number): void => {
    // 1. Obtenemos la URL desde el servicio (Lógica de datos)
    const url: string = ps.getPersistentImage(id);

    // 2. Localizamos el elemento en el DOM (Lógica de interfaz)
    const imgElement = document.getElementById(
        `img-product-${id}`,
    ) as HTMLImageElement;

    if (imgElement) {
        imgElement.src = url;
    }
};

/**
 * Formatea un precio numérico a un string con formato de moneda argentina, y lo
 * divide en parte entera y decimal.
 * @param price Precio a formatear.
 * @returns Un HTML string con la parte entera y decimal envueltas en spans para
 * estilos separados.
 */
export const formattedPriceHTML = (price: number): string => {
    const priceString: string = price.toLocaleString("es-AR", {
        style: "currency",
        currency: "ARS",
    });
    const [wholePrice, decimalPrice]: string[] = priceString.split(",");
    return `
    <span class="price--whole">${wholePrice}</span><span class="price--decimal">${decimalPrice}</span>
    `;
};

/**
 * Envuelve un fragmento de HTML con un enlace al detalle del producto.
 * @param id ID del producto.
 * @param content Contenido HTML a envolver.
 * @returns String HTML del enlace.
 */
export const wrapWithDetailLink = (id: number, content: string): string => {
    return `<a href="${PATHS.STORE.DETAIL(id)}" class="link">${content}</a>`;
};

/**
 * Genera el atributo 'disabled' o una clase CSS para indicar que un control no está disponible.
 * @param isAvailable Booleano que indica si el control debe estar activo.
 * @param classSelector Opcional. Si se proporciona, se devuelve la clase CSS en
 * lugar del atributo 'disabled'.
 * @returns Un string con el atributo 'disabled' y/o la clase CSS.
 */
export const getDisabledState = (
    isAvailable: boolean,
    classSelector?: string,
): string => {
    if (classSelector) {
        return !isAvailable ? `${classSelector}` : "";
    }
    return !isAvailable ? "disabled" : "";
};
