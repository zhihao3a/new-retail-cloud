package com.zhihao.newretail.product.service.impl;

import com.zhihao.newretail.api.product.vo.ProductApiVO;
import com.zhihao.newretail.api.product.vo.SkuApiVO;
import com.zhihao.newretail.core.enums.DeleteEnum;
import com.zhihao.newretail.core.exception.ServiceException;
import com.zhihao.newretail.product.dao.SkuMapper;
import com.zhihao.newretail.product.dao.SpuInfoMapper;
import com.zhihao.newretail.product.dao.SpuMapper;
import com.zhihao.newretail.product.enums.ProductEnum;
import com.zhihao.newretail.product.pojo.Sku;
import com.zhihao.newretail.product.pojo.Spu;
import com.zhihao.newretail.product.pojo.SpuInfo;
import com.zhihao.newretail.product.pojo.vo.ProductDetailVO;
import com.zhihao.newretail.product.pojo.vo.ProductInfoVO;
import com.zhihao.newretail.product.pojo.vo.SkuVO;
import com.zhihao.newretail.product.service.ProductService;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public ProductDetailVO getProductDetailVO(Integer spuId) throws ExecutionException, InterruptedException {
        ProductDetailVO productDetailVO = new ProductDetailVO();

        /*
        * 多线程异步获取数据
        * */
        CompletableFuture<ProductDetailVO> detailFuture = CompletableFuture.supplyAsync(() -> {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if (ObjectUtils.isEmpty(spu)) {
                throw new ServiceException(HttpStatus.SC_NO_CONTENT, "商品不存在");
            }
            BeanUtils.copyProperties(spu, productDetailVO);
            return productDetailVO;
        }, executor);

        CompletableFuture<Void> detailSkuFuture = detailFuture.thenAcceptAsync((res) -> {
            /* 避免空指针异常 */
            if (!ObjectUtils.isEmpty(res)) {
                List<Sku> skuList = skuMapper.selectListBySpuId(res.getId());
                if (CollectionUtils.isEmpty(skuList)) {
                    throw new ServiceException(HttpStatus.SC_NO_CONTENT, "商品规格不存在");
                }
                List<SkuVO> skuVOList = skuList.stream()
                        .filter(sku -> DeleteEnum.NOT_DELETE.getCode().equals(sku.getIsDelete()))
                        .map(sku -> {
                            SkuVO skuVO = new SkuVO();
                            BeanUtils.copyProperties(sku, skuVO);
                            return skuVO;
                        }).collect(Collectors.toList());
                productDetailVO.setSkuVOList(skuVOList);
            }
        }, executor);

        CompletableFuture<Void> detailInfoFuture = detailFuture.thenAcceptAsync((res) -> {
            if (!ObjectUtils.isEmpty(res)) {
                SpuInfo spuInfo = spuInfoMapper.selectBySpuId(res.getId());
                if (ObjectUtils.isEmpty(spuInfo)
                        || DeleteEnum.DELETE.getCode().equals(spuInfo.getIsDelete())) {
                    throw new ServiceException(HttpStatus.SC_NO_CONTENT, "商品信息不存在");
                }
                ProductInfoVO productInfoVO = new ProductInfoVO();
                BeanUtils.copyProperties(spuInfo, productInfoVO);
                productDetailVO.setProductInfoVO(productInfoVO);
            }
        }, executor);

        CompletableFuture.allOf(detailSkuFuture, detailInfoFuture).get();

        if (ObjectUtils.isEmpty(productDetailVO))
            throw new ServiceException(HttpStatus.SC_NO_CONTENT, "商品详情不存在");

        return productDetailVO;
    }

    @Override
    public List<SkuApiVO> listSkuApiVOs(Set<Integer> skuIdSet) {
        if (CollectionUtils.isEmpty(skuIdSet))
            throw new ServiceException(HttpStatus.SC_NO_CONTENT, "暂无数据");

        List<Sku> skuList = skuMapper.selectListByIdSet(skuIdSet);
        List<SkuApiVO> skuApiVOList = skuList.stream()
                .filter(sku -> DeleteEnum.NOT_DELETE.getCode().equals(sku.getIsDelete()))
                .filter(sku -> ProductEnum.SALEABLE.getCode().equals(sku.getIsSaleable()))
                .map(sku -> {
                    SkuApiVO skuApiVO = new SkuApiVO();
                    BeanUtils.copyProperties(sku, skuApiVO);
                    return skuApiVO;
                }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(skuApiVOList))
            throw new ServiceException(HttpStatus.SC_NO_CONTENT, "暂无数据");
        return skuApiVOList;
    }

    @Override
    public List<ProductApiVO> listProductApiVOs(Set<Integer> spuIdSet) {
        if (CollectionUtils.isEmpty(spuIdSet))
            throw new ServiceException(HttpStatus.SC_NO_CONTENT, "暂无数据");

        List<Spu> spuList = spuMapper.selectListByIdSet(spuIdSet);
        List<ProductApiVO> productApiVOList = spuList.stream()
                .filter(spu -> DeleteEnum.NOT_DELETE.getCode().equals(spu.getIsDelete()))
                .filter(spu -> ProductEnum.SALEABLE.getCode().equals(spu.getIsSaleable()))
                .map(spu -> {
                    ProductApiVO productApiVO = new ProductApiVO();
                    BeanUtils.copyProperties(spu, productApiVO);
                    return productApiVO;
                }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(productApiVOList))
            throw new ServiceException(HttpStatus.SC_NO_CONTENT, "暂无数据");
        return productApiVOList;
    }

    @Override
    public SkuApiVO getSkuApiVO(Integer skuId) {
        Sku sku = skuMapper.selectByPrimaryKey(skuId);

//        if (ObjectUtils.isEmpty(sku)
//                || DeleteEnum.DELETE.getCode().equals(sku.getIsDelete())
//                || ProductEnum.NOT_SALEABLE.getCode().equals(sku.getIsSaleable()))
//            throw new ServiceException(HttpStatus.SC_NO_CONTENT, "暂无数据");

        SkuApiVO skuApiVO = new SkuApiVO();
        BeanUtils.copyProperties(sku, skuApiVO);
        return skuApiVO;
    }

}
