package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.ProductMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoryRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDto save(ProductCreate productCreate) {

        Category category = categoryRepository.findByIdOrThrow(productCreate.categoryId());
        Product product = productMapper.toEntity(productCreate);

        category.addProduct(product);
        Product savedProduct = productRepository.save(product);
        categoryRepository.save(category);

        return productMapper.toDto(savedProduct, productCreate.categoryId());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        Long categoryId = productRepository.findCategoryIdByProductId(id).orElse(null);

        return productMapper.toDto(product, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {

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
    public ProductDto update(ProductEdit productEdit, Long id) {

        Product product = productRepository.findByIdOrThrow(id);

        // HU-07 (CA-4): Si el nombre está en blanco, mantenemos el original
        String finalName = (productEdit.name() == null || productEdit.name().trim().isEmpty())
                ? product.getName()
                : productEdit.name();

        // CA-4: En consola, el precio se lee como BigDecimal, pero si el usuario presiona Enter sin tipear nada, 
        // la lectura del BigDecimal suele fallar o mandar null si lo manejamos. Si llega null, conservamos el original.
        BigDecimal finalPrice = productEdit.price() == null ? product.getPrice() : productEdit.price();
        Integer finalStock = productEdit.stock() == null ? product.getStock() : productEdit.stock();

        // HU-07: Comprobar si hay cambios reales antes de persistir
        if (Objects.equals(finalName, product.getName()) &&
            Objects.equals(finalPrice, product.getPrice()) &&
            Objects.equals(finalStock, product.getStock())) {

            throw new IllegalArgumentException("No se detectaron cambios. La operación de modificación fue cancelada.");
        }

        if (productEdit.categoryId() != null) {

            Product finalProduct = product;
            productRepository
                    .findCategoryIdByProductId(id)
                    .flatMap(categoryRepository::findById)
                    .ifPresent(oldCategory -> oldCategory.getProducts().remove(finalProduct));

            Category newCategory = categoryRepository.findByIdOrThrow(productEdit.categoryId());

            newCategory.addProduct(product);
        }

        // Creamos un nuevo ProductEdit con los valores finales procesados
        ProductEdit cleanEdit = new ProductEdit(
                finalName,
                finalPrice,
                productEdit.description() == null || productEdit.description().trim().isEmpty() ? null : productEdit.description(),
                finalStock,
                productEdit.image() == null || productEdit.image().trim().isEmpty() ? null : productEdit.image(),
                productEdit.available(),
                productEdit.categoryId()
        );

        productMapper.updateProductFromEdit(cleanEdit, product);
        product = productRepository.saveAndFlush(product);

        Long categoryId =
                productEdit.categoryId() != null
                        ? productEdit.categoryId()
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
    public List<ProductDto> getProductsByAvailability(Boolean available) {

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
    public List<ProductDto> findProductsByName(String name) {

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
    public List<ProductDto> getLowStockProducts(int limit) {

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
    public ProductDto findHistoricalProduct(Long id) {

        Product deletedProduct = productRepository.findWithDeletedByIdOrThrow(id);
        Long categoryId = productRepository.findCategoryIdByProductId(id).orElse(null);

        return productMapper.toDto(deletedProduct, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getHistoricalProducts() {

        List<Product> allHistory = productRepository.findWithDeletedBy();

        return allHistory.stream().map(product -> productMapper.toDto(product, null)).toList();
    }
}