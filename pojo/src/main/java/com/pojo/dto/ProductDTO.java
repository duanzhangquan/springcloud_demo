package com.pojo.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Integer id;

    private String productName;

    public ProductDTO(Integer id, String productName) {
        this.id = id;
        this.productName = productName;
    }

    public ProductDTO(String productName) {
        this.productName = productName;
    }

    public ProductDTO() {
    }
}
