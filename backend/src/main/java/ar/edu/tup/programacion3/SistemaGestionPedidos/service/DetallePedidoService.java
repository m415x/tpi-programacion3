package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoEdit;

import java.util.List;

public interface DetallePedidoService {

	public DetallePedidoDto save(DetallePedidoCreate DetallePedidoCreate);

	public DetallePedidoDto findById(Long id);

	public List<DetallePedidoDto> findAll();

	public DetallePedidoDto update(DetallePedidoEdit DetallePedidoEdit, Long id);

	public void deleteById(Long id);
}
