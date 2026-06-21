import type { ICategory } from "@interfaces/Category.interface";
import { categoryService } from "@services/category.service";

// Estado local encapsulado del módulo de categorías
let isEditingMode: boolean = false;

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
                  <th>Nombre de la Categoría</th>
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
    },

    /**
     * Limpia y redibuja la tabla con datos frescos consumiendo tu servicio de Spring Boot.
     */
    async refreshTable(): Promise<void> {
        const dom = getElements();
        dom.tableBody.innerHTML = '<tr><td colspan="5" class="text-center">Cargando categorías...</td></tr>';

        try {
            const categories = await categoryService.getAll();
            dom.tableBody.innerHTML = "";

            if (categories.length === 0) {
                dom.tableBody.innerHTML =
                    '<tr><td colspan="5" class="text-center">No hay categorías cargadas en el sistema.</td></tr>';
                return;
            }

            categories.forEach((category: ICategory) => {
                const tr: HTMLTableRowElement = document.createElement("tr");

                // Formateamos la fecha de creación ISO de forma amigable (DD/MM/AAAA)
                const createdDate = category.createdAt ? new Date(category.createdAt).toLocaleDateString() : "N/A";

                tr.innerHTML = `
                    <td class="uuid-cell" title="${category.id}">${category.id.substring(0, 8)}...</td>
                    <td class="fw-bold">${category.name}</td>
                    <td class="desc-cell">${category.description || "Sin descripción asociada."}</td>
                    <td>
                        <div class="actions-wrapper">
                            <button class="btn btn--action btn-secondary">Editar</button>
                            <button class="btn btn--action btn-quaternary">Eliminar</button>
                        </div>
                    </td>
                `;

                // Escuchadores de eventos para los botones de acción usando preventDefault preventivo
                tr.querySelector(".btn-secondary")?.addEventListener("click", (e: Event): void => {
                    e.preventDefault();
                    this.fillFormForEdit(category);
                });

                tr.querySelector(".btn-quaternary")?.addEventListener(
                    "click",
                    (): Promise<void> => this.fireDeleteProcess(category.id, category.name),
                );

                dom.tableBody.appendChild(tr);
            });
        } catch (error) {
            console.error("Error al refrescar la grilla de categorías:", error);
            dom.tableBody.innerHTML =
                '<tr><td colspan="5" class="text-center error-text">Error de comunicación con el backend relacional.</td></tr>';
        }
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

            try {
                if (isEditingMode) {
                    // Flujo PUT: Pasamos el objeto completo mapeado a la firma del servicio
                    await categoryService.update({
                        id: dom.inputId.value,
                        name: dom.inputNombre.value.trim(),
                        description: dom.inputDescripcion.value.trim(),
                        isDeleted: false,
                        createdAt: new Date().toISOString(),
                    });
                    alert("Categoría modificada con éxito.");
                } else {
                    // Flujo POST: Creación de nueva categoría
                    await categoryService.create({
                        name: dom.inputNombre.value.trim(),
                        description: dom.inputDescripcion.value.trim(),
                        isDeleted: false,
                        createdAt: new Date().toISOString(),
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
