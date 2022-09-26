package com.zhihao.newretail.admin.controller;

import com.zhihao.newretail.api.product.vo.ProductApiVO;
import com.zhihao.newretail.api.product.vo.SpuApiVO;
import com.zhihao.newretail.core.exception.ServiceException;
import com.zhihao.newretail.core.util.PageUtil;
import com.zhihao.newretail.core.util.R;
import com.zhihao.newretail.admin.context.SysUserTokenContext;
import com.zhihao.newretail.admin.form.SkuForm;
import com.zhihao.newretail.admin.form.SpuForm;
import com.zhihao.newretail.admin.service.SysProductService;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.security.context.UserLoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class SysProductController {

    @Autowired
    private SysProductService sysProductService;

    @RequiresLogin
    @GetMapping("/product/list")
    public R productList(@RequestParam(required = false) Integer categoryId,
                         @RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "10") Integer pageSize) {
        if (pageNum == 0 || pageSize == 0) {
            throw new ServiceException("分页参数不能为0");
        }
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        PageUtil<ProductApiVO> pageData = sysProductService.listProductApiVOS(categoryId, pageNum, pageSize);
        UserLoginContext.sysClean();
        return R.ok().put("data", pageData);
    }

    @RequiresLogin
    @GetMapping("/spu/{spuId}")
    public R spuInfo(@PathVariable Integer spuId) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        SpuApiVO spuApiVO = sysProductService.getSpuApiVO(spuId);
        UserLoginContext.sysClean();
        return R.ok().put("data", spuApiVO);
    }

    @RequiresLogin
    @PostMapping("/spu")
    public R spu(@Valid @RequestBody SpuForm form) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer insertRow = sysProductService.addSpu(form);
        UserLoginContext.sysClean();
        if (insertRow == null) {
            throw new ServiceException("商品服务繁忙");
        }
        if (insertRow <= 0) {
            throw new ServiceException("新增商品失败");
        }
        return R.ok();
    }

    @RequiresLogin
    @PutMapping("/spu/{spuId}")
    public R spu(@PathVariable Integer spuId, @Valid @RequestBody SpuForm form) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer updateRow = sysProductService.updateSpu(spuId, form);
        UserLoginContext.sysClean();
        if (updateRow == null) {
            throw new ServiceException("商品服务繁忙");
        }
        if (updateRow <= 0) {
            throw new ServiceException("修改商品失败");
        }
        return R.ok();
    }

    @RequiresLogin
    @DeleteMapping("/spu/{spuId}")
    public R spu(@PathVariable Integer spuId) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer deleteRow = sysProductService.deleteSpu(spuId);
        UserLoginContext.sysClean();
        if (deleteRow == null) {
            throw new ServiceException("商品服务繁忙");
        }
        if (deleteRow <= 0) {
            throw new ServiceException("删除商品失败");
        }
        return R.ok();
    }

    @RequiresLogin
    @PostMapping("/sku")
    public R sku(@Valid @RequestBody SkuForm form) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer insertRow = sysProductService.addSku(form);
        UserLoginContext.sysClean();
        if (insertRow == null) {
            throw new ServiceException("商品服务繁忙");
        }
        if (insertRow <= 0) {
            throw new ServiceException("新增商品规格失败");
        }
        return R.ok();
    }

    @RequiresLogin
    @PutMapping("/sku/{skuId}")
    public R sku(@PathVariable Integer skuId, @Valid @RequestBody SkuForm form) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer updateRow = sysProductService.updateSku(skuId, form);
        UserLoginContext.sysClean();
        if (updateRow == null) {
            throw new ServiceException("商品服务繁忙");
        }
        if (updateRow <= 0) {
            throw new ServiceException("修改商品规格失败");
        }
        return R.ok();
    }

    @RequiresLogin
    @DeleteMapping("/sku/{skuId}")
    public R sku(@PathVariable Integer skuId) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer deleteRow = sysProductService.deleteSku(skuId);
        UserLoginContext.sysClean();
        if (deleteRow == null) {
            throw new ServiceException("商品服务繁忙");
        }
        if (deleteRow <= 0) {
            throw new ServiceException("删除商品规格失败");
        }
        return R.ok();
    }

    @RequiresLogin
    @PostMapping("/spu/upload/image")
    public R uploadSpuImage(@RequestPart MultipartFile file) throws IOException {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        String imageUrl = sysProductService.uploadSpuImage(file);
        UserLoginContext.sysClean();
        return R.ok().put("data", imageUrl);
    }

    @RequiresLogin
    @PostMapping("/sku/upload/image")
    public R uploadSkuImage(@RequestPart MultipartFile file) throws IOException {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        String imageUrl = sysProductService.uploadSkuImage(file);
        UserLoginContext.sysClean();
        return R.ok().put("data", imageUrl);
    }

    @RequiresLogin
    @PostMapping("/spu/upload/slider")
    public R uploadSliderImage(@RequestPart MultipartFile[] files) throws IOException {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        List<String> imageUrlList = sysProductService.uploadSpuSliderImage(files);
        UserLoginContext.sysClean();
        return R.ok().put("data", imageUrlList);
    }

    @RequiresLogin
    @PostMapping("/spu/upload/detail")
    public R uploadDetailImage(@RequestPart MultipartFile[] files) throws IOException {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        List<String> imageUrlList = sysProductService.uploadSpuDetailImage(files);
        UserLoginContext.sysClean();
        return R.ok().put("data", imageUrlList);
    }

}