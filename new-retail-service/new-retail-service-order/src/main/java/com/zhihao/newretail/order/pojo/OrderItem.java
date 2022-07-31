package com.zhihao.newretail.order.pojo;

import java.math.BigDecimal;

public class OrderItem {

    private Long orderId;

    private Integer skuId;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private Integer num;

    private Integer orderItemSharding;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getOrderItemSharding() {
        return orderItemSharding;
    }

    public void setOrderItemSharding(Integer orderItemSharding) {
        this.orderItemSharding = orderItemSharding;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + orderId +
                ", skuId=" + skuId +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                ", num=" + num +
                ", orderItemSharding=" + orderItemSharding +
                '}';
    }

}
