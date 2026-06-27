package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SGP.model.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    public OrderResponseDTO save(OrderRequestDTO dto);

    public OrderResponseDTO findById(UUID id);

    public List<OrderResponseDTO> findAll();

    public OrderResponseDTO update(OrderRequestDTO dto, UUID id);

	public OrderResponseDTO partialUpdate(OrderRequestDTO dto, UUID id);

	public void deleteById(UUID id);

    // Método personalizado
    public OrderResponseDTO addProductToOrder(UUID orderId, Integer qty, UUID productId);

    public OrderResponseDTO updateQtyItem(UUID orderId, UUID productId, Integer newQty);

    public UUID getQtyItems(UUID orderId);

	OrderResponseDTO updateStatus(UUID id, OrderStatus newStatus);

    OrderResponseDTO findHistoricalOrder(UUID id);

    List<OrderResponseDTO> getHistoricalOrders();

    // Método helper para reconstruir el Record de salida con su userId correspondiente
    public OrderResponseDTO unifyUserId(OrderResponseDTO dto, UUID userId);
}
