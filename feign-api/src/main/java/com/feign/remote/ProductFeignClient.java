package com.feign.remote;

import com.alibaba.fastjson.JSONObject;
import com.common.util.HttpServletResponseResultWrappers;
import com.pojo.dto.ProductDTO;
import com.pojo.vo.ProductVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "product")
//注意：需要加上服务熔断降级功能!
public interface ProductFeignClient {

    /**
     * 查询商品列表
     * @return
     */
    @GetMapping(value = "/product/list")
    @ResponseBody
    HttpServletResponseResultWrappers productList(@RequestHeader("token") String token,
                                                                   @RequestHeader("refreshToken") String refreshToken,
                                                                   @RequestHeader("account") String account,
                                                                   @RequestHeader("accountType") Integer accountType);

    /**
     * 新增商品列表
     * @return
     */
    @PostMapping(value = "/product/add")
    @ResponseBody
    HttpServletResponseResultWrappers productAdd(@RequestBody ProductDTO productDTO,
                                                 @RequestHeader("token") String token,
                                                 @RequestHeader("refreshToken") String refreshToken,
                                                 @RequestHeader("account") String account,
                                                 @RequestHeader("accountType") Integer accountType);


    /**
     * 修改商品列表
     * @return
     */
    @PutMapping(value = "/product/update")
    @ResponseBody
    HttpServletResponseResultWrappers productUpdate(@RequestBody ProductDTO productDTO,
                                                    @RequestHeader("token") String token,
                                                    @RequestHeader("refreshToken") String refreshToken,
                                                    @RequestHeader("account") String account,
                                                    @RequestHeader("accountType") Integer accountType);

}
