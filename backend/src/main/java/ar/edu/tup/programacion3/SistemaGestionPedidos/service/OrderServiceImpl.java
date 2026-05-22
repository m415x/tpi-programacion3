package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.order.OrderEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Order;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.User;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.OrderMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.OrderRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderDto save(OrderCreate orderCreate) {

        User user = userRepository.findByIdOrThrow(orderCreate.userId());
        Order order = orderMapper.toEntity(orderCreate);

        user.addOrder(order);
        Order savedOrder = orderRepository.saveAndFlush(order);
        userRepository.save(user);

        return unifyUserId(orderMapper.toDto(savedOrder), user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {

        Order order = orderRepository.findByIdOrThrow(id);
        Long userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(orderMapper.toDto(order), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {

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
    public OrderDto update(OrderEdit orderEdit, Long id) {

        Order order = orderRepository.findByIdOrThrow(id);

        orderMapper.updateOrderFromEdit(orderEdit, order);
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
    public OrderDto addProductToOrder(Long orderId, Integer qty, Long productId) {

        Order order = orderRepository.findByIdOrThrow(orderId);

        Product product = productRepository.findByIdOrThrow(productId);

        order.addOrderDetail(qty, product);
        order = orderRepository.saveAndFlush(order);

        Long userId = orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

        return unifyUserId(orderMapper.toDto(order), userId);
    }

    @Override
    @Transactional
    public OrderDto updateQtyItem(Long orderId, Long productId, Integer newQty) {

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
    public OrderDto findHistoricalOrder(Long id) {

        Order deletedOrder = orderRepository.findWithDeletedByIdOrThrow(id);
        Long userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(orderMapper.toDto(deletedOrder), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getHistoricalOrders() {

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

    public OrderDto unifyUserId(OrderDto dto, Long userId) {

        return new OrderDto(
                dto.id(),
                dto.date(),
                dto.orderStatus(),
                dto.total(),
                dto.paymentMethod(),
                userId,
                dto.orderDetails());
    }
}
