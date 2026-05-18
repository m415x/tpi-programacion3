package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoEdit;

import java.util.List;

public interface ProductoService {

    public ProductoDto save(ProductoCreate productoCreate);

    public ProductoDto findById(Long id);

    public List<ProductoDto> findAll();

    public ProductoDto update(ProductoEdit productoEdit, Long idProducto);

    public void deleteById(Long id);

    // Métodos personalizados
    public List<ProductoDto> getProductsByAvailability(Boolean available);

    public List<ProductoDto> findProductsByName(String name);

    public List<ProductoDto> getLowStockProducts(int limit);
}
