package com.tuyu.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单实体
 *
 * @author tuyu
 * @date 4/9/19
 * Talk is cheap, show me the code.
 */
@Data
public class Order implements Serializable{

    private static final long serialVersionUID = -748722631383469963L;

    private Long id;
    private String orderId;
    private String productId;
    private String productName;
    private Integer num;
    private Integer price;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private Boolean valid;
}
