package com.siva.springmicroservices.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String internalCode;
}
