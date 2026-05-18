package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoEdit;

import java.util.List;

public interface PedidoService {
    public PedidoDto save(PedidoCreate pedidoCreate);

    public PedidoDto findById(Long id);

    public List<PedidoDto> findAll();

    public PedidoDto update(PedidoEdit pedidoEdit, Long idPedido);

    public void deleteById(Long id);

    // Método personalizado
    public PedidoDto addProductToOrder(Long idPedido, Integer cantidad, Long idProducto);

    public PedidoDto updateQtyItem(Long idPedido, Long idProducto, Integer nuevaCantidad);

    public Long getQtyItems(Long idPedido);
}
