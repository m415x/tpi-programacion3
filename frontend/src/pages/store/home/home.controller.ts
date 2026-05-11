import type { ICategory } from "@interfaces/ICategory";
import type { Product } from "@interfaces/Product";
import { productService } from "@services/productService";
import { updateCartBadge } from "@utils/components";
import { storage } from "@utils/storage";
import {
    formattedPriceHTML,
    getItemAvailability,
    showCartNotice,
    updateBaseAvailabilityUI,
    updateProductImageUI,
    wrapWithDetailLink,
} from "@utils/uiUtils";

/**
 * Función para mostrar los productos en el contenedor principal de la tienda
 * @param products el array de productos a mostrar en la interfaz
 */
export const showProducts = (products: Product[]): void => {
    const productContainer =
        document.querySelector<HTMLElement>("#product-container");
    if (!productContainer) return;

    const productQty =
        document.querySelector<HTMLParagraphElement>("#product-qty");
    if (!productQty) return;

    // Limpiamos el contenedor antes de mostrar los productos para evitar duplicados
    productContainer.innerHTML = "";

    // Creamos un fragmento para optimizar la inserción de múltiples elementos en el DOM
    const fragment: DocumentFragment = document.createDocumentFragment();

    // Filtramos los productos activos (disponibles y con stock) para mostrar la
    // cantidad correcta en el título
    const activeProducts: Product[] =
        productService.getActiveProducts(products);

    // Actualizamos el texto de cantidad de productos encontrados
    if (productQty) {
        if (activeProducts.length === 0) {
            productQty.textContent = "";
        } else if (activeProducts.length === 1) {
            productQty.textContent = `${activeProducts.length} producto`;
        } else if (productQty) {
            productQty.textContent = `${activeProducts.length} productos`;
        }
    }

    if (activeProducts.length > 0) {
        activeProducts.forEach((prod: Product): void => {
            // Creamos los nodos uno por uno
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "product__card");
            article.dataset.id = prod.id.toString();

            // Formateamos el precio
            const unitPrice: string = formattedPriceHTML(prod.precio);

            // Obtenemos la categoría principal para mostrar en la card (si tiene)
            const categoryName =
                prod.categorias.length > 0 && prod.categorias[0]
                    ? prod.categorias[0].nombre
                    : "Sin categoría";

            // Obtenemos el estado de disponibilidad para mostrar en la card
            const { available, isAvailable, qtyInCart } =
                getItemAvailability(prod);

            // Creamos el HTML del producto, incluyendo la imagen con carga asíncrona y los enlaces a detalle
            const linkedImg = wrapWithDetailLink(
                prod.id,
                `<img class="product__img" src="" id="img-product-${prod.id}" alt="${prod.nombre}">`,
            );

            // El nombre del producto también se envuelve en un enlace a detalle
            const linkedName = wrapWithDetailLink(
                prod.id,
                `<h3 class="product__name">${prod.nombre}</h3>`,
            );

            article.innerHTML = `
            ${linkedImg}
            <p class="product__stock stock-badge"></p>
            <div class="product__content">
                <div class="product__body">
                    <p class="product__category">${categoryName}</p>
                    ${linkedName}
                    <p class="product__description">${prod.descripcion}</p>
                </div>
                <div class="product__foot">
                    <p class="price product__price">${unitPrice}</p>
                    <button class="btn btn--primary btn--add-product"></button>
                </div>
            </div>
            `;

            // Agregamos la card al fragmento en lugar de al DOM directamente para mejorar el rendimiento
            fragment.appendChild(article);

            // Sincronizamos el estado inicial de la card
            updateProductCardUI(article, isAvailable);

            // Iniciamos la carga de la imagen
            updateProductImageUI(prod.id, article);

            const btnAdd =
                article.querySelector<HTMLButtonElement>(".btn--add-product");
            if (!btnAdd) return;

            btnAdd.addEventListener("click", (): void => {
                if (storage.updateCartItem(prod.id)) {
                    const status = getItemAvailability(prod);

                    updateProductCardUI(article, status.isAvailable);

                    // Actualizamos el badge del carrito
                    updateCartBadge();

                    // Mostrar el aviso debajo de la searchBar
                    const searchBar =
                        document.querySelector<HTMLElement>(".search-bar");
                    if (searchBar) {
                        showCartNotice(searchBar, 1, prod.nombre, "after");
                    }

                    if (!status.isAvailable) {
                        alert("¡Has añadido la última unidad!");
                    }
                }
            });
        });

        // Una vez que hemos creado todas las cards en el fragmento, lo agregamos
        // al contenedor de productos en el DOM de una sola vez
        productContainer.appendChild(fragment);
    } else {
        const emptyResult: HTMLElement = document.createElement("p");

        emptyResult.classList.add("empty-result");
        emptyResult.textContent = "No se encontraron productos";
        productContainer?.appendChild(emptyResult);
    }
};

