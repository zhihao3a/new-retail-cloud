package com.zhihao.newretail.product.service;

import com.zhihao.newretail.api.product.vo.GoodsApiVO;
import com.zhihao.newretail.api.product.vo.ProductApiVO;
import com.zhihao.newretail.core.util.PageUtil;
import com.zhihao.newretail.product.vo.ProductVO;
import com.zhihao.newretail.product.vo.ProductDetailVO;

import java.util.List;
import java.util.Set;

public interface ProductService {

    /*
    * 获取商品详情
    * */
    ProductDetailVO getProductDetailVO(Integer spuId);

    /*
    * 批量获取商品
    * */
    List<GoodsApiVO> listGoodsApiVOS(Set<Integer> skuIdSet);

    /*
    * 获取sku
    * */
    GoodsApiVO getGoodsApiVO(Integer skuId);

    /*
    * 获取商品列表
    * */
    PageUtil<ProductVO> listProductVOS(Integer categoryId, Integer pageNum, Integer pageSize);

    /*
    * 获取商品列表(feign)
    * */
    PageUtil<ProductApiVO> listProductApiVOS(Integer categoryId, Integer pageNum, Integer pageSize);

}
