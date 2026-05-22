package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductEdit;
import java.util.List;

public interface ProductService {

    public ProductDto save(ProductCreate productCreate);

    public ProductDto findById(Long id);

    public List<ProductDto> findAll();

    public ProductDto update(ProductEdit productEdit, Long id);

    public void deleteById(Long id);

    // Métodos personalizados
    public List<ProductDto> getProductsByAvailability(Boolean available);

    public List<ProductDto> findProductsByName(String name);

    public List<ProductDto> getLowStockProducts(int limit);

	public ProductDto findHistoricalProduct(Long id);

	public List<ProductDto> getHistoricalProducts();
}
