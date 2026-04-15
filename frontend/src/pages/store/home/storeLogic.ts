import type { Product } from "@interfaces/Product";
import type { ICategory } from "@interfaces/ICategory";
import { sessionStore } from "@utils/sessionStore";

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
            const filteredProducts: Product[] = filterProductsByCategory(
                products,
                c.id,
            );
            showProducts(filteredProducts);
        });
        li.appendChild(a);
        unOrderedList?.appendChild(li);
    });
};

// Función reutilizable para filtrar productos por ID de categoría
const filterProductsByCategory = (
    products: Product[],
    categoryId: number | string,
): Product[] => {
    // Convertimos ambos a string para asegurar una comparación segura
    const categoryIdStr = categoryId.toString();
    return products.filter((p: Product): boolean =>
        p.categorias.some(
            (cat: ICategory | undefined): boolean =>
                cat?.id.toString() === categoryIdStr,
        ),
    );
};

// Función para cargar los productos en el contenedor principal
export const showProducts = (products: Product[]): void => {
    const productContainer =
        document.querySelector<HTMLElement>("#product-container");

    const productQty =
        document.querySelector<HTMLParagraphElement>("#product-qty");

    const activeProducts: Product[] = products.filter(
        (p: Product): boolean => !p.eliminado,
    );

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
        activeProducts.forEach((p: Product): void => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("card", "product__card");

            const formattedPrice: string = p.precio.toLocaleString("es-AR", {
                style: "currency",
                currency: "ARS",
            });

            const [wholePrice, decimalPrice]: string[] =
                formattedPrice.split(",");

            article.innerHTML = `
            <img class="product__img" src="https://via.placeholder.com/400x300?text=Cargando..." id="img-product-${p.id}" alt="${p.nombre}">
            <p class="product__stock ${p.stock > 0 ? "product__stock--available" : ""}">${p.stock > 0 ? `Stock ${p.stock}` : "Agotado"}</p>
            <div class="product__content">
                <div class="producto__cuerpo">
                    <p class="producto__categoria">${p.categorias[0]?.nombre || "Sin categoría"}</p>
                    <h3 class="producto__nombre">${p.nombre}</h3>
                    <p class="producto__descripcion">${p.descripcion}</p>
                </div>
                <div class="producto__pie">
                    <p class="producto__precio">${wholePrice}<span>${decimalPrice}</span></p>
                    ${
                        p.disponible
                            ? `<button class="btn btn--primary btn--add-product">
                                    Agregar al carrito
                                </button>`
                            : `<button class="btn btn--secondary btn--add-product btn--disabled" disabled >No disponible</button>`
                    }
                </div>
            </div>
            `;
            productContainer?.appendChild(article);

            // Iniciamos la carga asíncrona de la imagen
            fetchAndAssignProductImage(p.id, p.nombre);

            const btnAdd = article.querySelector(
                ".btn--add-product",
            ) as HTMLButtonElement;
            btnAdd?.addEventListener("click", (): void => {
                alert(`Has agregado: ${p.nombre}`);
                sessionStore.updateCartItem(p.id);
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
            (p: Product): boolean =>
                p.nombre.toLowerCase().includes(target.value.toLowerCase()),
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
            const filteredProducts: Product[] = filterProductsByCategory(
                product,
                value,
            );
            showProducts(filteredProducts);
        }
    });
};

// Promesa global para evitar que se disparen múltiples peticiones a la API al mismo tiempo
let fastFoodApiPromise: Promise<any> | null = null;

// Función asíncrona para obtener y asignar la imagen al producto dinámicamente
const fetchAndAssignProductImage = async (
    productId: number,
    productName: string,
): Promise<void> => {
    try {
        // Si no hay una petición en curso, la iniciamos y almacenamos la promesa
        if (!fastFoodApiPromise) {
            fastFoodApiPromise = fetch("https://devsapihub.com/api-fast-food")
                .then((res: Response) => res.json())
                .catch((err: Error) => {
                    console.error("Error consumiendo la API de imágenes:", err);
                    return []; // Fallback a un array vacío en caso de error de red
                });
        }

        const data = await fastFoodApiPromise;

        // NOTA: Ajusta esta lectura según la estructura real del JSON de tu API.
        // Aquí asumimos que los datos vienen en un array directo o dentro de 'data'/'results'
        const dataArray = Array.isArray(data)
            ? data
            : data.results || data.data || [];

        // Intentamos buscar una comida que coincida parcialmente con el nombre del producto
        const match = dataArray.find((item: any) => {
            const apiName = (item.name || item.title || "").toLowerCase();
            return (
                apiName &&
                (productName.toLowerCase().includes(apiName) ||
                    apiName.includes(productName.toLowerCase()))
            );
        });

        const placeholderImg =
            "https://via.placeholder.com/400x300?text=Food+Store";
        const randomImg =
            dataArray.length > 0
                ? dataArray[Math.floor(Math.random() * dataArray.length)]?.image
                : null;
        const imageUrl =
            match?.image || match?.url || randomImg || placeholderImg;

        const imgElement = document.getElementById(
            `img-product-${productId}`,
        ) as HTMLImageElement;
        if (imgElement) {
            imgElement.src = imageUrl;
        }
    } catch (error) {
        console.error(`Error asignando imagen a ${productName}:`, error);
    }
};
