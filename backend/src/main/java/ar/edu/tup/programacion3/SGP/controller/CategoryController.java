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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    @AdminRequired
    public ResponseEntity<CategoryResponseDTO> saveCategory(
            @Validated(OnCreate.class) @RequestBody CategoryRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findCategory(@PathVariable UUID id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findCategories(
            @RequestParam(required = false) String name) {

        if (name != null && !name.trim().isEmpty()) {

            return ResponseEntity.ok(service.findCategoriesByName(name));
        }

        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @AdminRequired
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO dto) {

        return ResponseEntity.ok(service.update(dto, id));
    }

    @PatchMapping("/{id}")
    @AdminRequired
    public ResponseEntity<CategoryResponseDTO> partialUpdateCategory(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody CategoryRequestDTO dto) {

        return ResponseEntity.ok(service.partialUpdate(dto, id));
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
    public ResponseEntity<List<CategoryResponseDTO>> getHistoricalCategories() {

        return ResponseEntity.ok(service.getHistoricalCategories());
    }

    @GetMapping("/{id}/history")
    @AdminRequired
    public ResponseEntity<CategoryResponseDTO> findHistoricalCategory(@PathVariable UUID id) {

        return ResponseEntity.ok(service.findHistoricalCategory(id));
    }
}
