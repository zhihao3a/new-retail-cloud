package com.zhihao.newretail.api.user.feign;

import com.zhihao.newretail.api.user.dto.UserCouponsApiDTO;
import com.zhihao.newretail.api.user.fallback.UserCouponsFeignFallback;
import com.zhihao.newretail.api.user.vo.UserCouponsApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Primary
@FeignClient(name = "new-retail-user", path = "/new-retail-user", fallback = UserCouponsFeignFallback.class)
public interface UserCouponsFeignService {

    @GetMapping("/feign/user-coupons/list")
    List<UserCouponsApiVO> listUserCouponsApiVOS();

    @PutMapping("/feign/user-coupons")
    int consumeCoupons(@RequestBody UserCouponsApiDTO userCouponsApiDTO);

}
