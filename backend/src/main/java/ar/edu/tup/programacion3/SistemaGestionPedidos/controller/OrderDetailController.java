package ar.edu.tup.programacion3.SistemaGestionPedidos.controller;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.service.OrderDetailService;
import ar.edu.tup.programacion3.SistemaGestionPedidos.validator.OnCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-detail")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    // AL CREAR: Forzamos la validación del grupo OnCreate (exige productId)
    @PostMapping
    public OrderDetailResponseDTO create(
            @Validated(OnCreate.class) @RequestBody OrderDetailRequestDTO orderDetailRequestDTO) {
        return orderDetailService.save(orderDetailRequestDTO);
    }

    // AL EDITAR: No pasamos grupo (o pasamos OnUpdate), ignorando la restricción de productId
    @PutMapping("/{id}")
    public OrderDetailResponseDTO update(
            @PathVariable Long id,
            @Validated @RequestBody OrderDetailRequestDTO orderDetailRequestDTO) {
        return orderDetailService.update(orderDetailRequestDTO, id);
    }
}
