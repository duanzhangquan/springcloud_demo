package com.product.service;


import com.alibaba.fastjson.JSONObject;
import com.base.entity.Product;
import com.common.exception.CustomDuplicateKeyException;
import com.common.util.enumeration.HttpServletResponseCodeEnum;
import com.pojo.vo.ProductVO;
import com.product.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author duan
 */
@Service
@Slf4j
public class ProductService{

    @Autowired
    ProductMapper productMapper;

    public List<ProductVO> list(){
        List<Product> p = productMapper.selectList(null);
        String json = JSONObject.toJSONString(p);
        List<ProductVO> productVOList = JSONObject.parseArray(json,ProductVO.class);

        return productVOList;
    }

    public void add(Product product){
        try{
            productMapper.insert(product);
        }catch (DuplicateKeyException e){
            log.error("违反唯一索引约束",e);
            HttpServletResponseCodeEnum responseCodeEnum = HttpServletResponseCodeEnum.DATA_REPLICATED;
            responseCodeEnum.setMsg(responseCodeEnum.getMsg()+":"+JSONObject.toJSONString(product));
            throw new CustomDuplicateKeyException(HttpServletResponseCodeEnum.DATA_REPLICATED);
        }
    }

    public void update(Product product){
        try{
            productMapper.updateById(product);
        }catch (DuplicateKeyException e){
            log.error("违反唯一索引约束",e);
            HttpServletResponseCodeEnum responseCodeEnum = HttpServletResponseCodeEnum.DATA_REPLICATED;
            responseCodeEnum.setMsg(responseCodeEnum.getMsg()+":"+JSONObject.toJSONString(product));
            throw new CustomDuplicateKeyException(HttpServletResponseCodeEnum.DATA_REPLICATED);
        }
    }

    public void delete(Serializable id){
        productMapper.deleteById(id);
    }
}
