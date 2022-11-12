package com.zhihao.newretail.api.coupons.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponsAddApiDTO {

    private BigDecimal deno;

    private BigDecimal condition;

    private Date startDate;

    private Date endDate;

    private Integer maxNum;

}
