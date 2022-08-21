package com.zhihao.newretail.rbac.controller;

import com.zhihao.newretail.core.util.R;
import com.zhihao.newretail.rbac.context.SysUserTokenContext;
import com.zhihao.newretail.rbac.service.SysProductService;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.security.context.UserLoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class SysProductController {

    @Autowired
    private SysProductService sysProductService;

    @RequiresLogin
    @PostMapping("/spu")
    public R spuAdd(@RequestParam Integer categoryId,
                    @RequestParam String title,
                    @RequestParam String subTitle,
                    @RequestPart MultipartFile showImage,
                    @RequestPart(required = false) MultipartFile[] sliderImage,
                    @RequestParam(required = false) String detailTitle,
                    @RequestParam(required = false) String detailPram,
                    @RequestPart(required = false) MultipartFile[] detailImage) throws IOException {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        sysProductService.addSpu(categoryId, title, subTitle, showImage, sliderImage, detailTitle, detailPram, detailImage);
        UserLoginContext.sysClean();
        return R.ok();
    }

    @RequiresLogin
    @PutMapping("/spu/{spuId}")
    public R spuUpdate(@PathVariable Integer spuId,
                       @RequestParam Integer categoryId,
                       @RequestParam String title,
                       @RequestParam String subTitle,
                       @RequestPart MultipartFile showImage,
                       @RequestPart(required = false) MultipartFile[] sliderImage,
                       @RequestParam(required = false) String detailTitle,
                       @RequestParam(required = false) String detailPram,
                       @RequestPart(required = false) MultipartFile[] detailImage) throws IOException {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        sysProductService.updateSpu(spuId, categoryId, title, subTitle, showImage, sliderImage, detailTitle, detailPram, detailImage);
        UserLoginContext.sysClean();
        return R.ok();
    }

}
