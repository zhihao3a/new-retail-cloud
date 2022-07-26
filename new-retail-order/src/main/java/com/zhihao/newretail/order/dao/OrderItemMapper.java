package com.zhihao.newretail.order.dao;

import com.zhihao.newretail.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface OrderItemMapper {

    int deleteByOrderId(Long orderId);

    int deleteBySkuId(Integer skuId);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByOrderId(Long orderId);

    OrderItem selectBySkuId(Integer skuId);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /*
    * 批量插入
    * */
    int insertBatch(@Param("orderItemList") List<OrderItem> orderItemList);

    /*
    * 订单号获取订单项集合
    * */
    List<OrderItem> selectListByOrderId(Long orderId);

    /*
    * 订单号批量获取订单项集合
    * */
    List<OrderItem> selectListByOrderIdSet(@Param("orderIdSet") Set<Long> orderIdSet);

}
