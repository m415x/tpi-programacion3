import type { ICategory } from "@interfaces/Category.interface";
import { categoryService } from "@services/category.service";

// Estado local encapsulado del módulo de categorías
let isEditingMode: boolean = false;
let currentCategoriesList: ICategory[] = [];

let categorySortDirections: Record<string, "ASC" | "DESC"> = {
    name: "ASC",
};

// Helpers para obtener referencias diferidas del DOM tras la inyección en la SPA
const getElements = () => ({
    tableBody: document.querySelector<HTMLTableSectionElement>("#categories-table-body")!,
    categoryForm: document.querySelector<HTMLFormElement>("#category-form")!,
    formTitle: document.querySelector<HTMLHeadingElement>("#form-title")!,
    btnSubmit: document.querySelector<HTMLButtonElement>("#btn-submit")!,
    btnCancel: document.querySelector<HTMLButtonElement>("#btn-cancel")!,
    btnOpenModal: document.querySelector<HTMLButtonElement>("#btn-open-category-modal")!,
    modalContainer: document.querySelector<HTMLElement>("#category-modal-container")!,
    inputId: document.querySelector<HTMLInputElement>("#category-id")!,
    inputNombre: document.querySelector<HTMLInputElement>("#category-name")!,
    inputDescripcion: document.querySelector<HTMLTextAreaElement>("#category-description")!,
    inputImagen: document.querySelector<HTMLInputElement>("#category-image")!,
});

