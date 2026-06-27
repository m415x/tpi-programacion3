package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SGP.model.Order;
import ar.edu.tup.programacion3.SGP.model.Product;
import ar.edu.tup.programacion3.SGP.model.User;
import ar.edu.tup.programacion3.SGP.mapper.OrderMapper;
import ar.edu.tup.programacion3.SGP.repository.OrderRepository;
import ar.edu.tup.programacion3.SGP.repository.ProductRepository;
import ar.edu.tup.programacion3.SGP.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper mapper;

	@Override
    @Transactional
    public OrderResponseDTO save(OrderRequestDTO dto) {

        User user = userRepository.findByIdOrThrow(dto.userId());
        Order order = mapper.toEntity(dto);

		order.setDate(LocalDate.now());
		order.setTotal(BigDecimal.ZERO);
		order.setDeleted(false);

        user.addOrder(order);
        Order savedOrder = orderRepository.saveAndFlush(order);
        userRepository.save(user);

        return unifyUserId(mapper.toDto(savedOrder), user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findById(UUID id) {

        Order order = orderRepository.findByIdOrThrow(id);
        UUID userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(mapper.toDto(order), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {

        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(
                        order -> {
                            UUID userId =
                                    orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

                            return unifyUserId(mapper.toDto(order), userId);
                        })
                .toList();
    }

    @Override
    @Transactional
    public OrderResponseDTO update(OrderRequestDTO dto, UUID id) {

        Order order = orderRepository.findByIdOrThrow(id);

	    UUID oldUserId = orderRepository.findUserIdByOrderId(id).orElse(null);
	    UUID newUserId = dto.userId();

	    if (newUserId != null && !newUserId.equals(oldUserId)) {

		    if (oldUserId != null) {

			    Order finalOrder = order;
			    userRepository.findById(oldUserId)
					    .ifPresent(oldUser -> oldUser.getOrders().remove(finalOrder));
		    }

		    User newUser = userRepository.findByIdOrThrow(newUserId);
		    newUser.addOrder(order);
	    }

        mapper.updateOrderFromEdit(dto, order);
        order = orderRepository.saveAndFlush(order);
	    UUID finalUserId = (newUserId != null) ? newUserId : oldUserId;

	    return unifyUserId(mapper.toDto(order), finalUserId);
    }

	@Override
	@Transactional
	public OrderResponseDTO partialUpdate(OrderRequestDTO dto, UUID id) {

		Order order = orderRepository.findByIdOrThrow(id);

		UUID oldUserId = orderRepository.findUserIdByOrderId(id).orElse(null);
		UUID newUserId = dto.userId();

		if (newUserId != null && !newUserId.equals(oldUserId)) {

			if (oldUserId != null) {

				Order finalOrder = order;
				userRepository.findById(oldUserId)
						.ifPresent(oldUser -> oldUser.getOrders().remove(finalOrder));
			}

			User newUser = userRepository.findByIdOrThrow(newUserId);
			newUser.addOrder(order);
		}

		mapper.updateOrderFromEdit(dto, order);
		order = orderRepository.saveAndFlush(order);
		UUID finalUserId = (newUserId != null) ? newUserId : oldUserId;

		return unifyUserId(mapper.toDto(order), finalUserId);
	}

    @Override
    @Transactional
    public void deleteById(UUID id) {

        Order order = orderRepository.findByIdOrThrow(id);

        order.setDeleted(true);

        if (order.getOrderDetails() != null) {
            order.getOrderDetails().forEach(detail -> detail.setDeleted(true));
        }

        orderRepository.saveAndFlush(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO addProductToOrder(UUID orderId, Integer qty, UUID productId) {

        Order order = orderRepository.findByIdOrThrow(orderId);

        Product product = productRepository.findByIdOrThrow(productId);

        order.addOrderDetail(qty, product);
        order = orderRepository.saveAndFlush(order);

        UUID userId = orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

        return unifyUserId(mapper.toDto(order), userId);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateQtyItem(UUID orderId, UUID productId, Integer newQty) {

        Order order = orderRepository.findByIdOrThrow(orderId);

        Product product = productRepository.findByIdOrThrow(productId);

        order.updateProductQty(product, newQty);
        order = orderRepository.saveAndFlush(order);

        UUID userId = orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

        return unifyUserId(mapper.toDto(order), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getQtyItems(UUID id) {

        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("No se encontró el pedido con id: " + id);
        }

        return orderRepository.getQtyItems(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findHistoricalOrder(UUID id) {

        Order deletedOrder = orderRepository.findDeletedByIdOrThrow(id);
        UUID userId = orderRepository.findUserIdByOrderId(id).orElse(null);

        return unifyUserId(mapper.toDto(deletedOrder), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getHistoricalOrders() {

        List<Order> allHistory = orderRepository.findDeletedAll();

        return allHistory.stream()
                .map(
                        order -> {
                            UUID userId =
                                    orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

                            return unifyUserId(mapper.toDto(order), userId);
                        })
                .toList();
    }

    public OrderResponseDTO unifyUserId(OrderResponseDTO dto, UUID userId) {

        return new OrderResponseDTO(
                dto.id(),
                dto.createdAt(),
                dto.orderStatus(),
                dto.total(),
                dto.paymentMethod(),
                userId,
	            dto.customerPhone(),
			    dto.shippingAddress(),
			    dto.customerNotes(),
                dto.orderDetails());
    }
}
