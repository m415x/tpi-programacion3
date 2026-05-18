package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.pedido.PedidoEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Pedido;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Producto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Usuario;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.PedidoRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductoRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public PedidoDto save(PedidoCreate pedidoCreate) {

        Usuario usuario =
                usuarioRepository
                        .findById(pedidoCreate.idUsuario())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el usuario con id: "
                                                        + pedidoCreate.idUsuario()));

        Pedido pedido = pedidoCreate.toEntity();
        usuario.addPedido(pedido);
        pedido = pedidoRepository.save(pedido);

        return PedidoDto.toDto(pedido, usuario.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDto findById(Long id) {

        Pedido pedido =
                pedidoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró pedido con id: " + id));

        Long idUsuario = pedidoRepository.findUsuarioIdByPedidoId(id).orElse(null);

        return PedidoDto.toDto(pedido, idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDto> findAll() {

        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream()
                .map(
                        pedido -> {
                            Long idUsuario =
                                    pedidoRepository
                                            .findUsuarioIdByPedidoId(pedido.getId())
                                            .orElse(null);

                            return PedidoDto.toDto(pedido, idUsuario);
                        })
                .toList();
    }

    @Override
    @Transactional
    public PedidoDto update(PedidoEdit pedidoEdit, Long idPedido) {

        Pedido pedido =
                pedidoRepository
                        .findById(idPedido)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró pedido con id: " + idPedido));

        pedidoEdit.applyTo(pedido);
        pedido = pedidoRepository.save(pedido);
        Long idUsuario = pedidoRepository.findUsuarioIdByPedidoId(idPedido).orElse(null);

        return PedidoDto.toDto(pedido, idUsuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Pedido pedido =
                pedidoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró pedido con id: " + id));

        pedido.setEliminado(true);

        if (pedido.getDetallePedido() != null) {
            pedido.getDetallePedido().forEach(detalle -> detalle.setEliminado(true));
        }

        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public PedidoDto addProductToOrder(Long idPedido, Integer cantidad, Long idProducto) {

        Pedido pedido =
                pedidoRepository
                        .findById(idPedido)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el pedido ID: " + idPedido));

        Producto producto =
                productoRepository
                        .findById(idProducto)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el producto ID: " + idProducto));

        pedido.addDetallePedido(cantidad, producto);
        pedido = pedidoRepository.save(pedido);
        Long idUsuario = pedidoRepository.findUsuarioIdByPedidoId(pedido.getId()).orElse(null);

        return PedidoDto.toDto(pedido, idUsuario);
    }

    @Override
    @Transactional
    public PedidoDto updateQtyItem(Long idPedido, Long idProducto, Integer nuevaCantidad) {

        Pedido pedido =
                pedidoRepository
                        .findById(idPedido)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el pedido ID: " + idPedido));

        Producto producto =
                productoRepository
                        .findById(idProducto)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró el producto ID: " + idProducto));

        pedido.updateQtyProducto(producto, nuevaCantidad);
        pedido = pedidoRepository.save(pedido);

        Long idUsuario = pedidoRepository.findUsuarioIdByPedidoId(pedido.getId()).orElse(null);

        return PedidoDto.toDto(pedido, idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getQtyItems(Long idPedido) {

        if (!pedidoRepository.existsById(idPedido)) {
            throw new EntityNotFoundException("No se encontró el pedido con id: " + idPedido);
        }

        return pedidoRepository.getQtyItems(idPedido);
    }
}
