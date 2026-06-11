package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailResponseDTO;

import java.util.List;

public interface OrderDetailService {

    public OrderDetailResponseDTO save(OrderDetailRequestDTO OrderDetailRequestDTO);

    public OrderDetailResponseDTO findById(Long id);

    public List<OrderDetailResponseDTO> findAll();

    public OrderDetailResponseDTO update(OrderDetailRequestDTO orderDetailRequestDTO, Long id);

    public void deleteById(Long id);

    OrderDetailResponseDTO findHistoricalOrderDetail(Long id);

    List<OrderDetailResponseDTO> getHistoricalOrderDetails();
}
