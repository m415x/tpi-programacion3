package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import java.util.List;

public interface CategoryService {

    public CategoryResponseDTO save(CategoryRequestDTO dto);

    public CategoryResponseDTO findById(Long id);

    public List<CategoryResponseDTO> findAll();

    public CategoryResponseDTO update(CategoryRequestDTO dto, Long id);

	public CategoryResponseDTO partialUpdate(CategoryRequestDTO dto, Long id);

    public void deleteById(Long id);

    // Método personalizado
    public List<CategoryResponseDTO> findCategoriesByName(String name);

    public CategoryResponseDTO findHistoricalCategory(Long id);

    public List<CategoryResponseDTO> getHistoricalCategories();
}
