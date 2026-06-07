package com.siva.springmicroservices.service;

import com.siva.springmicroservices.dto.PagedResponse;
import com.siva.springmicroservices.dto.ProductRequest;
import com.siva.springmicroservices.dto.ProductResponse;
import com.siva.springmicroservices.dto.ProductUpdateRequest;
import com.siva.springmicroservices.entity.Product;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    PagedResponse<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction);

    PagedResponse<ProductResponse> searchProducts(
            String name,
            int page,
            int size,
            String sortBy,
            String direction);

    ProductResponse getProduct(Long id);

    void deleteProduct(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    List<ProductResponse> createProducts(List<ProductRequest> requests);

    List<ProductResponse> updateProducts(List<ProductUpdateRequest> requests);
}
