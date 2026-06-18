package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.CategoryService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnUpdate;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public CategoryResponseDTO saveCategory(
            @Validated(OnCreate.class) @RequestBody CategoryRequestDTO dto) {

        return service.save(dto);
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findCategory(@PathVariable Long id) {

        return service.findById(id);
    }

    @GetMapping
    public List<CategoryResponseDTO> findCategories(@RequestParam(required = false) String name) {

        if (name != null && !name.trim().isEmpty()) {

			return service.findCategoriesByName(name);
        }

        return service.findAll();
    }

    @PutMapping("/{id}")
    public CategoryResponseDTO updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequestDTO dto) {

        return service.update(dto, id);
    }

    @PatchMapping("/{id}")
    public CategoryResponseDTO partialUpdateCategory(
            @PathVariable Long id, @Validated(OnUpdate.class) @RequestBody CategoryRequestDTO dto) {

        return service.partialUpdate(dto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    public List<CategoryResponseDTO> getHistoricalCategories() {

		return service.getHistoricalCategories();
    }

    @GetMapping("/{id}/history")
    public CategoryResponseDTO findHistoricalCategory(@PathVariable Long id) {

       return service.findHistoricalCategory(id);
    }
}
