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
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

	@Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO productRequestDTO) {

        Category category = categoryRepository.findByIdOrThrow(productRequestDTO.categoryId());
        Product product = productMapper.toEntity(productRequestDTO);

        category.addProduct(product);
        Product savedProduct = productRepository.save(product);
        categoryRepository.save(category);

        return productMapper.toDto(savedProduct, productRequestDTO.categoryId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        Long categoryId = productRepository.findCategoryIdByProductId(id).orElse(null);

        return productMapper.toDto(product, categoryId);
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

                            return productMapper.toDto(product, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO update(ProductRequestDTO productRequestDTO, Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        // HU-07 (CA-4): Si el nombre está en blanco, mantenemos el original
        String finalName = (productRequestDTO.name() == null || productRequestDTO.name().trim().isEmpty())
                ? product.getName()
                : productRequestDTO.name();

        // CA-4: En consola, el precio se lee como BigDecimal, pero si el usuario presiona Enter sin tipear nada, 
        // la lectura del BigDecimal suele fallar o mandar null si lo manejamos. Si llega null, conservamos el original.
        BigDecimal finalPrice = productRequestDTO.price() == null ? product.getPrice() : productRequestDTO.price();
        Integer finalStock = productRequestDTO.stock() == null ? product.getStock() : productRequestDTO.stock();

        // HU-07: Comprobar si hay cambios reales antes de persistir
        if (Objects.equals(finalName, product.getName()) &&
            Objects.equals(finalPrice, product.getPrice()) &&
            Objects.equals(finalStock, product.getStock())) {

            throw new IllegalArgumentException("No se detectaron cambios. La operación de modificación fue cancelada.");
        }

        if (productRequestDTO.categoryId() != null) {

            Product finalProduct = product;
            productRepository
                    .findCategoryIdByProductId(id)
                    .flatMap(categoryRepository::findById)
                    .ifPresent(oldCategory -> oldCategory.getProducts().remove(finalProduct));

            Category newCategory = categoryRepository.findByIdOrThrow(productRequestDTO.categoryId());

            newCategory.addProduct(product);
        }

        // Creamos un nuevo ProductEdit con los valores finales procesados
        ProductRequestDTO cleanEdit = new ProductRequestDTO(
                finalName,
                finalPrice,
                productRequestDTO.description() == null || productRequestDTO.description().trim().isEmpty() ? null : productRequestDTO.description(),
                finalStock,
                productRequestDTO.image() == null || productRequestDTO.image().trim().isEmpty() ? null : productRequestDTO.image(),
                productRequestDTO.available(),
                productRequestDTO.categoryId()
        );

        productMapper.updateProductFromEdit(cleanEdit, product);
        product = productRepository.saveAndFlush(product);

        Long categoryId =
                productRequestDTO.categoryId() != null
                        ? productRequestDTO.categoryId()
                        : productRepository.findCategoryIdByProductId(product.getId()).orElse(null);

        return productMapper.toDto(product, categoryId);
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

                            return productMapper.toDto(product, categoryId);
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

                            return productMapper.toDto(product, categoryId);
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

                            return productMapper.toDto(product, categoryId);
                        })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findHistoricalProduct(Long id) {

        Product deletedProduct = productRepository.findWithDeletedByIdOrThrow(id);
        Long categoryId = productRepository.findCategoryIdByProductId(id).orElse(null);

        return productMapper.toDto(deletedProduct, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getHistoricalProducts() {

        List<Product> allHistory = productRepository.findWithDeletedBy();

        return allHistory.stream().map(product -> productMapper.toDto(product, null)).toList();
    }
}