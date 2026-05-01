import type { ICartItem } from "@interfaces/ICartItem";
import type { ICategory } from "@interfaces/ICategory";
import type { Product } from "@interfaces/Product";
import { cartService } from "@services/cartService";
import { productService } from "@services/productService";
import { updateCartBadge } from "@utils/components";
import { storage } from "@utils/storage";
import {
    getDisabledState,
    updateProductImageUI,
    wrapWithDetailLink,
    formattedPriceHTML,
    showCartNotice,
} from "@utils/uiUtils";

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

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!container) return;

    const title = document.createElement("h2") as HTMLHeadingElement;
    title.textContent = "Categorías";
    container.appendChild(title);

    const unOrderedList = document.createElement("ul") as HTMLUListElement;
    unOrderedList.id = "category-list";
    container.appendChild(unOrderedList);

    const li = document.createElement("li") as HTMLLIElement;
    const a = document.createElement("a") as HTMLAnchorElement;
    a.href = "#";
    a.textContent = "Todas";
    a.dataset.categoryId = "all";
    a.classList.add("link", "link--active");
    a.addEventListener("click", (e: Event): void => {
        e.preventDefault();
        syncCategorySelection("all");
        showProducts(products);
    });
    li.appendChild(a);
    unOrderedList?.appendChild(li);

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
        unOrderedList?.appendChild(li);
    });
};

/**
 * Función para mostrar los productos en el contenedor principal de la tienda
 * @param products el array de productos a mostrar en la interfaz
 */
export const showProducts = (products: Product[]): void => {
    const productContainer =
        document.querySelector<HTMLElement>("#product-container");

    const productQty =
        document.querySelector<HTMLParagraphElement>("#product-qty");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!productContainer || !productQty) return;

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
    // Limpiar el contenedor de productos
    productContainer ? (productContainer.innerHTML = "") : null;

    if (activeProducts.length > 0) {
        activeProducts.forEach((prod: Product): void => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "product__card");

            const unitPrice: string = formattedPriceHTML(prod.precio);

            const categoryName =
                prod.categorias.length > 0 && prod.categorias[0]
                    ? prod.categorias[0].nombre
                    : "Sin categoría";

            // Obtener cuánto de este producto ya está en el carrito
            const qtyInCart: number = cartService.getProductQuantity(prod.id);

            // Calcular el stock disponible para mostrar
            const displayStock: number = prod.stock - qtyInCart;

            // Determinar si habilitar el elemento
            const isActuallyAvailable: boolean =
                prod.disponible && displayStock > 0;
            const disabledState: string = getDisabledState(isActuallyAvailable);
            const disabledStateBadge: string = getDisabledState(
                isActuallyAvailable,
                "stock-badge--out-of-stock",
            );

            // Determinar el texto del badge y del botón según la disponibilidad real del producto
            const badgeText: string = isActuallyAvailable
                ? "Disponible"
                : "Agotado";
            const btnTextAddToCart: string = isActuallyAvailable
                ? `Agregar al carrito`
                : `No disponible`;

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
            <p class="product__stock stock-badge ${disabledStateBadge}">${badgeText}</p>
            <div class="product__content">
                <div class="product__body">
                    <p class="product__category">${categoryName}</p>
                    ${linkedName}
                    <p class="product__description">${prod.descripcion}</p>
                </div>
                <div class="product__foot">
                    <p class="price product__price">${unitPrice}</p>
                    <button class="btn btn--primary btn--add-product" ${disabledState}>${btnTextAddToCart}</button>
                </div>
            </div>
            `;
            productContainer.appendChild(article);

            // Iniciamos la carga asíncrona de la imagen
            updateProductImageUI(prod.id);

            const btnAdd =
                article.querySelector<HTMLButtonElement>(".btn--add-product");

            // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
            if (!btnAdd) return;

            btnAdd.addEventListener("click", (): void => {
                if (storage.updateCartItem(prod.id)) {
                    // Calculamos el stock remanente
                    const cartItem: ICartItem | undefined = storage
                        .getCartItems()
                        .find((i: ICartItem): boolean => i.id === prod.id);
                    const qtyInCart: number = cartItem ? cartItem.qty : 0;
                    const currentAvailable: number = prod.stock - qtyInCart;

                    if (currentAvailable <= 0) {
                        // Stock agotado: Refrescamos para deshabilitar el botón y cambiar estilos
                        showProducts(products);
                        alert("¡Has añadido la última unidad!");
                    }
                    // Actualizamos el badge del carrito
                    updateCartBadge();

                    // Mostrar el aviso debajo de la searchBar
                    const searchBar =
                        document.querySelector<HTMLElement>(".search-bar");
                    if (searchBar) {
                        showCartNotice(searchBar, 1, prod.nombre, "after");
                    }
                }
            });
        });
    } else {
        const emptyResult: HTMLParagraphElement = document.createElement("p");

        emptyResult.classList.add("empty-result");
        emptyResult.textContent = "No se encontraron productos";
        productContainer?.appendChild(emptyResult);
    }
};

/**
 * Función para mostrar la barra de búsqueda y el filtro por categorías, y manejar sus eventos
 * @param product el array de productos sobre los cuales realizar la búsqueda
 * @param category el array de categorías para poblar el selector de filtros
 */
export const showSearchBar = (
    product: Product[],
    category: ICategory[],
): void => {
    const inputSearch =
        document.querySelector<HTMLInputElement>("#input-search");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!inputSearch) return;

    inputSearch.addEventListener("input", (e: Event) => {
        const target = e.target as HTMLInputElement;

        const filteredProducts: Product[] = product.filter(
            (prod: Product): boolean =>
                prod.nombre.toLowerCase().includes(target.value.toLowerCase()),
        );
        showProducts(filteredProducts);
    });

    const selectCategories =
        document.querySelector<HTMLSelectElement>("#select-categories");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
    if (!selectCategories) return;

    category.forEach((c: ICategory): void => {
        const option: HTMLOptionElement = document.createElement("option");

        option.value = c.id.toString();
        option.textContent = c.nombre;
        selectCategories.appendChild(option);
    });

    selectCategories.addEventListener("change", (e: Event): void => {
        const target = e.target as HTMLSelectElement;
        const value: string = target.value;

        syncCategorySelection(value);

        if (value === "all") {
            showProducts(product);
        } else {
            const filteredProducts: Product[] = productService.filterByCategory(
                product,
                value,
            );
            showProducts(filteredProducts);
        }
    });
};

// Función para detectar cuando la barra de búsqueda se vuelva sticky al hacer scroll
export const initStickySearch = (): void => {
    const searchBar = document.querySelector<HTMLElement>(".search-bar");

    // Cláusula de guarda para evitar errores si el elemento no existe en el DOM
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
