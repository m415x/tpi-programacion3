package ar.edu.tup.programacion3.SGP.controller;

import ar.edu.tup.programacion3.SGP.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SGP.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SGP.service.CategoryService;
import ar.edu.tup.programacion3.SGP.validator.AdminRequired;
import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.groups.OnUpdate;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

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
    @AdminRequired
    public CategoryResponseDTO saveCategory(
            @Validated(OnCreate.class) @RequestBody CategoryRequestDTO dto) {

        return service.save(dto);
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findCategory(@PathVariable UUID id) {

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
    @AdminRequired
    public CategoryResponseDTO updateCategory(
            @PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO dto) {

        return service.update(dto, id);
    }

    @PatchMapping("/{id}")
    @AdminRequired
    public CategoryResponseDTO partialUpdateCategory(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody CategoryRequestDTO dto) {

        return service.partialUpdate(dto, id);
    }

    @DeleteMapping("/{id}")
    @AdminRequired
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    @AdminRequired
    public List<CategoryResponseDTO> getHistoricalCategories() {

		return service.getHistoricalCategories();
    }

    @GetMapping("/{id}/history")
    @AdminRequired
    public CategoryResponseDTO findHistoricalCategory(@PathVariable UUID id) {

       return service.findHistoricalCategory(id);
    }
}
