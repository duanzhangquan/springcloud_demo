package com.oauth2.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * 商品增删改查页面渲染controller
 * 注意: oauth2服务不是前后端分离项目,所以使用的是传统的SpringMvc后端渲染页面
 */
@Controller
@Slf4j
public class ProductCRUDPageController {

    /**
     * 修改商品页面
     * @param model
     * @param request
     * @return
     */
    @GetMapping(value = "/product/goProductUpdatePage")
    public String goProductUpdatePage(Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        String productName = request.getParameter("productName");
        String productNameStr = new String(productName.getBytes("utf-8"));
        log.debug("params--- id:" + id);
        log.debug("params--- productName:" + productNameStr);

        model.addAttribute("id", id);
        model.addAttribute("productName", productName);
        return "productUpdatePage";
    }

    /**
     * 新增商品页面
     */
    @GetMapping(value = "/product/goProductAddPage")
    public String goProductAddPage() {
        return "productAddPage";
    }
}
