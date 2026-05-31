package com.siva.springmicroservices.service;

import com.siva.springmicroservices.dto.ProductRequest;
import com.siva.springmicroservices.dto.ProductResponse;
import com.siva.springmicroservices.dto.ProductUpdateRequest;
import com.siva.springmicroservices.entity.Product;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProduct(Long id);

    void deleteProduct(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    List<ProductResponse> createProducts(List<ProductRequest> requests);

    List<ProductResponse> updateProducts(List<ProductUpdateRequest> requests);
}
