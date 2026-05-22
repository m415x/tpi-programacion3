package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.OrderDetail;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.OrderDetailMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;

    public OrderDetailServiceImpl(
            OrderDetailRepository orderDetailRepository, OrderDetailMapper orderDetailMapper) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderDetailMapper = orderDetailMapper;
    }

    @Override
    @Transactional
    public OrderDetailDto save(OrderDetailCreate OrderDetailCreate) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para añadir un ítem a un pedido, utilice el servicio de Pedidos.");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDto findById(Long id) {

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
    public List<OrderDetailDto> findAll() {

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
    public OrderDetailDto update(OrderDetailEdit OrderDetailEdit, Long id) {

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
    public OrderDetailDto findHistoricalOrderDetail(Long id) {

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
    public List<OrderDetailDto> getHistoricalOrderDetails() {

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
