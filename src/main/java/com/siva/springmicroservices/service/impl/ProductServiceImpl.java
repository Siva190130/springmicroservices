package com.siva.springmicroservices.service.impl;

import com.siva.springmicroservices.dto.PagedResponse;
import com.siva.springmicroservices.dto.ProductRequest;
import com.siva.springmicroservices.dto.ProductResponse;
import com.siva.springmicroservices.dto.ProductUpdateRequest;
import com.siva.springmicroservices.entity.Product;
import com.siva.springmicroservices.exception.InvalidSortDirectionException;
import com.siva.springmicroservices.exception.InvalidSortFieldException;
import com.siva.springmicroservices.exception.ProductNotFoundException;
import com.siva.springmicroservices.repo.ProductRepository;
import com.siva.springmicroservices.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl
        implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .internalCode(request.getInternalCode())
                .build();

        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    public PagedResponse<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        validateSortField(sortBy);
        validateSortDirection(direction);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<Product> productPage =
                productRepository.findAll(pageable);

        List<ProductResponse> products =
                productPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return mapToPagedResponse(products, productPage);
    }

    @Override
    public ProductResponse getProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id : " + id));

        return mapToResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id : " + id));

        productRepository.delete(product);
    }

    @Override
    public ProductResponse updateProduct(Long id,
                                         ProductRequest request) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id : " + id));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());
        existingProduct.setInternalCode(request.getInternalCode());

        Product updatedProduct =
                productRepository.save(existingProduct);

        return mapToResponse(updatedProduct);
    }


    @Override
    public List<ProductResponse> createProducts(
            List<ProductRequest> requests) {

        List<Product> products = requests.stream()
                .map(request -> Product.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .price(request.getPrice())
                        .quantity(request.getQuantity())
                        .internalCode(request.getInternalCode())
                        .build())
                .toList();

        List<Product> savedProducts =
                productRepository.saveAll(products);

        return savedProducts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> updateProducts(
            List<ProductUpdateRequest> requests) {

        List<Product> updatedProducts = requests.stream()
                .map(request -> {

                    Product existingProduct =
                            productRepository.findById(request.getId())
                                    .orElseThrow(() ->
                                            new ProductNotFoundException(
                                                    "Product not found with id : " + request.getId()));

                    existingProduct.setName(request.getName());
                    existingProduct.setDescription(request.getDescription());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setQuantity(request.getQuantity());
                    existingProduct.setInternalCode(
                            request.getInternalCode());

                    return existingProduct;
                })
                .toList();

        List<Product> savedProducts =
                productRepository.saveAll(updatedProducts);

        return savedProducts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PagedResponse<ProductResponse> searchProducts(
            String name,
            int page,
            int size,
            String sortBy,
            String direction) {

        validateSortField(sortBy);
        validateSortDirection(direction);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<Product> productPage =
                productRepository
                        .findByNameContainingIgnoreCase(
                                name,
                                pageable);

        List<ProductResponse> products =
                productPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return mapToPagedResponse(products, productPage);
    }


    private void validateSortField(String sortBy) {
        Set<String> allowedFields = Set.of(
                "id",
                "name",
                "price",
                "quantity"
        );

        if (!allowedFields.contains(sortBy)) {
            throw new InvalidSortFieldException(sortBy);
        }
    }


    private void validateSortDirection(String direction) {

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new InvalidSortDirectionException(direction);
        }
    }


    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    private PagedResponse<ProductResponse> mapToPagedResponse(
            List<ProductResponse> products, Page<Product> productPage ){

        return PagedResponse.<ProductResponse>builder()
                .content(products)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();

    }
}