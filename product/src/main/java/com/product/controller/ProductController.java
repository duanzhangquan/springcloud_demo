package com.product.controller;

import com.base.entity.Product;
import com.common.exception.CustomDuplicateKeyException;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.pojo.vo.ProductVO;
import com.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duan
 */

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public List<ProductVO> list(){
        return productService.list();
    }

    @PostMapping("/add")
    public void add(@RequestBody Product product) throws CustomDuplicateKeyException {
        productService.add(product);
    }

    @PutMapping("/update")
    public void update(@RequestBody Product product) throws CustomDuplicateKeyException{
        productService.update(product);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") long id){
        productService.delete(id);
    }
}
