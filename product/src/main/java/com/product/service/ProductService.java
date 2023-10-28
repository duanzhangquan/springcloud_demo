package com.product.service;


import com.product.entity.Product;
import com.product.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author duan
 */
@Service
public class ProductService{

    @Autowired
    ProductMapper productMapper;

    public List<Product> list(){
        return productMapper.selectList(null);
    }

    public void add(Product product){
        productMapper.insert(product);
    }

    public void update(Product product){
        productMapper.updateById(product);
    }

    public void delete(Serializable id){
        productMapper.deleteById(id);
    }
}
