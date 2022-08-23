package com.zhihao.newretail.api.product.feign;

import com.zhihao.newretail.api.product.fallback.ProductFeignFallback;
import com.zhihao.newretail.api.product.vo.GoodsApiVO;
import com.zhihao.newretail.api.product.vo.ProductApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Primary
@FeignClient(name = "new-retail-product", path = "/product", fallback = ProductFeignFallback.class)
public interface ProductFeignService {

    @PostMapping("/api/goods/list")
    List<GoodsApiVO> listGoodsApiVOS(@RequestBody Set<Integer> idSet);

    @GetMapping("/api/goods/{skuId}")
    GoodsApiVO getGoodsApiVO(@PathVariable Integer skuId);

    @GetMapping("/api/product/list")
    List<ProductApiVO> listProductApiVOS(@RequestParam(required = false) Integer categoryId);

}
