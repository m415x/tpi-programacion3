package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.ProductService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnUpdate;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ProductResponseDTO saveProduct(
            @Validated(OnCreate.class) @RequestBody ProductRequestDTO dto) {

        return service.save(dto);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO findProduct(@PathVariable Long id) {

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
    public ProductResponseDTO updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {

        return service.update(dto, id);
    }

    @PatchMapping("/{id}")
    public ProductResponseDTO partialUpdateProduct(
            @PathVariable Long id, @Validated(OnUpdate.class) @RequestBody ProductRequestDTO dto) {

        return service.partialUpdate(dto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
    @GetMapping("/history")
    public List<ProductResponseDTO> getHistoricalProducts() {

		return service.getHistoricalProducts();
    }

    @GetMapping("/{id}/history")
    public ProductResponseDTO findHistoricalProduct(@PathVariable Long id) {

		return service.findHistoricalProduct(id);
    }
}
