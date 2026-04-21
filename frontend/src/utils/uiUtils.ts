import { productService as ps } from "@/services/productService";

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
