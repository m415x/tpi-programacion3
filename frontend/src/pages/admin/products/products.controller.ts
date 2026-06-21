import type { IProduct } from "@interfaces/Product.interface";
import type { ICategory } from "@interfaces/Category.interface";
import { productService } from "@services/product.service";
import { categoryService } from "@services/category.service";

// Estado local encapsulado del módulo
let categoriesMap: Map<string, string> = new Map();
let isEditingMode: boolean = false;

// Helpers para obtener referencias diferidas del DOM tras la inyección dinámica
const getElements = () => ({
    tableBody: document.querySelector<HTMLTableSectionElement>("#products-table-body")!,
    productForm: document.querySelector<HTMLFormElement>("#product-form")!,
    categorySelect: document.querySelector<HTMLSelectElement>("#category")!,
    formTitle: document.querySelector<HTMLHeadingElement>("#form-title")!,
    btnSubmit: document.querySelector<HTMLButtonElement>("#btn-submit")!,
    btnCancel: document.querySelector<HTMLButtonElement>("#btn-cancel")!,
    btnOpenModal: document.querySelector<HTMLButtonElement>("#btn-open-add-modal")!,
    modalContainer: document.querySelector<HTMLElement>("#product-modal-container")!,
    inputId: document.querySelector<HTMLInputElement>("#product-id")!,
    inputNombre: document.querySelector<HTMLInputElement>("#nombre")!,
    inputPrecio: document.querySelector<HTMLInputElement>("#precio")!,
    inputStock: document.querySelector<HTMLInputElement>("#stock")!,
    inputDescripcion: document.querySelector<HTMLTextAreaElement>("#descripcion")!,
    inputImagen: document.querySelector<HTMLInputElement>("#imagen")!,
    inputDisponible: document.querySelector<HTMLInputElement>("#disponible")!,
});

