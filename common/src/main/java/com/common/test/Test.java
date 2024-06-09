package com.common.test;

import com.common.exception.CustomDuplicateKeyException;
import com.common.util.enumeration.HttpServletResponseCodeEnum;

public class Test {
    public static void main(String[] args) {
        System.out.println(1111);

        try{
            test();
        }catch (CustomDuplicateKeyException e){
            System.out.println(e.getBizResponseCodeEnum().getCode());
            System.out.println(e.getBizResponseCodeEnum().getMsg());
            e.printStackTrace();
        }

    }

    public static void test(){
        HttpServletResponseCodeEnum e = HttpServletResponseCodeEnum.DATA_REPLICATED;
        e.setMsg(e.getMsg()+":id=1, productName='苹果笔记本电脑'");
        throw new CustomDuplicateKeyException(HttpServletResponseCodeEnum.DATA_REPLICATED);
    }



}
