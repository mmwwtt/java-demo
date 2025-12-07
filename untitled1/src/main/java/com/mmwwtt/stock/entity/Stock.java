package com.mmwwtt.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("stock_t")
public class Stock {


    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long stockId;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 股票名称
     */
    private String name;

}