export const categoriesController = {
    /**
     * Punto de entrada del módulo SPA invocado por el enrutador de home.controller.ts.
     */
    async init(targetContainer: HTMLElement): Promise<void> {
        // Inyectamos la estructura limpia: Cabecera simétrica, Tabla fija y Form en Modal Popup
        targetContainer.innerHTML = `
          <div class="admin-header-row">
            <h2>Gestión de Categorías</h2>
            <button id="btn-open-category-modal" class="btn btn--tertiary">+ Nueva Categoría</button>
          </div>

          <div class="table-responsive">
            <table class="admin-table">
              <thead>
                <tr>
                  <th>ID (UUID)</th>
                  <th>Imagen</th>
                  <th class="sortable-header" data-sort="name">Nombre</th>
                  <th>Descripción / Detalles</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody id="categories-table-body">
                <tr><td colspan="5" class="text-center">Cargando categorías...</td></tr>
              </tbody>
            </table>
          </div>

          <div id="category-modal-container" class="admin-modal-overlay hidden">
            <div class="form-container admin-modal-content">
              <h2 id="form-title">Agregar Nueva Categoría</h2>
              <form id="category-form" novalidate>
                <input type="hidden" id="category-id">

                <div class="form-group">
                  <label for="category-name">Nombre de la Categoría</label>
                  <input type="text" id="category-name" required placeholder="Ej: Hamburguesas, Bebidas, Postres">
                </div>

                <div class="form-group">
                  <label for="category-description">Descripción</label>
                  <textarea id="category-description" required placeholder="Breve descripción del grupo de menú..."></textarea>
                </div>

                <div class="form-group">
                  <label for="category-image">Nombre del archivo de imagen</label>
                  <input type="text" id="category-image" required placeholder="Ej: hamburguesas.jpg">
                </div>

                <div class="form-actions">
                  <button type="submit" class="btn btn--tertiary" id="btn-submit">Guardar Categoría</button>
                  <button type="button" class="btn btn--secondary" id="btn-cancel">Cancelar</button>
                </div>
              </form>
            </div>
          </div>
        `;

        isEditingMode = false;

        // Renderizamos las filas dinámicas y enlazamos los listeners de la UI
        await this.refreshTable();
        this.initCategoryEvents();

        // Escuchador de ordenamiento para categorías
        const tableHeader = targetContainer.querySelector("thead");
        tableHeader?.addEventListener("click", (e: Event) => {
            const th = (e.target as HTMLElement).closest(".sortable-header");
            if (!th) return;

            const sortBy = th.getAttribute("data-sort")!;
            this.sortCategoriesBy(sortBy);
        });
    },

    /**
     * Renderiza las filas de la tabla de categorías
     */
    renderTableRows(): void {
        const dom = getElements();
        dom.tableBody.innerHTML = "";

        if (currentCategoriesList.length === 0) {
            dom.tableBody.innerHTML =
                '<tr><td colspan="5" class="text-center">No hay categorías cargadas en el sistema.</td></tr>';
            return;
        }

        currentCategoriesList.forEach((category: ICategory) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="uuid-cell" title="${category.id}">${category.id.substring(0, 8)}...</td>
                <td><img src="${category.image}" class="table-img" alt="${category.name}"></td>
                <td class="fw-bold">${category.name}</td>
                <td class="desc-cell">${category.description || "Sin descripción asociada."}</td>
                <td>
                    <div class="actions-wrapper">
                        <button class="btn btn--action btn-secondary">Editar</button>
                        <button class="btn btn--action btn-quaternary">Eliminar</button>
                    </div>
                </td>
            `;

            tr.querySelector(".btn-secondary")?.addEventListener("click", (e: Event) => {
                e.preventDefault();
                this.fillFormForEdit(category);
            });
            tr.querySelector(".btn-quaternary")?.addEventListener("click", () =>
                this.fireDeleteProcess(category.id, category.name),
            );

            dom.tableBody.appendChild(tr);
        });
    },

    /**
     * Limpia y redibuja la tabla con datos frescos consumiendo tu servicio de Spring Boot.
     */
    async refreshTable(): Promise<void> {
        currentCategoriesList = await categoryService.getAll();
        this.renderTableRows();
    },

    /**
     * Ordenador dinámico de categorías
     */
    sortCategoriesBy(field: string): void {
        const direction = categorySortDirections[field];

        currentCategoriesList.sort((a, b) => {
            let valA = a[field as keyof ICategory] || "";
            let valB = b[field as keyof ICategory] || "";

            if (typeof valA === "string") valA = valA.toLowerCase();
            if (typeof valB === "string") valB = valB.toLowerCase();

            if (valA < valB) return direction === "ASC" ? -1 : 1;
            if (valA > valB) return direction === "ASC" ? 1 : -1;
            return 0;
        });

        categorySortDirections[field] = direction === "ASC" ? "DESC" : "ASC";
        this.renderTableRows();
    },

    /**
     * Levanta el popup modal y precarga los campos para el flujo de modificación (PUT).
     */
    fillFormForEdit(category: ICategory): void {
        isEditingMode = true;
        const dom = getElements();

        dom.formTitle.textContent = `Editando Categoría: ${category.name}`;
        dom.btnSubmit.textContent = "Actualizar Cambios";
        dom.btnSubmit.className = "btn btn--warning";

        dom.inputId.value = category.id;
        dom.inputNombre.value = category.name;
        dom.inputDescripcion.value = category.description || "";

        // Extraemos el nombre de archivo plano
        if (category.image) {
            dom.inputImagen.value = category.image.replace("/img/categories/", "");
        } else {
            dom.inputImagen.value = "";
        }

        // Quitamos la clase hidden para que emerja el popup flotante
        dom.modalContainer.classList.remove("hidden");
    },

    /**
     * Oculta el popup modal y limpia por completo el formulario.
     */
    clearForm(): void {
        isEditingMode = false;
        const dom = getElements();

        dom.categoryForm.reset();
        dom.inputId.value = "";
        dom.formTitle.textContent = "Agregar Nueva Categoría";
        dom.btnSubmit.textContent = "Guardar Categoría";
        dom.btnSubmit.className = "btn btn--tertiary";

        dom.modalContainer.classList.add("hidden");
    },

    /**
     * Registra los escuchadores globales de Submit y Cancelación.
     */
    initCategoryEvents(): void {
        const dom = getElements();

        // Apertura del modal para un alta limpia (POST)
        dom.btnOpenModal?.addEventListener("click", (): void => {
            this.clearForm();
            dom.modalContainer.classList.remove("hidden");
        });

        // Manejo del envío del formulario (POST / PUT) hacia Spring Boot
        dom.categoryForm.addEventListener("submit", async (e: Event): Promise<void> => {
            e.preventDefault();

            if (!dom.inputNombre.value.trim() || !dom.inputDescripcion.value.trim()) {
                alert("Por favor, completá todos los campos obligatorios.");
                return;
            }

            // Capturamos el nombre de archivo plano o aplicamos un fallback si viene vacío
            const imageFile = dom.inputImagen?.value.trim() || "default-food.jpg";

            try {
                if (isEditingMode) {
                    // Flujo PUT: Pasamos el objeto de dominio extendido con la propiedad imageFileName
                    await categoryService.update({
                        id: dom.inputId.value,
                        name: dom.inputNombre.value.trim(),
                        description: dom.inputDescripcion.value.trim(),
                        isDeleted: false,
                        createdAt: new Date().toISOString(),
                        image: dom.inputImagen.value.trim(),
                        imageFileName: imageFile,
                    });
                    alert("Categoría modificada con éxito.");
                } else {
                    // Flujo POST: Creación omitiendo id e img, intersecando imageFileName
                    await categoryService.create({
                        name: dom.inputNombre.value.trim(),
                        description: dom.inputDescripcion.value.trim(),
                        isDeleted: false,
                        createdAt: new Date().toISOString(),
                        imageFileName: imageFile,
                    });
                    alert("Nueva categoría almacenada en la base de datos.");
                }

                this.clearForm();
                await this.refreshTable();
            } catch (error: any) {
                console.error("Error al procesar categoría:", error);
                alert(error.response?.data?.message || "Acceso denegado: Verifique los privilegios de Administrador.");
            }
        });

        // Cancelación manual desde el botón
        dom.btnCancel.addEventListener("click", (): void => this.clearForm());

        // Cierre defensivo si hacen click sobre el fondo oscuro translúcido
        dom.modalContainer.addEventListener("click", (e: Event) => {
            if (e.target === dom.modalContainer) {
                this.clearForm();
            }
        });
    },
    /**
     * Orquesta el borrado lógico a través del método DELETE de tu API REST.
     */
    async fireDeleteProcess(id: string, name: string): Promise<void> {
        if (
            !confirm(
                `¿Estás seguro de que deseas eliminar la categoría "${name}"?\nEsto alterará la visualización de los productos asociados.`,
            )
        ) {
            return;
        }

        try {
            await categoryService.delete(id);
            alert(`La categoría "${name}" fue dada de baja correctamente.`);
            await this.refreshTable();
        } catch (error: any) {
            console.error("Error al eliminar categoría:", error);
            alert(error.response?.data?.message || "El interceptor perimetral rechazó la operación de borrado.");
        }
    },
};
