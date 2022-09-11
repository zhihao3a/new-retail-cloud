package com.zhihao.newretail.api.product.feign;

import com.zhihao.newretail.api.product.dto.SlideAddApiDTO;
import com.zhihao.newretail.api.product.dto.SlideUpdateApiDTO;
import com.zhihao.newretail.api.product.fallback.SlideFeignFallback;
import com.zhihao.newretail.api.product.vo.SlideApiVO;
import com.zhihao.newretail.core.util.PageUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

@Primary
@FeignClient(name = "new-retail-product", path = "/product", fallback = SlideFeignFallback.class)
public interface SlideFeignService {

    /*
    * 轮播图列表
    * */
    @GetMapping("/api/slide/list")
    PageUtil<SlideApiVO> listSlideApiVOS(@RequestParam(required = false) Integer slideId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize);

    /*
    * 新增首页轮播图
    * */
    @PostMapping("/api/slide")
    Integer addSlide(@RequestBody SlideAddApiDTO slideAddApiDTO);

    /*
    * 修改首页轮播图
    * */
    @PutMapping("/api/slide/{slideId}")
    Integer updateSlide(@PathVariable Integer slideId, @RequestBody SlideUpdateApiDTO slideUpdateApiDTO);

    /*
    * 删除首页轮播图
    * */
    @DeleteMapping("/api/slide/{slideId}")
    Integer deleteSlide(@PathVariable Integer slideId);

}