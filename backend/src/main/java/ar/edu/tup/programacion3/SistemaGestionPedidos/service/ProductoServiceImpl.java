package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Categoria;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Producto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoriaRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public ProductoDto save(ProductoCreate productoCreate) {

        Categoria categoria =
                categoriaRepository
                        .findById(productoCreate.idCategoria())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró categoria con id: "
                                                        + productoCreate.idCategoria()));

        Producto producto = productoCreate.toEntity();
        categoria.addProducto(producto);
        producto = productoRepository.save(producto);

        return ProductoDto.toDto(producto, productoCreate.idCategoria());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto findById(Long id) {

        Producto producto =
                productoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró producto con id: " + id));

        Long idCategoria = productoRepository.findCategoryIdByProductId(id).orElse(null);

        return ProductoDto.toDto(producto, idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> findAll() {

        return productoRepository.findAll().stream()
                .map(
                        producto -> {
                            Long idCategoria =
                                    productoRepository
                                            .findCategoryIdByProductId(producto.getId())
                                            .orElse(null);
                            return ProductoDto.toDto(producto, idCategoria);
                        })
                .toList();
    }

    @Override
    @Transactional
    public ProductoDto update(ProductoEdit productoEdit, Long idProducto) {

        Producto producto =
                productoRepository
                        .findById(idProducto)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró producto con id: " + idProducto));

        if (productoEdit.idCategoria() != null) {

            Producto finalProducto = producto;
            productoRepository
                    .findCategoryIdByProductId(idProducto)
                    .flatMap(categoriaRepository::findById)
                    .ifPresent(oldCategory -> oldCategory.getProductos().remove(finalProducto));

            Categoria newCategory =
                    categoriaRepository
                            .findById(productoEdit.idCategoria())
                            .orElseThrow(
                                    () ->
                                            new EntityNotFoundException(
                                                    "No se encontró categoría con id: "
                                                            + productoEdit.idCategoria()));

            newCategory.addProducto(producto);
        }

        productoEdit.applyTo(producto);
        producto = productoRepository.save(producto);

        Long idCategoria =
                productoEdit.idCategoria() != null
                        ? productoEdit.idCategoria()
                        : productoRepository
                                .findCategoryIdByProductId(producto.getId())
                                .orElse(null);

        return ProductoDto.toDto(producto, idCategoria);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Producto producto =
                productoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró producto con id: " + id));

        producto.setEliminado(true);
        productoRepository
                .findCategoryIdByProductId(id)
                .flatMap(categoriaRepository::findById)
                .ifPresent(categoria -> categoria.getProductos().remove(producto));
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> getProductsByAvailability(Boolean available) {

        List<Producto> productos =
                (available == null)
                        ? productoRepository.findAll()
                        : productoRepository.findByDisponible(available);

        return productos.stream()
                .map(
                        p ->
                                ProductoDto.toDto(
                                        p,
                                        productoRepository
                                                .findCategoryIdByProductId(p.getId())
                                                .orElse(null)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> findProductsByName(String name) {

        return productoRepository.findByNombreContainingIgnoreCase(name).stream()
                .map(
                        producto -> {
                            Long idCat =
                                    productoRepository
                                            .findCategoryIdByProductId(producto.getId())
                                            .orElse(null);
                            return ProductoDto.toDto(producto, idCat);
                        })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> getLowStockProducts(int limit) {

        return productoRepository.findByStockLessThan(limit).stream()
                .map(
                        p ->
                                ProductoDto.toDto(
                                        p,
                                        productoRepository
                                                .findCategoryIdByProductId(p.getId())
                                                .orElse(null)))
                .toList();
    }
}
