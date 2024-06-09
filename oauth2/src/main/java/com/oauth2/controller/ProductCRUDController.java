package com.oauth2.controller;

import com.common.util.HttpServletResponseResultWrappers;
import com.feign.remote.ProductFeignClient;
import com.oauth2.service.GiteeOauth2UserService;
import com.pojo.dto.ProductDTO;

import com.pojo.vo.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * 商品增删改查controller
 */
@Controller
@Slf4j
public class ProductCRUDController {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private GiteeOauth2UserService giteeOauth2UserService;


    /**
     * 查询商品列表
     * @param model
     * @param request
     * @return
     */
    @GetMapping(value = "/product/list")
    public String productList(Model model, HttpServletRequest request) {
        UserTokenVO u = giteeOauth2UserService.feignInvokeParamsPrepare(request);
        HttpServletResponseResultWrappers result =
                productFeignClient.productList(u.getToken(), u.getRefreshToken(), u.getAccount(), u.getAccountType());
        if (result.getCode() != 200) {
            log.error("openfeign远程调用返回异常,code:" + result.getCode() + ",message:" + result.getMsg());
            model.addAttribute("error", "发生异常");
            return "error";
        }

        model.addAttribute("productList", result.getData());
        return "productList";
    }

    /**
     * 根据id删除商品
     * @param model
     * @param request
     * @return
     */
    @GetMapping(value = "/product/delete")
    public String productDelete(Model model, HttpServletRequest request) {
        UserTokenVO u = giteeOauth2UserService.feignInvokeParamsPrepare(request);
        Integer id = Integer.parseInt(request.getParameter("id"));

        log.debug("params--- id:" + id);
        HttpServletResponseResultWrappers result =
                productFeignClient.productDelete(id, u.getToken(), u.getRefreshToken(), u.getAccount(),
                        u.getAccountType());
        if (result.getCode() != 200) {
            log.error("openfeign远程调用返回异常,code:" + result.getCode() + ",message:" + result.getMsg());
            model.addAttribute("error", "发生异常");
            return "error";
        }

        return "redirect:/product/list";
    }

    /**
     * 保存新增的商品
     * @param model
     * @param request
     * @return
     */
    @PostMapping(value = "/product/add")
    public String productAdd(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        UserTokenVO u = giteeOauth2UserService.feignInvokeParamsPrepare(request);
        String productName = new String(request.getParameter("productName").getBytes("utf-8"));

        log.debug("params--- productName:" + productName);
        ProductDTO productDTO = new ProductDTO(productName);
        HttpServletResponseResultWrappers result =
                productFeignClient.productAdd(productDTO, u.getToken(), u.getRefreshToken(), u.getAccount(),
                        u.getAccountType());
        if (result.getCode() != 200) {
            log.error("openfeign远程调用返回异常,code:" + result.getCode() + ",message:" + result.getMsg());
            model.addAttribute("error", "发生异常");
            return "error";
        }

        // "forward:/product/list"; 请注意：这里不能使用转发，否则springSecurity会
        //报AccessDeniedException异常
        return "redirect:/product/list";
    }

    /**
     * 保存修改的商品
     * @param model
     * @param request
     * @return
     */
    @PutMapping(value = "/product/update")
    public String productUpdate(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        UserTokenVO u = giteeOauth2UserService.feignInvokeParamsPrepare(request);
        Integer id = Integer.parseInt(request.getParameter("id"));
        String productName = new String(request.getParameter("productName").getBytes("utf-8"));
        log.debug("params--- id:" + id);
        log.debug("params--- productName:" + productName);

        ProductDTO productDTO = new ProductDTO(id, productName);
        HttpServletResponseResultWrappers result =
                productFeignClient.productUpdate(productDTO, u.getToken(), u.getRefreshToken(), u.getAccount(),
                        u.getAccountType());
      /*  if (result.getCode() != 200) {
            log.error("openfeign远程调用返回异常,code:" + result.getCode() + ",message:" + result.getMsg());
            model.addAttribute("error", "发生异常");
            return "error";
        }*/

        return "redirect:/product/list";
    }
}
