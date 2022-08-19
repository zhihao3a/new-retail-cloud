package com.zhihao.newretail.api.product.feign;

import com.zhihao.newretail.api.product.dto.SpuAddApiDTO;
import com.zhihao.newretail.api.product.dto.SpuUpdateApiDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "new-retail-product", path = "/product")
public interface SpuFeignService {

    @PostMapping("/api/spu")
    void addSpu(@Valid @RequestBody SpuAddApiDTO spuAddApiDTO);

    @PutMapping("/api/spu/{spuId}")
    void updateSpu(@PathVariable Integer spuId, @Valid @RequestBody SpuUpdateApiDTO spuUpdateApiDTO);

}
