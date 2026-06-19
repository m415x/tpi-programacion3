package ar.edu.tup.programacion3.SGP.controller;

import ar.edu.tup.programacion3.SGP.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SGP.service.OrderDetailService;
import ar.edu.tup.programacion3.SGP.validator.AdminRequired;
import ar.edu.tup.programacion3.SGP.validator.groups.OnCreate;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-details")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService service;

	@PostMapping
	public OrderDetailResponseDTO saveOrderDetail(
			@Validated(OnCreate.class) @RequestBody OrderDetailRequestDTO dto) {

		return service.save(dto);
	}

	@PutMapping("/{id}")
	@AdminRequired
	public OrderDetailResponseDTO updateOrderDetail(
			@PathVariable UUID id,
			@Validated @RequestBody OrderDetailRequestDTO dto) {

		return service.update(dto, id);
	}


	@PatchMapping("/{id}")
	@AdminRequired
	public OrderDetailResponseDTO partialUpdateOrderDetail(@PathVariable UUID id, @RequestBody OrderDetailRequestDTO dto) {

		return service.partialUpdate(dto, id);
	}

	@DeleteMapping("/{id}")
	@AdminRequired
	public void deleteOrderDetail(@PathVariable UUID id) {

		service.deleteById(id);
	}

	@GetMapping("/{id}")
	public OrderDetailResponseDTO findOrderDetail(@PathVariable UUID id) {

		return service.findById(id);
	}

	@GetMapping
	@AdminRequired
	public List<OrderDetailResponseDTO> findAllOrderDetails() {
		return service.findAll();
	}

	// ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
	@GetMapping("/history")
	@AdminRequired
	public List<OrderDetailResponseDTO> getHistoricalOrderDetails() {
		return service.getHistoricalOrderDetails();
	}

	@GetMapping("/{id}/history")
	@AdminRequired
	public OrderDetailResponseDTO findHistoricalOrderDetail(@PathVariable UUID id) {
		return service.findHistoricalOrderDetail(id);
	}
}