export const productsController = {
    /**
     * Punto de entrada del módulo SPA solicitado por home.controller.ts.
     * Inyecta la estructura HTML en el contenedor principal y activa los servicios relacionales.
     */
    async init(targetContainer: HTMLElement): Promise<void> {
        // Inyectamos la estructura limpia incluyendo el botón superior y el Form como Modal Popup
        targetContainer.innerHTML = `
          <div class="admin-header-row">
            <h2>Gestión de Productos</h2>
            <button id="btn-open-add-modal" class="btn btn--tertiary">+ Nuevo Producto</button>
          </div>

          <div class="table-responsive">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>ID (UUID)</th>
                  <th>Imagen</th>
                  <th>Nombre</th>
                  <th>Categoría</th>
                  <th>Descripción</th>
                  <th>Precio</th>
                  <th>Stock</th>
                  <th>Disponible</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody id="products-table-body">
                <tr><td colspan="9" class="text-center">Cargando catálogo...</td></tr>
              </tbody>
            </table>
          </div>

          <div id="product-modal-container" class="admin-modal-overlay hidden">
            <div class="form-container admin-modal-content">
              <h2 id="form-title">Agregar Nuevo Producto</h2>
              <form id="product-form" novalidate>
                <input type="hidden" id="product-id">

                <div class="form-group">
                  <label for="nombre">Nombre del Producto</label>
                  <input type="text" id="nombre" required placeholder="Ej: Hamburguesa Triple Smash">
                </div>

                <div class="form-group">
                  <label for="category">Categoría</label>
                  <select id="category" required></select>
                </div>

                <div class="form-group-row">
                  <div class="form-group">
                    <label for="precio">Precio ($)</label>
                    <input type="number" id="precio" min="0" required placeholder="0.00">
                  </div>
                  <div class="form-group">
                    <label for="stock">Stock Inicial</label>
                    <input type="number" id="stock" min="0" required placeholder="0">
                  </div>
                </div>

                <div class="form-group">
                  <label for="descripcion">Descripción</label>
                  <textarea id="descripcion" required placeholder="Detalle de ingredientes..."></textarea>
                </div>

                <div class="form-group">
                  <label for="imagen">Nombre del archivo de imagen</label>
                  <input type="text" id="imagen" required placeholder="Ej: lomo.jpg">
                </div>

                <div class="form-group form-group--checkbox">
                  <input type="checkbox" id="disponible" checked>
                  <label for="disponible">Habilitar visibilidad (Producto disponible para la venta)</label>
                </div>

                <div class="form-actions">
                  <button type="submit" class="btn btn btn--tertiary" id="btn-submit">Guardar Producto</button>
                  <button type="button" class="btn btn btn--secondary" id="btn-cancel">Cancelar</button>
                </div>
              </form>
            </div>
          </div>
        `;

        isEditingMode = false;

        // Orquestamos la carga de datos paralela y el enlazado de eventos
        await this.showProducts();
        this.initProductEvents();
    },

    /**
     * Carga y renderiza en paralelo las categorías y la grilla relacional de productos.
     */
    async showProducts(): Promise<void> {
        const dom = getElements();

        try {
            const categories: ICategory[] = await categoryService.getAll();
            dom.categorySelect.innerHTML = '<option value="">Seleccione una categoría</option>';
            categoriesMap.clear();

            categories.forEach((cat: ICategory): void => {
                categoriesMap.set(cat.id, cat.name);
                const option: HTMLOptionElement = document.createElement("option");
                option.value = cat.id;
                option.textContent = cat.name;
                dom.categorySelect.appendChild(option);
            });

            await this.refreshTable();
        } catch (error) {
            console.error("Error al poblar los datos iniciales del panel:", error);
        }
    },

    /**
     * Limpia y redibuja la tabla mostrando TODOS los productos (disponibles o no).
     */
    async refreshTable(): Promise<void> {
        const dom = getElements();
        dom.tableBody.innerHTML = '<tr><td colspan="9" class="text-center">Cargando catálogo...</td></tr>';

        // Traemos todo el universo de productos desde Spring Boot sin filtros de exclusión
        const products = await productService.getAll();
        dom.tableBody.innerHTML = "";

        if (products.length === 0) {
            dom.tableBody.innerHTML =
                '<tr><td colspan="9" class="text-center">No hay productos en el sistema.</td></tr>';
            return;
        }

        products.forEach((product) => {
            const tr: HTMLTableRowElement = document.createElement("tr");
            const categoryName: string = categoriesMap.get(product.categoryId || "") || "Sin Categoría";

            // 🚀 CONTROL RIGUROSO DE VISUALIZACIÓN DE DISPONIBILIDAD
            // Si es false, se renderiza un badge gris con la palabra "No", sin ocultar la fila
            const availabilityBadge = product.isAvailable
                ? `<span class="badge" style="background-color: #e8f5e9; color: #2e7d32; font-weight: bold;">Sí</span>`
                : `<span class="badge" style="background-color: #efeef0; color: #757575; font-weight: bold;">No</span>`;

            tr.innerHTML = `
                <td class="uuid-cell" title="${product.id}">${product.id.substring(0, 8)}...</td>
                <td><img src="${product.imageUrl}" class="table-img" alt="${product.name}"></td>
                <td class="fw-bold">${product.name}</td>
                <td><span class="badge badge--info">${categoryName}</span></td>
                <td class="desc-cell">${product.description}</td>
                <td class="price-cell">$${product.price.toFixed(2)}</td>
                <td>
                    <span class="stock-indicator ${product.stock <= 3 ? "stock-low" : "stock-ok"}">
                        ${product.stock} u.
                    </span>
                </td>
                <td class="text-center">${availabilityBadge}</td> <td>
                    <div class="actions-wrapper">
                        <button class="btn--action btn-secondary">Editar</button>
                        <button class="btn--action btn-quaternary">Eliminar</button>
                    </div>
                </td>
            `;

            tr.querySelector(".btn-secondary")?.addEventListener("click", (e: Event): void => {
                e.preventDefault();
                this.fillFormForEdit(product);
            });
            tr.querySelector(".btn-quaternary")?.addEventListener(
                "click",
                (): Promise<void> => this.fireDeleteProcess(product.id, product.name),
            );

            dom.tableBody.appendChild(tr);
        });
    },

    /**
     * Abre el modal y lo configura en modo de edición cargando los campos correspondientes.
     */
    fillFormForEdit(product: IProduct): void {
        isEditingMode = true;
        const dom = getElements();

        dom.formTitle.textContent = `Editando Producto: ${product.name}`;
        dom.btnSubmit.textContent = "Actualizar Cambios";
        dom.btnSubmit.className = "btn btn--warning";

        dom.inputId.value = product.id;
        dom.inputNombre.value = product.name;
        dom.inputPrecio.value = product.price.toString();
        dom.inputStock.value = product.stock.toString();
        dom.inputDescripcion.value = product.description;

        // 🚀 CORRECCIÓN: Seteamos el estado booleano usando .checked para chequear el campo
        dom.inputDisponible.checked = product.isAvailable;

        const urlParts = product.imageUrl.split("/");
        dom.inputImagen.value = urlParts[urlParts.length - 1] || "default-food.jpg";
        dom.categorySelect.value = product.categoryId || "";

        // Mostramos el popup removiendo la clase hidden
        dom.modalContainer.classList.remove("hidden");
    },

    /**
     * Oculta el popup flotante y resetea los campos.
     */
    clearForm(): void {
        isEditingMode = false;
        const dom = getElements();

        dom.productForm.reset();
        dom.inputId.value = "";
        dom.formTitle.textContent = "Agregar Nuevo Producto";
        dom.btnSubmit.textContent = "Guardar Producto";
        dom.btnSubmit.className = "btn btn--tertiary";

        // Dejamos el checkbox tildado por defecto al limpiar para nuevos registros
        dom.inputDisponible.checked = true;

        dom.modalContainer.classList.add("hidden");
    },

    /**
     * Enlaza los escuchadores globales del módulo.
     */
    initProductEvents(): void {
        const dom = getElements();

        // Escuchador para abrir el modal en modo "Nuevo Producto"
        dom.btnOpenModal?.addEventListener("click", (): void => {
            this.clearForm();
            dom.modalContainer.classList.remove("hidden");
        });

        // Manejador del submit del formulario
        dom.productForm.addEventListener("submit", async (e: Event): Promise<void> => {
            e.preventDefault();

            if (
                !dom.inputNombre.value.trim() ||
                !dom.categorySelect.value ||
                !dom.inputPrecio.value ||
                !dom.inputStock.value
            ) {
                alert("Por favor, completá todos los campos obligatorios.");
                return;
            }

            const priceNum: number = parseFloat(dom.inputPrecio.value);
            const stockNum: number = parseInt(dom.inputStock.value, 10);

            if (priceNum < 0 || stockNum < 0) {
                alert("El precio y el stock no pueden ser negativos.");
                return;
            }

            try {
                const imgName = dom.inputImagen.value.trim() || "default-food.jpg";

                // 🚀 Capturamos el estado real del checkbox en vivo
                const isCheckedAvailability = dom.inputDisponible.checked;

                if (isEditingMode) {
                    await productService.update({
                        id: dom.inputId.value,
                        name: dom.inputNombre.value.trim(),
                        price: priceNum,
                        description: dom.inputDescripcion.value.trim(),
                        stock: stockNum,
                        isAvailable: isCheckedAvailability, // Mapeado dinámico corregido
                        categoryId: dom.categorySelect.value,
                        isDeleted: false,
                        createdAt: new Date().toISOString(),
                        imageUrl: `/img/products/${imgName}`,
                        imageFileName: imgName,
                    });
                    alert("Producto modificado correctamente.");
                } else {
                    await productService.create({
                        name: dom.inputNombre.value.trim(),
                        price: priceNum,
                        description: dom.inputDescripcion.value.trim(),
                        stock: stockNum,
                        isAvailable: isCheckedAvailability, // Mapeado dinámico corregido
                        categoryId: dom.categorySelect.value,
                        isDeleted: false,
                        createdAt: new Date().toISOString(),
                        imageFileName: imgName,
                    });
                    alert("Nuevo producto persistido en la base de datos.");
                }

                this.clearForm();
                await this.refreshTable();
            } catch (error: any) {
                alert(error.response?.data?.message || "Error al procesar la solicitud perimetral.");
            }
        });

        dom.btnCancel.addEventListener("click", (): void => this.clearForm());

        // Cerrar el popup si hacen click en el fondo oscuro difuminado fuera del formulario
        dom.modalContainer.addEventListener("click", (e: Event) => {
            if (e.target === dom.modalContainer) {
                this.clearForm();
            }
        });
    },

    /**
     * Orquesta la confirmación y ejecución del Soft Delete lógico en la API.
     */
    async fireDeleteProcess(id: string, name: string): Promise<void> {
        if (!confirm(`¿Estás seguro de que deseas dar de baja "${name}"?`)) return;

        try {
            await productService.delete(id);
            alert(`"${name}" fue dado de baja del catálogo.`);
            await this.refreshTable();
        } catch (error: any) {
            alert(error.response?.data?.message || "Operación rechazada por el Interceptor perimetral.");
        }
    },
};
