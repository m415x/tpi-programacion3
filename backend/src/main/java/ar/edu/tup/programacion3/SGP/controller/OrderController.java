package ar.edu.tup.programacion3.SGP.controller;

import java.util.List;
import java.util.UUID;
import java.util.Map;

import ar.edu.tup.programacion3.SGP.model.enums.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.tup.programacion3.SGP.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SGP.service.OrderService;
import ar.edu.tup.programacion3.SGP.validator.AdminRequired;
import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import ar.edu.tup.programacion3.SGP.validator.groups.OnUpdate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService service;

	@PostMapping
	public ResponseEntity<OrderResponseDTO> saveOrder(
			@Validated(OnCreate.class) @RequestBody OrderRequestDTO dto) {

		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDTO> findOrder(@PathVariable UUID id) {

		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping
	public ResponseEntity<List<OrderResponseDTO> > findOrders() {

		return ResponseEntity.ok(service.findAll());
	}

	@PutMapping("/{id}")
	@AdminRequired
	public ResponseEntity<OrderResponseDTO> updateOrder(
			@PathVariable UUID id, @Valid @RequestBody OrderRequestDTO dto) {

		return ResponseEntity.ok(service.update(dto, id));
	}

	@PatchMapping("/{id}")
	@AdminRequired
	public ResponseEntity<OrderResponseDTO> partialUpdateOrder(
			@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody OrderRequestDTO dto) {

		return ResponseEntity.ok(service.partialUpdate(dto, id));
	}

	@PatchMapping("/{id}/status")
	@AdminRequired
	public ResponseEntity<OrderResponseDTO> updateOrderStatus(
			@PathVariable UUID id,
			@RequestBody Map<String, String> body) {

		String statusStr = body.get("status");
		OrderStatus newStatus = OrderStatus.valueOf(statusStr);

		return ResponseEntity.ok(service.updateStatus(id, newStatus));
	}

	@DeleteMapping("/{id}")
	@AdminRequired
	public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
		service.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	// GESTIÓN DE DETALLES / ELEMENTOS DEL PEDIDO (Sub-recursos)
	/**
	 * Mapea a: POST /orders/{id}/items?productId=X&qty=Y
	 * Añade un nuevo producto al pedido actual de forma controlada.
	 */
	@PostMapping("/{id}/items")
	public ResponseEntity<OrderResponseDTO> addItemToOrder(
			@PathVariable("id") UUID orderId,
			@RequestParam UUID productId,
			@RequestParam Integer qty) {

		return ResponseEntity.ok(service.addProductToOrder(orderId, qty, productId));
	}

	/**
	 * Mapea a: PUT /orders/{id}/items/{productId}?qty=Y
	 * Actualiza la cantidad de un ítem existente dentro de la orden.
	 */
	@PutMapping("/{id}/items/{productId}")
	public ResponseEntity<OrderResponseDTO> updateItemQty(
			@PathVariable("id") UUID orderId,
			@PathVariable UUID productId,
			@RequestParam("qty") Integer newQty) {

		return ResponseEntity.ok(service.updateQtyItem(orderId, productId, newQty));
	}

	/**
	 * Mapea a: GET /orders/{id}/items/count
	 * Devuelve la sumatoria total de ítems en el pedido actual.
	 */
	@GetMapping("/{id}/items/count")
	public ResponseEntity<UUID> getItemCount(@PathVariable UUID id) {

		return ResponseEntity.ok(service.getQtyItems(id));
	}

	// ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
	@GetMapping("/history")
	@AdminRequired
	public ResponseEntity<List<OrderResponseDTO> > getHistoricalOrders() {

		return ResponseEntity.ok(service.getHistoricalOrders());
	}

	@GetMapping("/{id}/history")
	@AdminRequired
	public ResponseEntity<OrderResponseDTO> findHistoricalOrder(@PathVariable UUID id) {

		return ResponseEntity.ok(service.findHistoricalOrder(id));
	}
}
