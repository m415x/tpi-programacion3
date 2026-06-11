package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import java.util.List;

public interface ProductService {

    public ProductResponseDTO save(ProductRequestDTO productRequestDTO);

    public ProductResponseDTO findById(Long id);

    public List<ProductResponseDTO> findAll();

    public ProductResponseDTO update(ProductRequestDTO productRequestDTO, Long id);

    public void deleteById(Long id);

    // Métodos personalizados
    public List<ProductResponseDTO> getProductsByAvailability(Boolean available);

    public List<ProductResponseDTO> findProductsByName(String name);

    public List<ProductResponseDTO> getLowStockProducts(int limit);

	public ProductResponseDTO findHistoricalProduct(Long id);

	public List<ProductResponseDTO> getHistoricalProducts();
}
