package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.detallePedido.DetallePedidoEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.DetallePedido;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.DetallePedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    @Transactional
    public DetallePedidoDto save(DetallePedidoCreate DetallePedidoCreate) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para añadir un ítem a un pedido, utilice el servicio de Pedidos.");
    }

    @Override
    @Transactional(readOnly = true)
    public DetallePedidoDto findById(Long id) {

        DetallePedido detalle =
                detallePedidoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el detalle de pedido con id: "
                                                        + id));

        Long idPedido = detallePedidoRepository.findPedidoIdByDetalleId(id).orElse(null);

        Long idCategoria =
                detalle.getProducto() != null
                        ? detallePedidoRepository
                                .findCategoriaIdByProductoId(detalle.getProducto().getId())
                                .orElse(null)
                        : null;

        return DetallePedidoDto.toDto(detalle, idPedido, idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoDto> findAll() {

        List<DetallePedido> detalles = detallePedidoRepository.findAll();

        return detalles.stream()
                .map(
                        detalle -> {
                            Long idPedido =
                                    detallePedidoRepository
                                            .findPedidoIdByDetalleId(detalle.getId())
                                            .orElse(null);
                            Long idCategoria =
                                    detalle.getProducto() != null
                                            ? detallePedidoRepository
                                                    .findCategoriaIdByProductoId(
                                                            detalle.getProducto().getId())
                                                    .orElse(null)
                                            : null;

                            return DetallePedidoDto.toDto(detalle, idPedido, idCategoria);
                        })
                .toList();
    }

    @Override
    @Transactional
    public DetallePedidoDto update(DetallePedidoEdit DetallePedidoEdit, Long id) {

        throw new UnsupportedOperationException(
                "Operación no permitida: Para modificar un ítem, procese la actualización desde el servicio de Pedidos.");
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        DetallePedido detalle =
                detallePedidoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el detalle de pedido con id: "
                                                        + id));

        detalle.setEliminado(true);
        detallePedidoRepository.save(detalle);
    }
}
