package com.zhihao.newretail.api.product.feign;

import com.zhihao.newretail.api.product.dto.SpuAddApiDTO;
import com.zhihao.newretail.api.product.dto.SpuUpdateApiDTO;
import com.zhihao.newretail.api.product.vo.SpuApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@FeignClient(name = "new-retail-product", path = "/product")
public interface SpuFeignService {

    @GetMapping("/api/spu/{spuId}")
    SpuApiVO getSpuApiVO(@PathVariable Integer spuId);

    @PostMapping("/api/spu")
    void addSpu(@Valid @RequestBody SpuAddApiDTO spuAddApiDTO);

    @PutMapping("/api/spu/{spuId}")
    void updateSpu(@PathVariable Integer spuId, @Valid @RequestBody SpuUpdateApiDTO spuUpdateApiDTO);

    @DeleteMapping("/api/spu/{spuId}")
    void deleteSpu(@PathVariable Integer spuId);

}