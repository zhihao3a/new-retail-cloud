package com.zhihao.newretail.product.feign;

import com.zhihao.newretail.api.product.dto.SlideAddApiDTO;
import com.zhihao.newretail.api.product.dto.SlideUpdateApiDTO;
import com.zhihao.newretail.api.product.vo.SlideApiVO;
import com.zhihao.newretail.core.util.PageUtil;
import com.zhihao.newretail.product.service.SlideService;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.security.context.UserLoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign")
public class SlideFeignClient {

    @Autowired
    private SlideService slideService;

    @RequiresLogin
    @GetMapping("/slide/list")
    PageUtil<SlideApiVO> listSlideApiVOS(@RequestParam(required = false) Integer slideId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        PageUtil<SlideApiVO> pageData = slideService.listSlideApiVOS(slideId, pageNum, pageSize);
        UserLoginContext.sysClean();
        return pageData;
    }

    @RequiresLogin
    @PostMapping("/slide")
    Integer addSlide(@RequestBody SlideAddApiDTO slideAddApiDTO) {
        int insertRow = slideService.insertSlide(slideAddApiDTO);
        UserLoginContext.sysClean();
        return insertRow;
    }

    @RequiresLogin
    @PutMapping("/slide/{slideId}")
    Integer updateSlide(@PathVariable Integer slideId, @RequestBody SlideUpdateApiDTO slideUpdateApiDTO) {
        int updateRow = slideService.updateSlide(slideId, slideUpdateApiDTO);
        UserLoginContext.sysClean();
        return updateRow;
    }

    @RequiresLogin
    @DeleteMapping("/slide/{slideId}")
    Integer deleteSlide(@PathVariable Integer slideId) {
        int deleteRow = slideService.deleteSlide(slideId);
        UserLoginContext.sysClean();
        return deleteRow;
    }

}
