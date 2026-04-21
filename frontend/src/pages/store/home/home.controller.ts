import type { Product } from "@interfaces/Product";
import type { ICategory } from "@interfaces/ICategory";
import { storage } from "@utils/storage";
import { productService as ps } from "@/services/productService";
import { updateProductImageUI } from "@/utils/uiUtils";
import { formattedPriceHTML } from "@/utils/uiUtils";
import type { ICartItem } from "@/types/ICartItem";
import { updateCartBadge } from "@/utils/components";

// Función para cargar el título en el menú lateral
export const showHeadingInSidebar = (title: string): void => {
    const heading = document.createElement("h2") as HTMLHeadingElement;
    heading.textContent = title;
};

// Función para cargar las categorías en el menú lateral
export const showCategoriesInSidebar = (
    containerId: string,
    categories: ICategory[],
    products: Product[],
): void => {
    const container = document.getElementById(
        containerId,
    ) as HTMLElement | null;

    const title = document.createElement("h2") as HTMLHeadingElement;
    title.textContent = "Categorías";
    container?.appendChild(title);

    const unOrderedList = document.createElement("ul") as HTMLUListElement;
    unOrderedList.id = "category-list";
    container?.appendChild(unOrderedList);

    const li = document.createElement("li") as HTMLLIElement;
    const a = document.createElement("a") as HTMLAnchorElement;
    a.href = "#";
    a.textContent = "Todas";
    a.addEventListener("click", (e: Event): void => {
        e.preventDefault();
        showProducts(products);
    });
    li.appendChild(a);
    unOrderedList?.appendChild(li);

    categories.forEach((c: ICategory): void => {
        const li = document.createElement("li") as HTMLLIElement;
        const a = document.createElement("a") as HTMLAnchorElement;
        a.href = `#${c.nombre.toLocaleLowerCase().replaceAll(" ", "-")}`;
        a.textContent = c.nombre;
        a.addEventListener("click", (e: Event): void => {
            e.preventDefault();
            const filteredProducts: Product[] = ps.filterByCategory(
                products,
                c.id,
            );
            showProducts(filteredProducts);
        });
        li.appendChild(a);
        unOrderedList?.appendChild(li);
    });
};

// Función para cargar los productos en el contenedor principal
export const showProducts = (products: Product[]): void => {
    const productContainer =
        document.querySelector<HTMLElement>("#product-container");

    const productQty =
        document.querySelector<HTMLParagraphElement>("#product-qty");

    const activeProducts: Product[] = ps.getActiveProducts(products);

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
            const cartItem: ICartItem | undefined = storage
                .getCartItems()
                .find((i: ICartItem): boolean => i.id === prod.id);
            const qtyInCart: number = cartItem ? cartItem.qty : 0;

            // Calcular el stock disponible para mostrar
            const displayStock: number = prod.stock - qtyInCart;

            // Determinar si habilitar el botón
            const isActuallyAvailable: boolean =
                prod.disponible && displayStock > 0;

            const btnAvailable: string = isActuallyAvailable
                ? `<button class="btn btn--primary btn--add-product">
                        Agregar al carrito
                    </button>`
                : `<button class="btn btn--secondary btn--add-product btn--disabled" disabled >No disponible</button>`;

            article.innerHTML = `
            <img class="product__img" src="" id="img-product-${prod.id}" alt="${prod.nombre}">
            <p class="product__stock ${isActuallyAvailable ? "product__stock--available" : ""}">${isActuallyAvailable ? "Disponible" : "Agotado"}</p>
            <div class="product__content">
                <div class="product__body">
                    <p class="product__category">${categoryName}</p>
                    <h3 class="product__name">${prod.nombre}</h3>
                    <p class="product__description">${prod.descripcion}</p>
                </div>
                <div class="product__foot">
                    <p class="price product__price">${unitPrice}</p>
                    ${btnAvailable}
                </div>
            </div>
            `;
            productContainer?.appendChild(article);

            // Iniciamos la carga asíncrona de la imagen
            updateProductImageUI(prod.id);

            const btnAdd = article.querySelector(
                ".btn--add-product",
            ) as HTMLButtonElement;
            btnAdd?.addEventListener("click", (): void => {
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

// Función para cargar la barra de búsqueda y filtros
export const showSearchBar = (
    product: Product[],
    category: ICategory[],
): void => {
    const inputSearch =
        document.querySelector<HTMLInputElement>("#input-search");

    inputSearch?.addEventListener("input", (e: Event) => {
        const target = e.target as HTMLInputElement;

        const filteredProducts: Product[] = product.filter(
            (prod: Product): boolean =>
                prod.nombre.toLowerCase().includes(target.value.toLowerCase()),
        );
        showProducts(filteredProducts);
    });

    const selectCategories =
        document.querySelector<HTMLSelectElement>("#select-categories");

    if (!selectCategories) return;

    category.forEach((c: ICategory): void => {
        const option: HTMLOptionElement = document.createElement("option");

        option.value = c.id.toString();
        option.textContent = c.nombre;
        selectCategories?.appendChild(option);
    });

    selectCategories?.addEventListener("change", (e: Event): void => {
        const target = e.target as HTMLSelectElement;
        const value: string = target.value;

        if (value === "all") {
            showProducts(product);
        } else {
            const filteredProducts: Product[] = ps.filterByCategory(
                product,
                value,
            );
            showProducts(filteredProducts);
        }
    });
};

// Función para detectar cuando la barra de búsqueda se vuelva sticky al hacer scroll
export const initStickySearch = (): void => {
    const searchBar = document.querySelector(".search-bar") as HTMLElement;
    if (!searchBar) return;

    const sentinel: HTMLElement = document.createElement("div");
    sentinel.classList.add("main__sticky-sentinel");
    searchBar.parentNode?.insertBefore(sentinel, searchBar);

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
