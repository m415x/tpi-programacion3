package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SGP.dto.ProductRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    public ProductResponseDTO save(ProductRequestDTO dto);

    public ProductResponseDTO findById(UUID id);

    public List<ProductResponseDTO> findAll();

    public ProductResponseDTO update(ProductRequestDTO dto, UUID id);

	public ProductResponseDTO partialUpdate(ProductRequestDTO dto, UUID id);

	public void deleteById(UUID id);

    // Métodos personalizados
    public List<ProductResponseDTO> findByCategoryId(UUID categoryId);

    public List<ProductResponseDTO> getProductsByAvailability(Boolean available);

	public List<ProductResponseDTO> findProductsAdvanced(String name, UUID categoryId, Boolean available, String sort);

    public List<ProductResponseDTO> getLowStockProducts(int limit);

	public ProductResponseDTO findHistoricalProduct(UUID id);

	public List<ProductResponseDTO> getHistoricalProducts();
}
