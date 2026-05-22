package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.orderDetail.OrderDetailEdit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderDetailService {

    public OrderDetailDto save(OrderDetailCreate OrderDetailCreate);

    public OrderDetailDto findById(Long id);

    public List<OrderDetailDto> findAll();

    public OrderDetailDto update(OrderDetailEdit OrderDetailEdit, Long id);

    public void deleteById(Long id);

    OrderDetailDto findHistoricalOrderDetail(Long id);

    List<OrderDetailDto> getHistoricalOrderDetails();
}
