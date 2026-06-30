package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.OrderRequestDTO;
import ar.edu.tup.programacion3.SGP.dto.OrderResponseDTO;
import ar.edu.tup.programacion3.SGP.model.Order;
import ar.edu.tup.programacion3.SGP.model.Product;
import ar.edu.tup.programacion3.SGP.model.User;
import ar.edu.tup.programacion3.SGP.mapper.OrderMapper;
import ar.edu.tup.programacion3.SGP.model.enums.OrderStatus;
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

		if (dto.orderDetail() == null || dto.orderDetail().isEmpty()) {
			throw new IllegalArgumentException("Se requiere al menos un detalle de pedido.");
		}

        User user = userRepository.findByIdOrThrow(dto.userId());
        Order order = mapper.toEntity(dto);

		order.setDate(LocalDate.now());
		order.setOrderStatus(dto.orderStatus());
		order.setTotal(BigDecimal.ZERO);
		order.setPaymentMethod(dto.paymentMethod());
		order.setDeleted(false);

		for (OrderRequestDTO.ItemInnerRequestDTO itemDto : dto.orderDetail()) {
			if (itemDto.quantity() == null || itemDto.quantity() < 1) {
				throw new IllegalArgumentException("La quantity de cada detalle debe ser mayor o igual a 1.");
			}

			Product product = productRepository.findByIdOrThrow(itemDto.productId());

			if (product.getAvailable() == null || !product.getAvailable()) {
				throw new IllegalStateException("El producto '" + product.getName() + "' no está disponible para la venta.");
			}

			if (product.getStock() < itemDto.quantity()) {
				throw new IllegalStateException("Stock insuficiente para '" + product.getName() +
						"'. Disponible: " + product.getStock() + ", Solicitado: " + itemDto.quantity());
			}

			product.setStock(product.getStock() - itemDto.quantity());
			productRepository.save(product);

			order.addOrderDetail(itemDto.quantity(), product);
		}

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
		UUID userId = orderRepository.findUserIdByOrderId(id).orElse(null);

		if (dto.orderStatus() != null) {
			order.setOrderStatus(dto.orderStatus()); // Escenario 1 y 3
		}
		if (dto.paymentMethod() != null) {
			order.setPaymentMethod(dto.paymentMethod()); // Escenario 2
		}
		order = orderRepository.saveAndFlush(order);

		return unifyUserId(mapper.toDto(order), userId);
	}

	@Override
	@Transactional
	public OrderResponseDTO partialUpdate(OrderRequestDTO dto, UUID id) {

		return this.update(dto, id);
	}

    @Override
    @Transactional
    public void deleteById(UUID id) {

        Order order = orderRepository.findByIdOrThrow(id);
        order.setDeleted(true);

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
	@Transactional
	public OrderResponseDTO updateStatus(UUID id, OrderStatus newStatus) {

		Order order = orderRepository.findByIdOrThrow(id);
		order.setOrderStatus(newStatus);

		Order updatedOrder = orderRepository.saveAndFlush(order);
		UUID userId = orderRepository.findUserIdByOrderId(id).orElse(null);

		return unifyUserId(mapper.toDto(updatedOrder), userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponseDTO> findByUserId(UUID userId) {

		userRepository.findByIdOrThrow(userId);

		return orderRepository.findAll().stream()
				.filter(order -> {
					UUID actualOwnerId = orderRepository.findUserIdByOrderId(order.getId()).orElse(null);

					return userId.equals(actualOwnerId);
				})
				.map(order -> unifyUserId(mapper.toDto(order), userId))
				.toList();
	}

	@Override
	@Transactional
	public OrderResponseDTO cancelOrder(UUID id) {

		Order order = orderRepository.findByIdOrThrow(id);

		if (!OrderStatus.PENDING.equals(order.getOrderStatus())) {
			throw new IllegalArgumentException(
					"No se puede cancelar el pedido porque ya se encuentra en estado: " + order.getOrderStatus()
			);
		}

		if (order.getOrderDetails() != null) {
			order.getOrderDetails().forEach(detail -> {
				Product product = detail.getProduct();
				if (product != null) {

					int restoredStock = product.getStock() + detail.getQuantity();
					product.setStock(restoredStock);

					if (restoredStock > 0) {
						product.setAvailable(true);
					}
					productRepository.save(product);
				}
			});
		}

		order.setOrderStatus(OrderStatus.CANCELLED);
		order = orderRepository.saveAndFlush(order);
		UUID userId = orderRepository.findUserIdByOrderId(id).orElse(null);

		return unifyUserId(mapper.toDto(order), userId);
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
