package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.CategoryMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoryRepository;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

	@Override
    @Transactional
    public CategoryResponseDTO save(CategoryRequestDTO categoryRequestDTO) {

        Category category = categoryMapper.toEntity(categoryRequestDTO);
        category = categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {

        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CategoryResponseDTO update(CategoryRequestDTO categoryRequestDTO, Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);

        // Determinar los valores finales, manteniendo los originales si la entrada está en blanco.
        String finalName = (categoryRequestDTO.name() == null || categoryRequestDTO.name().trim().isEmpty())
                ? category.getName()
                : categoryRequestDTO.name();
        String finalDescription = (categoryRequestDTO.description() == null || categoryRequestDTO.description().trim().isEmpty())
                ? category.getDescription()
                : categoryRequestDTO.description();

        // Comprobar si hay cambios reales antes de realizar la escritura en la base de datos.
        if (Objects.equals(finalName, category.getName()) &&
            Objects.equals(finalDescription, category.getDescription())) {

            throw new IllegalArgumentException("No se detectaron cambios. La operación de modificación fue cancelada.");
        }

        // Aplicar los cambios y guardar
        category.setName(finalName);
        category.setDescription(finalDescription);
        category = categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);

        category.setDeleted(true);

        if (category.getProducts() != null) {
            category.getProducts()
                    .forEach(
                            product -> {
                                product.setDeleted(true);
                            });
        }

        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findCategoriesByName(String name) {

        return categoryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO findHistoricalCategory(Long id) {

        Category deteledCategory = categoryRepository.findWithDeletedByIdOrThrow(id);

        return categoryMapper.toDto(deteledCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getHistoricalCategories() {

        List<Category> allHistory = categoryRepository.findWithDeletedBy();

        return allHistory.stream().map(categoryMapper::toDto).toList();
    }
}