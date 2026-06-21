import type { ICategory } from "@interfaces/Category.interface";
import type { IProduct } from "@interfaces/Product.interface";
import { productService } from "@services/product.service";
import { categoryService } from "@services/category.service";
import { updateCartBadge } from "@utils/components";
import { storage } from "@utils/storage";
import {
    formattedPriceHTML,
    getItemAvailability,
    showCartNotice,
    updateBaseAvailabilityUI,
    wrapWithDetailLink,
} from "@utils/uiUtils";

/**
 *
 */
export const homeController = {
    /**
     * Función para mostrar los productos en el contenedor principal de la tienda
     */
    async showProducts(): Promise<void> {
        const productContainer = document.querySelector<HTMLElement>("#product-container");
        if (!productContainer) return;

        const productQty = document.querySelector<HTMLParagraphElement>("#product-qty");
        if (!productQty) return;

        const searchTerm = productContainer.dataset.searchTerm || "";
        const selectedCategory = productContainer.dataset.categoryId || "all";

        // Limpiamos el contenedor antes de mostrar los productos para evitar duplicados
        productContainer.innerHTML = "";

        // Creamos un fragmento para optimizar la inserción de múltiples elementos en el DOM
        const fragment: DocumentFragment = document.createDocumentFragment();

        // Traemos los datos asíncronos en paralelo para máxima velocidad
        const [allProducts, categories] = await Promise.all([
            productService.getAll({ name: searchTerm }),
            categoryService.getAll(),
        ]);

        // Aplicamos el filtro de categoría sobre los productos ya filtrados por nombre desde el backend
        let activeProducts = productService.applyFilters(allProducts, {
            searchTerm: "",
            categoryId: selectedCategory,
        });

        // Creamos un mapa indexado por ID para buscar en tiempo de ejecución O(1)
        const categoryMap = new Map<string, string>(
            categories.map((cat: { id: string; name: string }) => [cat.id, cat.name]),
        );

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
            activeProducts.forEach((prod: IProduct): void => {
                // Creamos los nodos uno por uno
                const article: HTMLElement = document.createElement("article");
                article.classList.add("card", "product__card");
                article.dataset.id = prod.id.toString();

                // Formateamos el precio
                const unitPrice: string = formattedPriceHTML(prod.price);

                // Buscamos el nombre usando el ID único del backend
                const categoryName: string = prod.categoryId
                    ? categoryMap.get(prod.categoryId) || "Sin categoría"
                    : "Sin categoría";

                // Obtenemos el estado de disponibilidad para mostrar en la card
                const { isAvailable } = getItemAvailability(prod);

                // Creamos el HTML del producto, incluyendo la imagen con carga asíncrona y los enlaces a detalle
                const linkedImg: string = wrapWithDetailLink(
                    prod.id,
                    `<img class="product__img" src="${prod.imageUrl}" id="img-product-${prod.id}" alt="${prod.name}">`,
                );

                // El nombre del producto también se envuelve en un enlace a detalle
                const linkedName: string = wrapWithDetailLink(prod.id, `<h3 class="product__name">${prod.name}</h3>`);

                article.innerHTML = `
                ${linkedImg}
                <p class="product__stock stock-badge"></p>
                <div class="product__content">
                    <div class="product__body">
                        <p class="product__category">${categoryName}</p>
                        ${linkedName}
                        <p class="product__description">${prod.description}</p>
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
                homeController.updateProductCardUI(article, isAvailable);

                const btnAdd = article.querySelector<HTMLButtonElement>(".btn--add-product");
                if (!btnAdd) return;

                btnAdd.addEventListener("click", async (): Promise<void> => {
                    const wasUpdated: boolean = await storage.updateCartItem(prod.id);

                    if (wasUpdated) {
                        const status = getItemAvailability(prod);

                        homeController.updateProductCardUI(article, status.isAvailable);

                        // Actualizamos el badge del carrito
                        updateCartBadge();

                        // Mostrar el aviso debajo de la searchBar
                        const searchBar = document.querySelector<HTMLElement>(".search-bar");
                        if (searchBar) {
                            showCartNotice(searchBar, 1, prod.name, "after");
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
    },

    /**
     * Actualiza el estado de la card del producto (disponible/agotado) según su stock y lo que hay en el carrito
     * @param container el elemento HTML de la card del producto a actualizar
     * @param isAvailable booleano que indica si el producto está disponible para agregar al carrito
     */
    updateProductCardUI(container: HTMLElement, isAvailable: boolean): void {
        // Actualizamos el badge de stock y el botón de agregar al carrito
        updateBaseAvailabilityUI(container, isAvailable, {
            badge: ".product__stock",
            button: ".btn--add-product",
        });

        const badge = container.querySelector<HTMLElement>(".product__stock");
        if (badge) badge.textContent = isAvailable ? "Disponible" : "Agotado";
    },

    /**
     * Inicializa y gestiona los eventos de la barra de búsqueda y el selector de categorías.
     * Delega el filtrado de datos a la capa de servicios e impacta los cambios en la UI.
     * @param categories Lista de categorías activas para poblar el elemento select.
     */
    showSearchBar(categories: ICategory[]): void {
        const inputSearch = document.querySelector<HTMLInputElement>("#input-search");
        const selectCategories = document.querySelector<HTMLSelectElement>("#select-categories");
        if (!inputSearch || !selectCategories) return;

        // Variable para manejar el debounce de la búsqueda (tipado correcto en entorno web)
        let debounceTimer: number;

        /**
         * Captura los filtros de la interfaz y relanza la renderización de productos.
         * Pasamos los filtros como un objeto de criterios al estado global o la función de renderizado.
         */
        const handleFilters = (): void => {
            const searchTerm: string = inputSearch.value || "";
            const categoryId: string = selectCategories.value || "all";

            // Guardamos los filtros en el objeto dataset del contenedor o en una ventana de estado
            // para que 'showProducts' sepa qué criterios aplicar al invocar productService.getAll()
            const productContainer = document.querySelector<HTMLElement>("#product-container");
            if (productContainer) {
                productContainer.dataset.searchTerm = searchTerm;
                productContainer.dataset.categoryId = categoryId;
            }

            // Volvemos a invocar la renderización principal.
            homeController.showProducts();
        };

        // Evento de escucha para la barra de texto con debounce de 300ms
        inputSearch.addEventListener("input", (): void => {
            clearTimeout(debounceTimer);
            debounceTimer = window.setTimeout((): void => handleFilters(), 300);
        });

        // Creamos un fragmento para optimizar la inserción masiva en el DOM
        const fragment: DocumentFragment = document.createDocumentFragment();

        categories.forEach((c: ICategory): void => {
            const option: HTMLOptionElement = document.createElement("option");
            option.value = c.id.toString();
            option.textContent = c.name;
            fragment.appendChild(option);
        });

        // Insertamos todas las opciones al HTML de una sola vez
        selectCategories.appendChild(fragment);

        // Evento de escucha para el cambio de categoría instantáneo
        selectCategories.addEventListener("change", (): void => handleFilters());
    },

    /**
     * Muestra un título en el menú lateral
     * @param title el texto del título a mostrar
     */
    showHeadingInSidebar(title: string): void {
        const heading = document.createElement("h2") as HTMLHeadingElement;
        heading.textContent = title;
    },

    /**
     * Sincroniza la categoría activa en los elementos visuales (sidebar y select)
     * @param categoryId ID de la categoría (o "all" para todas)
     */
    syncCategorySelection(categoryId: string): void {
        // Sincronizar el select
        const selectCategories = document.querySelector<HTMLSelectElement>("#select-categories");

        // Actualizamos el valor del select
        if (selectCategories && selectCategories.value !== categoryId) {
            selectCategories.value = categoryId;
        }

        // Sincronizar los enlaces del sidebar
        const sidebarLinks = document.querySelectorAll<HTMLAnchorElement>("#category-list a");
        sidebarLinks.forEach((link: HTMLAnchorElement) => {
            if (link.dataset.categoryId === categoryId) {
                link.classList.add("link--active");
            } else {
                link.classList.remove("link--active");
            }
        });
    },

    /**
     * Renderiza la lista de categorías en la barra lateral y gestiona el evento de filtrado.
     * Mantiene el desacoplamiento al delegar la actualización de datos a la UI centralizada.
     * @param containerSelector Selector CSS del contenedor de la Sidebar
     * @param categories Lista de categorías activas obtenidas del backend
     */
    showCategoriesInSidebar(containerSelector: string, categories: ICategory[]): void {
        const container = document.querySelector<HTMLElement>(containerSelector);
        if (!container) return;

        // Creamos un fragmento para optimizar la inserción de múltiples elementos en el DOM
        const fragment: DocumentFragment = document.createDocumentFragment();

        container.innerHTML = `
            <div class="sidebar__header">
                <h2>Categorías</h2>
            </div>
            <ul id="category-list" class="sidebar__list">
                <li>
                    <a href="#" class="link link--active" data-category-id="all">Todas</a>
                </td>
            </ul>
        `;

        const categoryAll = container.querySelector<HTMLLinkElement>("[data-category-id='all']");
        const categoryList = container.querySelector<HTMLUListElement>("#category-list");
        if (!categoryAll || !categoryList) return;

        /**
         * Centraliza el cambio de filtro de categoría compartiendo el ID en el DOM
         * y disparando la recarga asíncrona desde el servidor.
         */
        const handleCategoryClick = (categoryId: string): void => {
            const productContainer = document.querySelector<HTMLElement>("#product-container");
            const selectCategories = document.querySelector<HTMLSelectElement>("#select-categories");

            // 1. Persistimos el filtro en el dataset para que lo lea 'showProducts'
            if (productContainer) {
                productContainer.dataset.categoryId = categoryId;
            }

            // 2. Sincronizamos el select de la searchbar (si existe en la UI) para evitar desfaces visuales
            if (selectCategories) {
                selectCategories.value = categoryId;
            }

            // 3. Modificamos los estilos activos en los enlaces (función utilitaria de tu UI)
            homeController.syncCategorySelection(categoryId);

            // 4. Gatillamos la recarga reactiva de las tarjetas
            homeController.showProducts();
        };

        // Manejador para el botón por defecto "Todas"
        categoryAll.addEventListener("click", (e: Event): void => {
            e.preventDefault();
            handleCategoryClick("all");
        });

        // Iteramos sobre las categorías reales de la base de datos para crear los enlaces
        categories.forEach((c: ICategory): void => {
            const li = document.createElement("li");
            const a = document.createElement("a");

            a.href = `#${c.name.toLowerCase().trim().replaceAll(" ", "-")}`;
            a.textContent = c.name;
            a.dataset.categoryId = c.id.toString();
            a.classList.add("link");

            a.addEventListener("click", (e: Event): void => {
                e.preventDefault();
                handleCategoryClick(c.id.toString());
            });

            li.appendChild(a);
            fragment.appendChild(li);
        });

        categoryList.appendChild(fragment);
    },

    /**
     * Detecta cuando la barra de búsqueda se vuelva sticky al hacer scroll
     */
    initStickySearch(): void {
        const searchBar = document.querySelector<HTMLElement>(".search-bar");
        if (!searchBar) return;

        const sentinel: HTMLElement = document.createElement("div");
        sentinel.classList.add("main__sticky-sentinel");
        searchBar.parentNode?.insertBefore(sentinel, searchBar);

        // Usamos IntersectionObserver para detectar cuando el centinela sale de la vista
        const observer: IntersectionObserver = new IntersectionObserver(
            ([entry]: IntersectionObserverEntry[]) => {
                if (entry) {
                    searchBar.classList.toggle("search-bar--is-sticky", !entry.isIntersecting);
                }
            },
            { threshold: [0] },
        );

        observer.observe(sentinel);
    },
};
