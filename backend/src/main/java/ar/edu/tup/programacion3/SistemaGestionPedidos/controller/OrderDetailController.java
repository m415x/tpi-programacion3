package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.OrderDetailService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.groups.OnCreate;
import java.util.List;
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
	public OrderDetailResponseDTO updateOrderDetail(
			@PathVariable Long id,
			@Validated @RequestBody OrderDetailRequestDTO dto) {

		return service.update(dto, id);
	}


	@PatchMapping("/{id}")
	public OrderDetailResponseDTO partialUpdateOrderDetail(@PathVariable Long id, @RequestBody OrderDetailRequestDTO dto) {

		return service.partialUpdate(dto, id);
	}

	@DeleteMapping("/{id}")
	public void deleteOrderDetail(@PathVariable Long id) {

		service.deleteById(id);
	}

	@GetMapping("/{id}")
	public OrderDetailResponseDTO findOrderDetail(@PathVariable Long id) {

		return service.findById(id);
	}

	@GetMapping
	public List<OrderDetailResponseDTO> findAllOrderDetails() {
		return service.findAll();
	}

	// ENDPOINTS HISTÓRICOS (Separados para proteger el flujo principal)
	@GetMapping("/history")
	public List<OrderDetailResponseDTO> getHistoricalOrderDetails() {
		return service.getHistoricalOrderDetails();
	}

	@GetMapping("/{id}/history")
	public OrderDetailResponseDTO findHistoricalOrderDetail(@PathVariable Long id) {
		return service.findHistoricalOrderDetail(id);
	}
}
