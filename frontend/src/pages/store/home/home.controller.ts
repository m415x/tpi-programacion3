import type { Product } from "@interfaces/Product";
import type { ICategory } from "@interfaces/ICategory";
import { storage } from "@utils/storage";
import { productService as ps } from "@/services/productService";

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

            const formattedPrice: string = prod.precio.toLocaleString("es-AR", {
                style: "currency",
                currency: "ARS",
            });

            const [wholePrice, decimalPrice]: string[] =
                formattedPrice.split(",");

            const categoryName =
                prod.categorias.length > 0 && prod.categorias[0]
                    ? prod.categorias[0].nombre
                    : "Sin categoría";

            const btnAvailable: string = prod.disponible
                ? `<button class="btn btn--primary btn--add-product">
                        Agregar al carrito
                    </button>`
                : `<button class="btn btn--secondary btn--add-product btn--disabled" disabled >No disponible</button>`;

            article.innerHTML = `
            <img class="product__img" src="https://via.placeholder.com/400x300?text=Cargando..." id="img-product-${prod.id}" alt="${prod.nombre}">
            <p class="product__stock ${prod.disponible ? "product__stock--available" : ""}">${prod.disponible ? "Disponible" : "Agotado"}</p>
            <div class="product__content">
                <div class="product__body">
                    <p class="product__category">${categoryName}</p>
                    <h3 class="product__name">${prod.nombre}</h3>
                    <p class="product__description">${prod.descripcion}</p>
                </div>
                <div class="product__foot">
                    <p class="product__price">${wholePrice}<span>${decimalPrice}</span></p>
                    ${btnAvailable}
                </div>
            </div>
            `;
            productContainer?.appendChild(article);

            // Iniciamos la carga asíncrona de la imagen
            fetchAndAssignProductImage(prod.id, prod.nombre);

            const btnAdd = article.querySelector(
                ".btn--add-product",
            ) as HTMLButtonElement;
            btnAdd?.addEventListener("click", (): void => {
                if (storage.updateCartItem(prod.id)) {
                    alert(`Has agregado: ${prod.nombre}`);
                    storage.updateCartItem(prod.id);
                } else {
                    alert("Stock insuficiente");
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

// Función asíncrona para obtener y asignar la imagen al producto dinámicamente
const fetchAndAssignProductImage = async (
    productId: number,
    productName: string,
): Promise<void> => {
    // Delegamos la obtención del dato al servicio
    const imageUrl: string = await ps.getProductImageUrl(productName);

    // Solo interactuamos con el DOM en esta capa
    const imgElement = document.getElementById(
        `img-product-${productId}`,
    ) as HTMLImageElement;

    if (imgElement) {
        imgElement.src = imageUrl;
    }
};
