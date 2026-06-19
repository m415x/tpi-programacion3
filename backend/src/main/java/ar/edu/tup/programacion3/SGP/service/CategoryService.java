package ar.edu.tup.programacion3.SGP.service;

import ar.edu.tup.programacion3.SGP.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SGP.dto.CategoryRequestDTO;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

    public CategoryResponseDTO save(CategoryRequestDTO dto);

    public CategoryResponseDTO findById(UUID id);

    public List<CategoryResponseDTO> findAll();

    public CategoryResponseDTO update(CategoryRequestDTO dto, UUID id);

	public CategoryResponseDTO partialUpdate(CategoryRequestDTO dto, UUID id);

    public void deleteById(UUID id);

    // Método personalizado
    public List<CategoryResponseDTO> findCategoriesByName(String name);

    public CategoryResponseDTO findHistoricalCategory(UUID id);

    public List<CategoryResponseDTO> getHistoricalCategories();
}
