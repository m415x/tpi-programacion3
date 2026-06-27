package ar.edu.tup.programacion3.SGP.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	public OrderResponseDTO findOrder(@PathVariable UUID id) {

		return service.findById(id);
	}

	@GetMapping
	public List<OrderResponseDTO> findOrders() {

		return service.findAll();
	}

	@PutMapping("/{id}")
	@AdminRequired
	public OrderResponseDTO updateOrder(
			@PathVariable UUID id, @Valid @RequestBody OrderRequestDTO dto) {

		return service.update(dto, id);
	}

	@PatchMapping("/{id}")
	@AdminRequired
	public OrderResponseDTO partialUpdateOrder(
			@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody OrderRequestDTO dto) {

		return service.partialUpdate(dto, id);
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
	public OrderResponseDTO addItemToOrder(
			@PathVariable("id") UUID orderId,
			@RequestParam UUID productId,
			@RequestParam Integer qty) {

		return service.addProductToOrder(orderId, qty, productId);
	}

	/**
	 * Mapea a: PUT /orders/{id}/items/{productId}?qty=Y
	 * Actualiza la cantidad de un ítem existente dentro de la orden.
	 */
	@PutMapping("/{id}/items/{productId}")
	public OrderResponseDTO updateItemQty(
			@PathVariable("id") UUID orderId,
			@PathVariable UUID productId,
			@RequestParam("qty") Integer newQty) {

		return service.updateQtyItem(orderId, productId, newQty);
	}

	/**
	 * Mapea a: GET /orders/{id}/items/count
	 * Devuelve la sumatoria total de ítems en el pedido actual.
	 */
	@GetMapping("/{id}/items/count")
	public UUID getItemCount(@PathVariable UUID id) {

		return service.getQtyItems(id);
	}

	// ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
	@GetMapping("/history")
	@AdminRequired
	public List<OrderResponseDTO> getHistoricalOrders() {

		return service.getHistoricalOrders();
	}

	@GetMapping("/{id}/history")
	@AdminRequired
	public OrderResponseDTO findHistoricalOrder(@PathVariable UUID id) {

		return service.findHistoricalOrder(id);
	}
}
