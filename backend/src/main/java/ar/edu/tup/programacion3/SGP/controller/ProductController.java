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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    @AdminRequired
    public ProductResponseDTO saveProduct(
            @Validated(OnCreate.class) @RequestBody ProductRequestDTO dto) {

        return service.save(dto);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO findProduct(@PathVariable UUID id) {

        return service.findById(id);
    }

    @GetMapping
    public List<ProductResponseDTO> findProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Integer lowStock) {

        if (name != null && !name.trim().isEmpty()) {

			return service.findProductsByName(name);
        }

        if (available != null) {

			return service.getProductsByAvailability(available);
        }

        if (lowStock != null && lowStock > 0) {

			return service.getLowStockProducts(lowStock);
        }

        return service.findAll();
    }

    @PutMapping("/{id}")
    @AdminRequired
    public ProductResponseDTO updateProduct(
            @PathVariable UUID id, @Valid @RequestBody ProductRequestDTO dto) {

        return service.update(dto, id);
    }

    @PatchMapping("/{id}")
    @AdminRequired
    public ProductResponseDTO partialUpdateProduct(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody ProductRequestDTO dto) {

        return service.partialUpdate(dto, id);
    }

    @DeleteMapping("/{id}")
    @AdminRequired
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    @AdminRequired
    public List<ProductResponseDTO> getHistoricalProducts() {

		return service.getHistoricalProducts();
    }

    @GetMapping("/{id}/history")
    @AdminRequired
    public ProductResponseDTO findHistoricalProduct(@PathVariable UUID id) {

		return service.findHistoricalProduct(id);
    }
}
