package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductResponseDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.ProductRequestDTO;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.ProductMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoryRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO dto) {

        Category category = categoryRepository.findByIdOrThrow(dto.categoryId());
        Product product = mapper.toEntity(dto);

        category.addProduct(product);
        Product savedProduct = productRepository.save(product);
        categoryRepository.save(category);

        return mapper.toDto(savedProduct, dto.categoryId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        Long categoryId = productRepository.findCategoryIdByProductId(id).orElse(null);

        return mapper.toDto(product, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {

        return productRepository.findAll().stream()
                .map(
                        product -> {
                            Long categoryId =
                                    productRepository
                                            .findCategoryIdByProductId(product.getId())
                                            .orElse(null);

                            return mapper.toDto(product, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO update(ProductRequestDTO dto, Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        Long oldCategoryId = productRepository.findCategoryIdByProductId(id).orElse(null);
        Long newCategoryId = dto.categoryId();

        if (newCategoryId != null && !newCategoryId.equals(oldCategoryId)) {

            if (oldCategoryId != null) {

	            Product finalProduct = product;
	            categoryRepository
                        .findById(oldCategoryId)
                        .ifPresent(oldCategory -> oldCategory.getProducts().remove(finalProduct));
            }

            Category newCategory = categoryRepository.findByIdOrThrow(newCategoryId);
            newCategory.addProduct(product);
        }

        mapper.updateProductFromEdit(dto, product);
        product = productRepository.saveAndFlush(product);

        return mapper.toDto(product, newCategoryId);
    }

	@Override
	@Transactional
	public ProductResponseDTO partialUpdate(ProductRequestDTO dto, Long id) {

		Product product = productRepository.findByIdOrThrow(id);

		Long oldCategoryId = productRepository.findCategoryIdByProductId(id).orElse(null);
		Long newCategoryId = dto.categoryId();

		if (newCategoryId != null && !newCategoryId.equals(oldCategoryId)) {

			if (oldCategoryId != null) {

				Product finalProduct = product;
				categoryRepository
						.findById(oldCategoryId)
						.ifPresent(oldCategory -> oldCategory.getProducts().remove(finalProduct));
			}

			Category newCategory = categoryRepository.findByIdOrThrow(newCategoryId);
			newCategory.addProduct(product);
		}

		mapper.updateProductFromEdit(dto, product);
		product = productRepository.saveAndFlush(product);
		Long finalCategoryId = (newCategoryId != null) ? newCategoryId : oldCategoryId;

		return mapper.toDto(product, finalCategoryId);
	}

    @Override
    @Transactional
    public void deleteById(Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        product.setDeleted(true);
        productRepository
                .findCategoryIdByProductId(id)
                .flatMap(categoryRepository::findById)
                .ifPresent(category -> category.getProducts().remove(product));
        productRepository.saveAndFlush(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByAvailability(Boolean available) {

        List<Product> products =
                (available == null)
                        ? productRepository.findAll()
                        : productRepository.findByAvailable(available);

        return products.stream()
                .map(
                        product -> {
                            Long categoryId =
                                    productRepository
                                            .findCategoryIdByProductId(product.getId())
                                            .orElse(null);

                            return mapper.toDto(product, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findProductsByName(String name) {

        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(
                        product -> {
                            Long categoryId =
                                    productRepository
                                            .findCategoryIdByProductId(product.getId())
                                            .orElse(null);

                            return mapper.toDto(product, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStockProducts(int limit) {

        return productRepository.findByStockLessThan(limit).stream()
                .map(
                        product -> {
                            Long categoryId =
                                    productRepository
                                            .findCategoryIdByProductId(product.getId())
                                            .orElse(null);

                            return mapper.toDto(product, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findHistoricalProduct(Long id) {

        Product deletedProduct = productRepository.findDeletedByIdOrThrow(id);
        Long categoryId = productRepository.findCategoryIdByProductId(id).orElse(null);

        return mapper.toDto(deletedProduct, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getHistoricalProducts() {

        List<Product> allHistory = productRepository.findDeletedAll();

        return allHistory.stream().map(product -> mapper.toDto(product, null)).toList();
    }
}
