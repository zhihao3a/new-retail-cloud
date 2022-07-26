package com.zhihao.newretail.api.user.feign;

import com.zhihao.newretail.api.user.fallback.UserAddressFeignFallback;
import com.zhihao.newretail.api.user.vo.UserAddressApiVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@Primary
@FeignClient(name = "new-retail-user", path = "/new-retail-user", fallback = UserAddressFeignFallback.class)
public interface UserAddressFeignService {

    @GetMapping("/feign/address/list")
    List<UserAddressApiVO> listUserAddressApiVOS();

    @GetMapping("/feign/address/{addressId}")
    UserAddressApiVO getUserAddressApiVO(@PathVariable Integer addressId);

    @PostMapping("/feign/address/list")
    List<UserAddressApiVO> listUserAddressApiVOS(@RequestBody Set<Integer> userIdSet);

}
