
package com.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * @author duan
 */

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String productName;

}

