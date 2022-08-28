package com.zhihao.newretail.coupons.feign;

import com.zhihao.newretail.api.coupons.dto.CouponsAddApiDTO;
import com.zhihao.newretail.api.coupons.vo.CouponsApiVO;
import com.zhihao.newretail.coupons.service.CouponsService;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.security.context.UserLoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class CouponsFeignClient {

    @Autowired
    private CouponsService couponsService;

    @GetMapping("/api/coupons/{couponsId}")
    public CouponsApiVO getCouponsApiVO(@PathVariable Integer couponsId) {
        return couponsService.getCouponsApiVO(couponsId);
    }

    @PostMapping("/api/coupons/list")
    public List<CouponsApiVO> listCouponsApiVOS(@RequestBody Set<Integer> couponsIdSet) {
        return couponsService.listCouponsApiVOS(couponsIdSet);
    }

    @RequiresLogin
    @PostMapping("/api/coupons")
    Integer insertCoupons(@RequestBody CouponsAddApiDTO couponsAddApiDTO) {
        int insertRow = couponsService.insertCoupons(couponsAddApiDTO);
        UserLoginContext.sysClean();
        return insertRow;
    }

}
