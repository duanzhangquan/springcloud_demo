package com.common.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONUtil {

    /**
     * 判断一个字符串是否符合JSON格式
     */
    public static boolean isJson(String jsonStr){
        try{
            JSONObject obj = JSONObject.parseObject(jsonStr);
            return true;
        }catch (JSONException e){
            log.debug(jsonStr+" 不是json格式");
            return false;
        }
    }
}
