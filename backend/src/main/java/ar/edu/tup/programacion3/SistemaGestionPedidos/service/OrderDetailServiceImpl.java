package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.OrderDetailResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.OrderDetail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.OrderDetailMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository repository;
    private final OrderDetailMapper mapper;

	@Override
    @Transactional
    public OrderDetailResponseDTO save(OrderDetailRequestDTO dto) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para añadir un ítem a un pedido, utilice el servicio de Pedidos.");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO findById(Long id) {

        OrderDetail detail = repository.findByIdOrThrow(id);
        Long orderId = repository.findOrderIdByOrderDetailId(id).orElse(null);
        Long categoryId =
                detail.getProduct() != null
                        ? repository
                                .findCategoryIdByProductId(detail.getProduct().getId())
                                .orElse(null)
                        : null;

        return mapper.toDto(detail, orderId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> findAll() {

        List<OrderDetail> details = repository.findAll();

        return details.stream()
                .map(
                        detail -> {
                            Long orderId =
                                    repository
                                            .findOrderIdByOrderDetailId(detail.getId())
                                            .orElse(null);
                            Long categoryId =
                                    detail.getProduct() != null
                                            ? repository
                                                    .findCategoryIdByProductId(
                                                            detail.getProduct().getId())
                                                    .orElse(null)
                                            : null;

                            return mapper.toDto(detail, orderId, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional
    public OrderDetailResponseDTO update(OrderDetailRequestDTO dto, Long id) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para modificar un ítem, procese la actualización desde el servicio de Pedidos.");
    }

	@Override
	@Transactional
	public OrderDetailResponseDTO partialUpdate(OrderDetailRequestDTO dto, Long id) {

		throw new UnsupportedOperationException(
				"Operación no permitida: Para modificar un ítem, procese la actualización desde el servicio de Pedidos.");
	}

    @Override
    @Transactional
    public void deleteById(Long id) {

	    throw new UnsupportedOperationException(
			    "Operación no permitida: Para eliminar un ítem, hágalo a través del servicio de Pedidos para recalcular los totales.");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO findHistoricalOrderDetail(Long id) {

        OrderDetail detail = repository.findDeletedByIdOrThrow(id);
        Long orderId = repository.findOrderIdByOrderDetailId(id).orElse(null);
        Long categoryId =
                (detail.getProduct() != null)
                        ? repository
                                .findCategoryIdByProductId(detail.getProduct().getId())
                                .orElse(null)
                        : null;

        return mapper.toDto(detail, orderId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getHistoricalOrderDetails() {

        List<OrderDetail> allHistory = repository.findDeletedAll();

        return allHistory.stream()
                .map(
                        detail -> {
                            Long orderId =
                                    repository
                                            .findOrderIdByOrderDetailId(detail.getId())
                                            .orElse(null);
                            Long categoryId =
                                    (detail.getProduct() != null)
                                            ? repository
                                                    .findCategoryIdByProductId(
                                                            detail.getProduct().getId())
                                                    .orElse(null)
                                            : null;

                            return mapper.toDto(detail, orderId, categoryId);
                        })
                .toList();
    }
}
