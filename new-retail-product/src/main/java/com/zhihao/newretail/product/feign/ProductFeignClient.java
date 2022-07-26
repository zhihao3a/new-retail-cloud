package com.zhihao.newretail.product.feign;

import com.zhihao.newretail.api.product.vo.GoodsApiVO;
import com.zhihao.newretail.api.product.vo.ProductApiVO;
import com.zhihao.newretail.core.util.PageUtil;
import com.zhihao.newretail.product.service.ProductService;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.security.context.UserLoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/feign")
public class ProductFeignClient {

    @Autowired
    private ProductService productService;

    @PostMapping("/goods/list")
    public List<GoodsApiVO> listGoodsApiVOS(@RequestBody Set<Integer> idSet) {
        return productService.listGoodsApiVOS(idSet);
    }

    @GetMapping("/goods/{skuId}")
    public GoodsApiVO getGoodsApiVO(@PathVariable Integer skuId) {
        return productService.getGoodsApiVO(skuId);
    }

    @RequiresLogin
    @GetMapping("/product/list")
    public PageUtil<ProductApiVO> listProductApiVOS(@RequestParam(required = false) Integer categoryId,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        PageUtil<ProductApiVO> pageData = productService.listProductApiVOS(categoryId, pageNum, pageSize);
        UserLoginContext.sysClean();
        return pageData;
    }

}
