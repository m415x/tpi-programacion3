import { type Producto, productos, categorias } from "../../../js/data";
import { logout } from "../../../utils/auth";

const buttonLogout = document.getElementById(
  "logoutButton",
) as HTMLButtonElement;

buttonLogout?.addEventListener("click", (): void => {
  logout();
});

const listaCategorias =
  document.querySelector<HTMLUListElement>("#lista-categorias");

const cargarCategorias = (categorias: string[]): void => {
  categorias.forEach((categoria) => {
    const li = document.createElement("li");
    const a = document.createElement("a");
    a.href = "#";
    a.textContent = categoria;
    li.appendChild(a);
    listaCategorias?.appendChild(li);
  });
};

const contenedorProductos = document.querySelector<HTMLElement>(
  "#contenedor-productos",
);

const cargarProductos = (productos: Producto[]): void => {
  contenedorProductos ? (contenedorProductos.innerHTML = "") : null;

  if (productos.length > 0) {
    productos.forEach((producto) => {
      const article: HTMLElement = document.createElement("article");
      article.classList.add("producto__card");

      const precioFormateado: string = producto.precio.toLocaleString("es-AR", {
        style: "currency",
        currency: "ARS",
      });

      const [precioEntero, precioDecimal]: string[] =
        precioFormateado.split(",");

      article.innerHTML = `
            <img class="producto__imagen" src="${producto.imagen}" alt="${producto.nombre}">
            <div class="producto__contenido">
                <div class="producto__cuerpo">
                    <p class="producto__categoria">${producto.categoria}</p>
                    <h3 class="producto__nombre">${producto.nombre}</h3>
                    <p class="producto__descripcion">${producto.descripcion}</p>
                </div>
                <div class="producto__pie">
                    <p class="producto__precio">${precioEntero}<span>${precioDecimal}</span></p>
                    <button class="btn btn__tertiary btn__tertiary--add-product" onclick="alert('Has agregado: ${producto.nombre}')">
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

const inputSearch = document.querySelector<HTMLInputElement>("#input-search");

inputSearch?.addEventListener("input", (e: Event) => {
  const target = e.target as HTMLInputElement;

  const productosFiltrados = productos.filter((producto): boolean =>
    producto.nombre.toLowerCase().includes(target.value.toLowerCase()),
  );
  cargarProductos(productosFiltrados);
});

const selectCategorias =
  document.querySelector<HTMLSelectElement>("#select-categories");

const opcionesCategorias = (categorias: string[]): void => {
  categorias.forEach((categoria): void => {
    const option: HTMLOptionElement = document.createElement("option");

    option.value = categoria;
    option.textContent = categoria;
    selectCategorias?.appendChild(option);
  });
};

const selectCategoria =
  document.querySelector<HTMLSelectElement>("#select-categoria");

selectCategoria?.addEventListener("change", (e: Event) => {
  const target = e.target as HTMLOptionElement;
  if (target.value === "all") {
    cargarProductos(productos);
  } else {
    const productosFiltrados: Producto[] = productos.filter(
      (producto): boolean => producto.categoria === target.value,
    );
    cargarProductos(productosFiltrados);
  }
});

// Llamada a las funciones
document.addEventListener("DOMContentLoaded", (): void => {
  cargarCategorias(categorias);
  opcionesCategorias(categorias);
  cargarProductos(productos);
});
