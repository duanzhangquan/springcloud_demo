package com.product.controller;

import com.product.entity.Product;
import com.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author duan
 */

@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    @ResponseBody
    public List<Product> list(){
        return productService.list();
    }

    @PostMapping("/")
    public void add(@RequestBody Product product){
        productService.add(product);
    }

    @PutMapping("/")
    public void update(@RequestBody Product product){
        productService.update(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        productService.delete(id);
    }
}
