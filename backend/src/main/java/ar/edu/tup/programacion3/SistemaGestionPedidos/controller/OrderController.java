package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.OrderService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnUpdate;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderService service;

	@PostMapping
	public OrderResponseDTO saveOrder(
			@Validated(OnCreate.class) @RequestBody OrderRequestDTO dto) {

		return service.save(dto);
	}

	@GetMapping("/{id}")
	public OrderResponseDTO findOrder(@PathVariable Long id) {

		return service.findById(id);
	}

	@GetMapping
	public List<OrderResponseDTO> findOrders() {

		return service.findAll();
	}

	@PutMapping("/{id}")
	public OrderResponseDTO updateOrder(
			@PathVariable Long id, @Valid @RequestBody OrderRequestDTO dto) {

		return service.update(dto, id);
	}

	@PatchMapping("/{id}")
	public OrderResponseDTO partialUpdateOrder(
			@PathVariable Long id, @Validated(OnUpdate.class) @RequestBody OrderRequestDTO dto) {

		return service.partialUpdate(dto, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		service.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	// GESTIÓN DE DETALLES / ELEMENTOS DEL PEDIDO (Sub-recursos)
	/**
	 * Mapea a: POST /orders/{id}/items?productId=X&qty=Y
	 * Añade un nuevo producto al pedido actual de forma controlada.
	 */
	@PostMapping("/{id}/items")
	public OrderResponseDTO addItemToOrder(
			@PathVariable("id") Long orderId,
			@RequestParam Long productId,
			@RequestParam Integer qty) {

		return service.addProductToOrder(orderId, qty, productId);
	}

	/**
	 * Mapea a: PUT /orders/{id}/items/{productId}?qty=Y
	 * Actualiza la cantidad de un ítem existente dentro de la orden.
	 */
	@PutMapping("/{id}/items/{productId}")
	public OrderResponseDTO updateItemQty(
			@PathVariable("id") Long orderId,
			@PathVariable Long productId,
			@RequestParam("qty") Integer newQty) {

		return service.updateQtyItem(orderId, productId, newQty);
	}

	/**
	 * Mapea a: GET /orders/{id}/items/count
	 * Devuelve la sumatoria total de ítems en el pedido actual.
	 */
	@GetMapping("/{id}/items/count")
	public Long getItemCount(@PathVariable Long id) {

		return service.getQtyItems(id);
	}

	// ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
	@GetMapping("/history")
	public List<OrderResponseDTO> getHistoricalOrders() {

		return service.getHistoricalOrders();
	}

	@GetMapping("/{id}/history")
	public OrderResponseDTO findHistoricalOrder(@PathVariable Long id) {

		return service.findHistoricalOrder(id);
	}
}
