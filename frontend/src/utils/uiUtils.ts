import { PATHS } from "@utils/paths";
import { productService as ps } from "@services/productService";

/**
 * Busca la imagen de un producto aleatorio en la API y la asigna al elemento
 * <img> correspondiente.
 * @param id ID del producto para localizar el elemento en el DOM.
 */
export const updateProductImageUI = (id: number): void => {
    // Obtener URL de la imagen desde el servicio
    const url: string = ps.getPersistentImage(id);

    // Actualizar el src del elemento <img> con el ID específico
    const imgElement = document.querySelector<HTMLImageElement>(
        `#img-product-${id}`,
    );

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!imgElement) return;

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
    // Formatear el precio a moneda argentina
    const priceString: string = price.toLocaleString("es-AR", {
        style: "currency",
        currency: "ARS",
    });

    // Dividir el precio en parte entera y decimal
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
    // Si no está disponible y se pasó una clase CSS, devolver esa clase.
    if (!isAvailable && classSelector) {
        return classSelector;
    }

    // Si no, devolver "disabled" como atributo.
    return !isAvailable ? "disabled" : "";
};
