package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderDetailResponseDTO;

import java.util.List;
import java.util.UUID;

public interface OrderDetailService {

    public OrderDetailResponseDTO save(OrderDetailRequestDTO dto);

    public OrderDetailResponseDTO findById(UUID id);

    public List<OrderDetailResponseDTO> findAll();

    public OrderDetailResponseDTO update(OrderDetailRequestDTO dto, UUID id);

	public OrderDetailResponseDTO partialUpdate(OrderDetailRequestDTO dto, UUID id);

    public void deleteById(UUID id);

    OrderDetailResponseDTO findHistoricalOrderDetail(UUID id);

    List<OrderDetailResponseDTO> getHistoricalOrderDetails();
}