/**
 * Función para actualizar el estado de la card del producto (disponible/agotado) según su stock y lo que hay en el carrito
 * @param container el elemento HTML de la card del producto a actualizar
 * @param isAvailable booleano que indica si el producto está disponible para agregar al carrito
 */
const updateProductCardUI = (
    container: HTMLElement,
    isAvailable: boolean,
): void => {
    // Actualizamos el badge de stock y el botón de agregar al carrito
    updateBaseAvailabilityUI(container, isAvailable, {
        badge: ".product__stock",
        button: ".btn--add-product",
    });

    const badge = container.querySelector<HTMLElement>(".product__stock");
    if (badge) badge.textContent = isAvailable ? "Disponible" : "Agotado";
};

/**
 * Función para mostrar la barra de búsqueda y el filtro por categorías, y manejar sus eventos
 * @param product el array de productos sobre los cuales realizar la búsqueda
 * @param category el array de categorías para poblar el selector de filtros
 */
export const showSearchBar = (
    products: Product[],
    categories: ICategory[],
): void => {
    const inputSearch =
        document.querySelector<HTMLInputElement>("#input-search");
    const selectCategories =
        document.querySelector<HTMLSelectElement>("#select-categories");
    if (!inputSearch || !selectCategories) return;

    // Variable para manejar el debounce de la búsqueda
    let debounceTimer: number;

    // Función para manejar los filtros de búsqueda y categoría, delegando la lógica
    // al servicio y actualizando la UI
    const handleFilters = (): void => {
        // Capturamos los valores actuales de la UI
        const searchTerm: string = inputSearch.value || "";
        const categoryId: string = selectCategories.value || "all";

        // Delegamos la lógica al servicio
        const filteredProducts = productService.applyFilters(products, {
            searchTerm,
            categoryId,
        });

        showProducts(filteredProducts);
    };

    inputSearch.addEventListener("input", (): void => {
        // Cancelamos el temporizador anterior si el usuario sigue escribiendo
        clearTimeout(debounceTimer);

        // Esperamos 300ms antes de ejecutar la lógica de búsqueda
        debounceTimer = window.setTimeout((): void => handleFilters(), 300);
    });

    // Creamos un fragmento para optimizar la inserción de múltiples elementos en el DOM
    const fragment: DocumentFragment = document.createDocumentFragment();

    categories.forEach((c: ICategory): void => {
        // Creamos los nodos uno por uno
        const option: HTMLOptionElement = document.createElement("option");

        option.value = c.id.toString();
        option.textContent = c.nombre;
        fragment.appendChild(option);
    });

    // Insertamos todas las opciones al DOM de una sola vez
    selectCategories.appendChild(fragment);

    selectCategories.addEventListener("change", (): void => handleFilters());
};

/**
 * Función para mostrar un título en el menú lateral
 * @param title el texto del título a mostrar
 */
export const showHeadingInSidebar = (title: string): void => {
    const heading = document.createElement("h2") as HTMLHeadingElement;
    heading.textContent = title;
};

