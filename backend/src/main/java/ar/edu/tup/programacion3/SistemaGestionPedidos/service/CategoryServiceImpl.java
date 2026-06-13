package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.CategoryRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.CategoryMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoryRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

	@Override
    @Transactional
    public CategoryResponseDTO save(CategoryRequestDTO dto) {

        Category category = mapper.toEntity(dto);
        category = repository.save(category);

        return mapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(Long id) {

        Category category = repository.findByIdOrThrow(id);

        return mapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {

        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public CategoryResponseDTO update(CategoryRequestDTO dto, Long id) {

        Category category = repository.findByIdOrThrow(id);

        mapper.updateCategoryFromEdit(dto, category);
        category = repository.save(category);

        return mapper.toDto(category);
    }

	@Override
	@Transactional
	public CategoryResponseDTO partialUpdate(CategoryRequestDTO dto, Long id) {

		Category category = repository.findByIdOrThrow(id);

		mapper.updateCategoryFromEdit(dto, category);
		category = repository.save(category);

		return mapper.toDto(category);
	}

    @Override
    @Transactional
    public void deleteById(Long id) {

        Category category = repository.findByIdOrThrow(id);

        category.setDeleted(true);

        if (category.getProducts() != null) {
            category.getProducts()
                    .forEach(
                            product -> {
                                product.setDeleted(true);
                            });
        }

        repository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findCategoriesByName(String name) {

        return repository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO findHistoricalCategory(Long id) {

        Category deteledCategory = repository.findDeletedByIdOrThrow(id);

        return mapper.toDto(deteledCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getHistoricalCategories() {

        List<Category> allHistory = repository.findDeletedAll();

        return allHistory.stream().map(mapper::toDto).toList();
    }
}