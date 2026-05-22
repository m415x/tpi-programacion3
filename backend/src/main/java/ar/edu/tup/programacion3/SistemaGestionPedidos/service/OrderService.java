package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderEdit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    public OrderDto save(OrderCreate orderCreate);

    public OrderDto findById(Long id);

    public List<OrderDto> findAll();

    public OrderDto update(OrderEdit orderEdit, Long id);

    public void deleteById(Long id);

    // Método personalizado
    public OrderDto addProductToOrder(Long orderId, Integer qty, Long productId);

    public OrderDto updateQtyItem(Long orderId, Long productId, Integer newQty);

    public Long getQtyItems(Long orderId);

    OrderDto findHistoricalOrder(Long id);

    List<OrderDto> getHistoricalOrders();

    // Método helper para reconstruir el Record de salida con su userId correspondiente
    public OrderDto unifyUserId(OrderDto dto, Long userId);
}
