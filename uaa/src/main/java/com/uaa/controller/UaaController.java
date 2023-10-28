package com.uaa.controller;

import com.uaa.entity.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author duan
 */
@RestController
@RequestMapping("/uaa")
public class UaaController {


    /**
     * 用户登录
     */

    @GetMapping("/login")
    @ResponseBody
    public User login(){
        return new User();
    }
}
