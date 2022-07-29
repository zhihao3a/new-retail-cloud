package com.zhihao.newretail.api.product.feign;

import com.zhihao.newretail.api.product.dto.SkuStockBatchApiDTO;
import com.zhihao.newretail.api.product.dto.SkuStockLockApiDTO;
import com.zhihao.newretail.api.product.vo.SkuStockApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "new-retail-product", path = "/product")
public interface ProductStockFeignService {

    @PostMapping("/api/listSkuStocks")
    List<SkuStockApiVO> listSkuStockApiVOs(@RequestBody SkuStockBatchApiDTO skuStockBatchApiDTO);

    /*
    * 锁定商品库存
    * */
    @PostMapping("/api/stockLock")
    void stockLock(@RequestBody SkuStockLockApiDTO skuStockLockApiDTO);

}
