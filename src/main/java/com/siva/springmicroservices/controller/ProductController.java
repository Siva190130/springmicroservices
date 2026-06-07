package com.siva.springmicroservices.controller;

import com.siva.springmicroservices.dto.PagedResponse;
import com.siva.springmicroservices.dto.ProductRequest;
import com.siva.springmicroservices.dto.ProductResponse;
import com.siva.springmicroservices.dto.ProductUpdateRequest;
import com.siva.springmicroservices.entity.Product;
import com.siva.springmicroservices.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping
    public ResponseEntity<ProductResponse>
    createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(

            @Min(value = 0,
                    message = "Page number must be greater than or equal to 0")
            @RequestParam(defaultValue = "0")
            int page,

            @Min(value = 1,
                    message = "Page size must be at least 1")
            @Max(value = 20,
                    message = "Page size cannot exceed 100")
            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                productService.getAllProducts(
                        page,
                        size,
                        sortBy,
                        direction));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(

            @NotBlank(message = "Search name cannot be blank")
            @RequestParam String name,

            @Min(value = 0,
                    message = "Page number must be greater than or equal to 0")
            @RequestParam(defaultValue = "0")
            int page,

            @Min(value = 1,
                    message = "Page size must be at least 1")
            @Max(value = 100,
                    message = "Page size cannot exceed 100")
            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                productService.searchProducts(
                        name,
                        page,
                        size,
                        sortBy,
                        direction));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse>
    getProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProduct(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse>
    updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(
                        id,
                        request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ProductResponse>>
    createProducts(
            @Valid @RequestBody
            List<ProductRequest> requests) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        productService
                                .createProducts(requests));
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<ProductResponse>>
    updateProducts(
            @Valid @RequestBody
            List<ProductUpdateRequest> requests) {

        return ResponseEntity.ok(
                productService.updateProducts(
                        requests));
    }
}