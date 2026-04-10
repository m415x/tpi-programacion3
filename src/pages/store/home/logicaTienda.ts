import type { IProduct } from "@interfaces/IProduct";

// Función para cargar las categorías en el menú lateral
export const cargarCategorias = (categorias: string[]): void => {
    const listaCategorias =
        document.querySelector<HTMLUListElement>("#lista-categorias");

    categorias.forEach((categoria) => {
        const li = document.createElement("li");
        const a = document.createElement("a");
        a.href = "#";
        a.textContent = categoria;
        li.appendChild(a);
        listaCategorias?.appendChild(li);
    });
};

//
export const cargarProductos = (productos: IProduct[]): void => {
    const contenedorProductos = document.querySelector<HTMLElement>(
        "#contenedor-productos",
    );

    contenedorProductos ? (contenedorProductos.innerHTML = "") : null;

    if (productos.length > 0) {
        productos.forEach((producto) => {
            const article: HTMLElement = document.createElement("article");
            article.classList.add("producto__card");

            const precioFormateado: string = producto.price.toLocaleString(
                "es-AR",
                {
                    style: "currency",
                    currency: "ARS",
                },
            );

            const [precioEntero, precioDecimal]: string[] =
                precioFormateado.split(",");

            article.innerHTML = `
            <img class="producto__imagen" src="${producto.image}" alt="${producto.name}">
            <div class="producto__contenido">
                <div class="producto__cuerpo">
                    <p class="producto__categoria">${producto.category}</p>
                    <h3 class="producto__nombre">${producto.name}</h3>
                    <p class="producto__descripcion">${producto.description}</p>
                </div>
                <div class="producto__pie">
                    <p class="producto__precio">${precioEntero}<span>${precioDecimal}</span></p>
                    <button class="btn btn__tertiary btn__tertiary--add-product" onclick="alert('Has agregado: ${producto.name}')">
                        Agregar al carrito
                    </button>
                </div>
            </div>
        `;
            contenedorProductos?.appendChild(article);
        });
    } else {
        const emptyResult: HTMLParagraphElement = document.createElement("p");

        emptyResult.classList.add("empty-result");
        emptyResult.textContent = "No se encontraron productos";
        contenedorProductos?.appendChild(emptyResult);
    }
};

// Función para cargar la barra de búsqueda
export const cargarSearchBar = (
    productos: IProduct[],
    categorias: string[],
): void => {
    const inputSearch =
        document.querySelector<HTMLInputElement>("#input-search");

    inputSearch?.addEventListener("input", (e: Event) => {
        const target = e.target as HTMLInputElement;

        const productosFiltrados = productos.filter((producto): boolean =>
            producto.name.toLowerCase().includes(target.value.toLowerCase()),
        );
        cargarProductos(productosFiltrados);
    });

    const selectCategorias =
        document.querySelector<HTMLSelectElement>("#select-categories");

    categorias.forEach((categoria): void => {
        const option: HTMLOptionElement = document.createElement("option");

        option.value = categoria;
        option.textContent = categoria;
        selectCategorias?.appendChild(option);
    });

    const selectCategoria =
        document.querySelector<HTMLSelectElement>("#select-categoria");

    selectCategoria?.addEventListener("change", (e: Event) => {
        const target = e.target as HTMLOptionElement;
        if (target.value === "all") {
            cargarProductos(productos);
        } else {
            const productosFiltrados: IProduct[] = productos.filter(
                (producto): boolean => producto.category === target.value,
            );
            cargarProductos(productosFiltrados);
        }
    });
};
