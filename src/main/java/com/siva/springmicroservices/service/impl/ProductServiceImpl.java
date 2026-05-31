package com.siva.springmicroservices.service.impl;

import com.siva.springmicroservices.dto.ProductRequest;
import com.siva.springmicroservices.dto.ProductResponse;
import com.siva.springmicroservices.dto.ProductUpdateRequest;
import com.siva.springmicroservices.entity.Product;
import com.siva.springmicroservices.exception.ProductNotFoundException;
import com.siva.springmicroservices.repo.ProductRepository;
import com.siva.springmicroservices.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
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


    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}