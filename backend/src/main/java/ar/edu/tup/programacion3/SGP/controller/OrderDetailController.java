package ar.edu.tup.programacion3.SGP.controller;

import ar.edu.tup.programacion3.SGP.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SGP.service.OrderDetailService;
import ar.edu.tup.programacion3.SGP.validator.AdminRequired;
import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService service;

	@PostMapping
	public ResponseEntity<OrderDetailResponseDTO> saveOrderDetail(
			@Validated(OnCreate.class) @RequestBody OrderDetailRequestDTO dto) {

		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
	}

	@PutMapping("/{id}")
	@AdminRequired
	public ResponseEntity<OrderDetailResponseDTO> updateOrderDetail(
			@PathVariable UUID id,
			@Validated @RequestBody OrderDetailRequestDTO dto) {

		return ResponseEntity.ok(service.update(dto, id));
	}


	@PatchMapping("/{id}")
	@AdminRequired
	public ResponseEntity<OrderDetailResponseDTO> partialUpdateOrderDetail(@PathVariable UUID id, @RequestBody OrderDetailRequestDTO dto) {

		return ResponseEntity.ok(service.partialUpdate(dto, id));
	}

	@DeleteMapping("/{id}")
	@AdminRequired
	public ResponseEntity<Void> deleteOrderDetail(@PathVariable UUID id) {
		service.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderDetailResponseDTO> findOrderDetail(@PathVariable UUID id) {

		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping
	@AdminRequired
	public ResponseEntity<List<OrderDetailResponseDTO>> findAllOrderDetails() {
		return ResponseEntity.ok(service.findAll());
	}

	// ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
	@GetMapping("/history")
	@AdminRequired
	public ResponseEntity<List<OrderDetailResponseDTO>> getHistoricalOrderDetails() {
		return ResponseEntity.ok(service.getHistoricalOrderDetails());
	}

	@GetMapping("/{id}/history")
	@AdminRequired
	public ResponseEntity<OrderDetailResponseDTO> findHistoricalOrderDetail(@PathVariable UUID id) {
		return ResponseEntity.ok(service.findHistoricalOrderDetail(id));
	}
}
