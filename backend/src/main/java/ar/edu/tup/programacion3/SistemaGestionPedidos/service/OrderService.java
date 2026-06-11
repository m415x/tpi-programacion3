package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    public OrderResponseDTO save(OrderRequestDTO orderRequestDTO);

    public OrderResponseDTO findById(Long id);

    public List<OrderResponseDTO> findAll();

    public OrderResponseDTO update(OrderRequestDTO orderRequestDTO, Long id);

    public void deleteById(Long id);

    // Método personalizado
    public OrderResponseDTO addProductToOrder(Long orderId, Integer qty, Long productId);

    public OrderResponseDTO updateQtyItem(Long orderId, Long productId, Integer newQty);

    public Long getQtyItems(Long orderId);

    OrderResponseDTO findHistoricalOrder(Long id);

    List<OrderResponseDTO> getHistoricalOrders();

    // Método helper para reconstruir el Record de salida con su userId correspondiente
    public OrderResponseDTO unifyUserId(OrderResponseDTO dto, Long userId);
}
