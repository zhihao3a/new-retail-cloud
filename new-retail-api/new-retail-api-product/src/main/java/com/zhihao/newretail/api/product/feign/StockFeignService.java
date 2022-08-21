package com.zhihao.newretail.api.product.feign;

import com.zhihao.newretail.api.product.dto.SkuStockLockApiDTO;
import com.zhihao.newretail.api.product.dto.SkuStockLockBatchApiDTO;
import com.zhihao.newretail.api.product.vo.SkuStockApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "new-retail-product", path = "/product")
public interface StockFeignService {

    /*
    * 获取库存信息
    * */
    @PostMapping("/api/skuStock/list")
    List<SkuStockApiVO> listSkuStockApiVOS(@RequestBody Set<Integer> skuIdSet);

    /*
    * 锁定商品库存
    * */
    @PostMapping("/api/skuStock/lock")
    void stockLock(@RequestBody SkuStockLockApiDTO skuStockLockApiDTO);

    /*
    * 批量锁定商品库存
    * */
    @PostMapping("/api/skuStock/batchLock")
    int batchStockLock(@RequestBody SkuStockLockBatchApiDTO skuStockLockBatchApiDTO);

}