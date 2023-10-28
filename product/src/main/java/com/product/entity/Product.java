package com.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author duan
 */
@Data
@TableName("product")
public class Product {

    private Integer id;
    private String productName;

}
