package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Order;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.User;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.OrderMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.OrderRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

	@Override
    @Transactional
    public OrderResponseDTO save(OrderRequestDTO orderRequestDTO) {

        User user = userRepository.findByIdOrThrow(orderRequestDTO.userId());
        Order order = orderMapper.toEntity(orderRequestDTO);

        user.addOrder(order);
        Order savedOrder = orderRepository.saveAndFlush(order);
        userRepository.save(user);

        return unifyUserId(orderMapper.toDto(savedOrder), user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id) {

        Order order = orderRepository.findByIdOrThrow(id);
        Long userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(orderMapper.toDto(order), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {

        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(
                        order -> {
                            Long userId =
                                    orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

                            return unifyUserId(orderMapper.toDto(order), userId);
                        })
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDTO update(OrderRequestDTO orderRequestDTO, Long id) {

        Order order = orderRepository.findByIdOrThrow(id);

        orderMapper.updateOrderFromEdit(orderRequestDTO, order);
        order = orderRepository.saveAndFlush(order);

        Long userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(orderMapper.toDto(order), userId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Order order = orderRepository.findByIdOrThrow(id);

        order.setDeleted(true);

        if (order.getOrderDetails() != null) {
            order.getOrderDetails().forEach(detail -> detail.setDeleted(true));
        }

        orderRepository.saveAndFlush(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO addProductToOrder(Long orderId, Integer qty, Long productId) {

        Order order = orderRepository.findByIdOrThrow(orderId);

        Product product = productRepository.findByIdOrThrow(productId);

        order.addOrderDetail(qty, product);
        order = orderRepository.saveAndFlush(order);

        Long userId = orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

        return unifyUserId(orderMapper.toDto(order), userId);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateQtyItem(Long orderId, Long productId, Integer newQty) {

        Order order = orderRepository.findByIdOrThrow(orderId);

        Product product = productRepository.findByIdOrThrow(productId);

        order.updateProductQty(product, newQty);
        order = orderRepository.saveAndFlush(order);

        Long userId = orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

        return unifyUserId(orderMapper.toDto(order), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getQtyItems(Long id) {

        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el pedido con id: " + id);
        }

        return orderRepository.getQtyItems(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findHistoricalOrder(Long id) {

        Order deletedOrder = orderRepository.findWithDeletedByIdOrThrow(id);
        Long userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(orderMapper.toDto(deletedOrder), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getHistoricalOrders() {

        List<Order> allHistory = orderRepository.findWithDeletedBy();

        return allHistory.stream()
                .map(
                        order -> {
                            Long userId =
                                    orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

                            return unifyUserId(orderMapper.toDto(order), userId);
                        })
                .toList();
    }

    public OrderResponseDTO unifyUserId(OrderResponseDTO dto, Long userId) {

        return new OrderResponseDTO(
                dto.id(),
                dto.date(),
                dto.orderStatus(),
                dto.total(),
                dto.paymentMethod(),
                userId,
                dto.orderDetails());
    }
}
