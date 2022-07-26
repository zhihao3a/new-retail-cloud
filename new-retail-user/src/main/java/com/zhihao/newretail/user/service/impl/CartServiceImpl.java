package com.zhihao.newretail.user.service.impl;

import com.zhihao.newretail.api.product.feign.ProductFeignService;
import com.zhihao.newretail.api.product.vo.GoodsApiVO;
import com.zhihao.newretail.api.user.vo.CartApiVO;
import com.zhihao.newretail.core.exception.ServiceException;
import com.zhihao.newretail.redis.util.MyRedisUtil;
import com.zhihao.newretail.user.form.CartAddForm;
import com.zhihao.newretail.user.form.CartUpdateForm;
import com.zhihao.newretail.user.pojo.Cart;
import com.zhihao.newretail.user.service.CartService;
import com.zhihao.newretail.user.vo.CartProductVO;
import com.zhihao.newretail.user.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private MyRedisUtil redisUtil;

    @Autowired
    private ProductFeignService productFeignService;

    private static final String CART_REDIS_KEY = "cart_user_id_%d";

    @Override
    public CartVO getCartVO(Integer userId) {
        /* 初始化购物车显示信息 */
        boolean selectedAll = true;     // 是否全选
        Integer totalQuantity = 0;      // 购物车商品总数
        BigDecimal cartTotalPrice = BigDecimal.ZERO;    // 总金额
        List<CartProductVO> cartProductVOList = new ArrayList<>();  // 购物车商品列表

        /* 获取redis内容 */
        String redisKey = String.format(CART_REDIS_KEY, userId);
        Map<Object, Object> redisMap = redisUtil.getMap(redisKey);

        /* 获取skuId集合 */
        Set<Integer> skuIdSet = new HashSet<>();
        redisMap.forEach((k, v) -> skuIdSet.add((Integer) k));
        if (!CollectionUtils.isEmpty(skuIdSet)) {
            /* 获取购物车商品 */
            try {
                List<GoodsApiVO> goodsApiVOList = productFeignService.listGoodsApiVOS(skuIdSet);
                for (Map.Entry<Object, Object> entry : redisMap.entrySet()) {
                    Cart cart = (Cart) entry.getValue();
                    CartProductVO cartProductVO = new CartProductVO();
                    goodsApiVOList.stream().filter(goodsApiVO -> cart.getSkuId().equals(goodsApiVO.getId())).forEach(goodsApiVO -> {
                        buildCartProductVO(cart, goodsApiVO, cartProductVO);
                        cartProductVOList.add(cartProductVO);
                    });
                    if (!cart.getSelected()) {
                        selectedAll = false;
                    }
                    totalQuantity += cart.getQuantity();
                    if (cart.getSelected()) {
                        cartTotalPrice = cartTotalPrice.add(cartProductVO.getTotalPrice());
                    }
                }
            } catch (NullPointerException e) {
                throw new ServiceException("商品服务繁忙");
            }
        }
        return buildCartVO(selectedAll, totalQuantity, cartTotalPrice, cartProductVOList);
    }

    @Override
    public CartVO addCart(Integer userId, CartAddForm form) {
        try {
            GoodsApiVO goodsApiVO = productFeignService.getGoodsApiVO(form.getSkuId());
            if (!ObjectUtils.isEmpty(goodsApiVO.getId())) {
                String redisKey = String.format(CART_REDIS_KEY, userId);
                Cart cart = (Cart) redisUtil.getMapValue(redisKey, goodsApiVO.getId());
                if (ObjectUtils.isEmpty(cart)) {
                    cart = new Cart(goodsApiVO.getSpuId(), goodsApiVO.getId(), form.getQuantity(), form.getSelected());
                } else {
                    cart.setQuantity(cart.getQuantity() + form.getQuantity());
                }
                redisUtil.setHash(redisKey, goodsApiVO.getId(), cart);
                return getCartVO(userId);
            }
            throw new ServiceException("商品下架或删除");
        } catch (NullPointerException e) {
            throw new ServiceException("商品服务繁忙");
        }
    }

    @Override
    public CartVO updateCart(Integer userId, CartUpdateForm form) {
        String redisKey = String.format(CART_REDIS_KEY, userId);
        Cart cart = (Cart) redisUtil.getMapValue(redisKey, form.getSkuId());
        if (!ObjectUtils.isEmpty(cart)) {
            if (form.getQuantity() != null && form.getQuantity() >= 0) {
                cart.setQuantity(form.getQuantity());
            }
            if (form.getSelected() != null) {
                cart.setSelected(form.getSelected());
            }
            redisUtil.setHash(redisKey, form.getSkuId(), cart);
            return getCartVO(userId);
        }
        throw new ServiceException("购物车无此商品");
    }

    @Override
    public CartVO deleteCart(Integer userId, Integer skuId) {
        String redisKey = String.format(CART_REDIS_KEY, userId);
        Cart cart = (Cart) redisUtil.getMapValue(redisKey, skuId);
        if (!ObjectUtils.isEmpty(cart)) {
            redisUtil.deleteEntry(redisKey, skuId);
            return getCartVO(userId);
        }
        throw new ServiceException("购物车无此商品");
    }

    @Override
    public CartVO updateCartSelectedAll(Integer userId) {
        String redisKey = String.format(CART_REDIS_KEY, userId);
        List<Cart> cartList = listCartS(userId);
        if (!CollectionUtils.isEmpty(cartList)) {
            cartList.forEach(cart -> {
                cart.setSelected(true);
                redisUtil.setHash(redisKey, cart.getSkuId(), cart);
            });
        }
        return getCartVO(userId);
    }

    @Override
    public CartVO updateCartNotSelectedAll(Integer userId) {
        String redisKey = String.format(CART_REDIS_KEY, userId);
        List<Cart> cartList = listCartS(userId);
        if (!CollectionUtils.isEmpty(cartList)) {
            cartList.forEach(cart -> {
                cart.setSelected(false);
                redisUtil.setHash(redisKey, cart.getSkuId(), cart);
            });
        }
        return getCartVO(userId);
    }

    @Override
    public Integer getQuantity(Integer userId) {
        return listCartS(userId).stream().map(Cart::getQuantity).reduce(0, Integer::sum);
    }

    @Override
    public List<CartApiVO> listCartApiVOS(Integer userId) {
        List<Cart> cartList = listCartS(userId);
        return cartList.stream().filter(Cart::getSelected).map(this::cart2CartApiVO).collect(Collectors.toList());
    }

    @Override
    public void deleteCartBySelected(Integer userId) {
        String redisKey = String.format(CART_REDIS_KEY, userId);
        List<Cart> cartList = listCartS(userId);
        for (Cart cart : cartList) {
            if (cart.getSelected()) {
                redisUtil.deleteEntry(redisKey, cart.getSkuId());
            }
        }
    }

    private CartVO buildCartVO(boolean selectedAll,
                               Integer totalQuantity,
                               BigDecimal cartTotalPrice,
                               List<CartProductVO> cartProductVOList) {
        CartVO cartVO = new CartVO();
        cartVO.setSelectedAll(selectedAll);
        cartVO.setTotalQuantity(totalQuantity);
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);
        return cartVO;
    }

    private void buildCartProductVO(Cart cart, GoodsApiVO goodsApiVO, CartProductVO cartProductVO) {
        cartProductVO.setSpuId(goodsApiVO.getSpuId());    // 商品ID
        cartProductVO.setSkuId(goodsApiVO.getId());       // 商品规格ID
        cartProductVO.setTitle(goodsApiVO.getTitle());    // 商品标题
        cartProductVO.setSkuImage(goodsApiVO.getSkuImage());  // 商品图片地址
        cartProductVO.setParam(goodsApiVO.getParam());        // 商品规格参数
        cartProductVO.setPrice(goodsApiVO.getPrice());        // 单价
        cartProductVO.setQuantity(cart.getQuantity());      // 数量
        cartProductVO.setTotalPrice(goodsApiVO.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));  // 总价
        cartProductVO.setIsSaleable(goodsApiVO.getIsSaleable());  // 是否有效
        cartProductVO.setSelected(cart.getSelected());          // 是否选中
    }

    private List<Cart> listCartS(Integer userId) {
        String redisKey = String.format(CART_REDIS_KEY, userId);
        Map<Object, Object> redisMap = redisUtil.getMap(redisKey);
        List<Cart> cartList = new ArrayList<>();
        redisMap.forEach((k, v) -> cartList.add((Cart) v));
        return cartList;
    }

    private CartApiVO cart2CartApiVO(Cart cart) {
        CartApiVO cartApiVO = new CartApiVO();
        BeanUtils.copyProperties(cart, cartApiVO);
        return cartApiVO;
    }

}
