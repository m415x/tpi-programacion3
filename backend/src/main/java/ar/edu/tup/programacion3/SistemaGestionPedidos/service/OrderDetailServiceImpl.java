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

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;

	@Override
    @Transactional
    public OrderDetailResponseDTO save(OrderDetailRequestDTO OrderDetailRequestDTO) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para añadir un ítem a un pedido, utilice el servicio de Pedidos.");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO findById(Long id) {

        OrderDetail detail = orderDetailRepository.findByIdOrThrow(id);
        Long orderId = orderDetailRepository.findOrderIdByOrderDetailId(id).orElse(null);
        Long categoryId =
                detail.getProduct() != null
                        ? orderDetailRepository
                                .findCategoryIdByProductId(detail.getProduct().getId())
                                .orElse(null)
                        : null;

        return orderDetailMapper.toDto(detail, orderId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> findAll() {

        List<OrderDetail> details = orderDetailRepository.findAll();

        return details.stream()
                .map(
                        detail -> {
                            Long orderId =
                                    orderDetailRepository
                                            .findOrderIdByOrderDetailId(detail.getId())
                                            .orElse(null);
                            Long categoryId =
                                    detail.getProduct() != null
                                            ? orderDetailRepository
                                                    .findCategoryIdByProductId(
                                                            detail.getProduct().getId())
                                                    .orElse(null)
                                            : null;

                            return orderDetailMapper.toDto(detail, orderId, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional
    public OrderDetailResponseDTO update(OrderDetailRequestDTO orderDetailRequestDTO, Long id) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para modificar un ítem, procese la actualización desde el servicio de Pedidos.");
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        OrderDetail detail = orderDetailRepository.findByIdOrThrow(id);

        detail.setDeleted(true);
        orderDetailRepository.save(detail);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO findHistoricalOrderDetail(Long id) {

        OrderDetail detail = orderDetailRepository.findByIdOrThrow(id);
        Long orderId = orderDetailRepository.findOrderIdByOrderDetailId(id).orElse(null);
        Long categoryId =
                (detail.getProduct() != null)
                        ? orderDetailRepository
                                .findCategoryIdByProductId(detail.getProduct().getId())
                                .orElse(null)
                        : null;

        return orderDetailMapper.toDto(detail, orderId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getHistoricalOrderDetails() {

        List<OrderDetail> allHistory = orderDetailRepository.findWithDeletedBy();

        return allHistory.stream()
                .map(
                        detail -> {
                            Long orderId =
                                    orderDetailRepository
                                            .findOrderIdByOrderDetailId(detail.getId())
                                            .orElse(null);
                            Long categoryId =
                                    (detail.getProduct() != null)
                                            ? orderDetailRepository
                                                    .findCategoryIdByProductId(
                                                            detail.getProduct().getId())
                                                    .orElse(null)
                                            : null;

                            return orderDetailMapper.toDto(detail, orderId, categoryId);
                        })
                .toList();
    }
}