/**
 * Función central para sincronizar la categoría activa en los elementos visuales (sidebar y select)
 * @param categoryId ID de la categoría (o "all" para todas)
 */
export const syncCategorySelection = (categoryId: string): void => {
    // Sincronizar el select
    const selectCategories =
        document.querySelector<HTMLSelectElement>("#select-categories");

    // Actualizamos el valor del select
    if (selectCategories && selectCategories.value !== categoryId) {
        selectCategories.value = categoryId;
    }

    // Sincronizar los enlaces del sidebar
    const sidebarLinks =
        document.querySelectorAll<HTMLAnchorElement>("#category-list a");
    sidebarLinks.forEach((link: HTMLAnchorElement) => {
        if (link.dataset.categoryId === categoryId) {
            link.classList.add("link--active");
        } else {
            link.classList.remove("link--active");
        }
    });
};

/**
 * Función para mostrar las categorías disponibles en el menú lateral
 * @param containerSelector el selector del elemento contenedor donde se mostrarán las categorías
 * @param categories el array de categorías a mostrar
 * @param products el array completo de productos, necesario para filtrar por categoría
 * al hacer click en cada una de ellas
 */
export const showCategoriesInSidebar = (
    containerSelector: string,
    categories: ICategory[],
    products: Product[],
): void => {
    const container = document.querySelector<HTMLElement>(containerSelector);
    if (!container) return;

    // Creamos un fragmento para optimizar la inserción de múltiples elementos en el DOM
    const fragment: DocumentFragment = document.createDocumentFragment();

    container.innerHTML = `
        <h2 class="sidebar__title">Categorías</h2>
        <ul id="category-list" class="sidebar__list">
            <li>
                <a href="#" class="link link--active" data-category-id="all">Todas</a>
            </li>
        </ul>
    `;

    const categoryAll = container.querySelector<HTMLLinkElement>(
        "[data-category-id='all']",
    );
    if (!categoryAll) return;

    const categoryList =
        container.querySelector<HTMLUListElement>("#category-list");
    if (!categoryList) return;

    categoryAll.addEventListener("click", (e: Event): void => {
        e.preventDefault();
        syncCategorySelection("all");
        showProducts(products);
    });

    // Iteramos sobre las categorías para crear un enlace por cada una
    categories.forEach((c: ICategory): void => {
        const li = document.createElement("li") as HTMLLIElement;
        const a = document.createElement("a") as HTMLAnchorElement;
        a.href = `#${c.nombre.toLocaleLowerCase().replaceAll(" ", "-")}`;
        a.textContent = c.nombre;
        a.dataset.categoryId = c.id.toString();
        a.classList.add("link");
        a.addEventListener("click", (e: Event): void => {
            e.preventDefault();
            syncCategorySelection(c.id.toString());
            const filteredProducts: Product[] = productService.filterByCategory(
                products,
                c.id,
            );
            showProducts(filteredProducts);
        });
        li.appendChild(a);
        fragment.appendChild(li);
    });
    categoryList.appendChild(fragment);
};

// Función para detectar cuando la barra de búsqueda se vuelva sticky al hacer scroll
export const initStickySearch = (): void => {
    const searchBar = document.querySelector<HTMLElement>(".search-bar");
    if (!searchBar) return;

    const sentinel: HTMLElement = document.createElement("div");
    sentinel.classList.add("main__sticky-sentinel");
    searchBar.parentNode?.insertBefore(sentinel, searchBar);

    // Usamos IntersectionObserver para detectar cuando el centinela sale de la vista
    const observer: IntersectionObserver = new IntersectionObserver(
        ([entry]: IntersectionObserverEntry[]) => {
            if (entry) {
                searchBar.classList.toggle(
                    "search-bar--is-sticky",
                    !entry.isIntersecting,
                );
            }
        },
        { threshold: [0] },
    );

    observer.observe(sentinel);
};
