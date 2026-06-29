package ar.edu.tup.programacion3.SGP.controller;

import ar.edu.tup.programacion3.SGP.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SGP.service.ProductService;
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
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    @AdminRequired
    public ResponseEntity<ProductResponseDTO> saveProduct(
            @Validated(OnCreate.class) @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProduct(@PathVariable UUID id) {

        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Integer lowStock) {

        if (name != null && !name.trim().isEmpty()) {
            return ResponseEntity.ok(service.findProductsByName(name));
        }
        if (available != null) {
            return ResponseEntity.ok(service.getProductsByAvailability(available));
        }
        if (lowStock != null && lowStock > 0) {
            return ResponseEntity.ok(service.getLowStockProducts(lowStock));
        }

        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @AdminRequired
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID id, @Valid @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.ok(service.update(dto, id));
    }

    @PatchMapping("/{id}")
    @AdminRequired
    public ResponseEntity<ProductResponseDTO> partialUpdateProduct(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody ProductRequestDTO dto) {

        return ResponseEntity.ok(service.partialUpdate(dto, id));
    }

    @DeleteMapping("/{id}")
    @AdminRequired
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable UUID categoryId) {

		List<ProductResponseDTO> products = service.findByCategoryId(categoryId);

		return ResponseEntity.ok(products);
	}

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    @AdminRequired
    public ResponseEntity<List<ProductResponseDTO>> getHistoricalProducts() {

        return ResponseEntity.ok(service.getHistoricalProducts());
    }

    @GetMapping("/{id}/history")
    @AdminRequired
    public ResponseEntity<ProductResponseDTO> findHistoricalProduct(@PathVariable UUID id) {

        return ResponseEntity.ok(service.findHistoricalProduct(id));
    }
}
