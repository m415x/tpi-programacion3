package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.CategoryMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoryRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryCreate categoryCreate) {

        Category category = categoryMapper.toEntity(categoryCreate);
        category = categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {

        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryEdit categoryEdit, Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);

        // Determinar los valores finales, manteniendo los originales si la entrada está en blanco.
        String finalName = (categoryEdit.name() == null || categoryEdit.name().trim().isEmpty())
                ? category.getName()
                : categoryEdit.name();
        String finalDescription = (categoryEdit.description() == null || categoryEdit.description().trim().isEmpty())
                ? category.getDescription()
                : categoryEdit.description();

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
    public List<CategoryDto> findCategoriesByName(String name) {

        return categoryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findHistoricalCategory(Long id) {

        Category deteledCategory = categoryRepository.findWithDeletedByIdOrThrow(id);

        return categoryMapper.toDto(deteledCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getHistoricalCategories() {

        List<Category> allHistory = categoryRepository.findWithDeletedBy();

        return allHistory.stream().map(categoryMapper::toDto).toList();
    }
}