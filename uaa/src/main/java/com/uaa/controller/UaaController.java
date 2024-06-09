package com.uaa.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;



/**
 * @author duan
 */

@RestController
//@RequestMapping("/uaa")
@Slf4j
public class UaaController {


    @GetMapping("/test")
    public void test(){
        log.info("---test---");
    }

    /**
     * 注销登录,标记用户为离线状态
     */
    public void logout(){
        DuplicateKeyException d;
    }

}

