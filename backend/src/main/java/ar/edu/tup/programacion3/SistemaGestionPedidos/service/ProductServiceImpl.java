package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.product.ProductEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Category;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Product;
import ar.edu.tup.programacion3.SistemaGestionPedidos.mapper.ProductMapper;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoryRepository;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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

        if (productEdit.categoryId() != null) {

            Product finalProduct = product;
            productRepository
                    .findCategoryIdByProductId(id)
                    .flatMap(categoryRepository::findById)
                    .ifPresent(oldCategory -> oldCategory.getProducts().remove(finalProduct));

            Category newCategory = categoryRepository.findByIdOrThrow(productEdit.categoryId());

            newCategory.addProduct(product);
        }

        productMapper.updateProductFromEdit(productEdit, product);
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
}