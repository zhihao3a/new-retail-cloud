package com.zhihao.newretail.product.service.impl;

import com.zhihao.newretail.api.product.dto.SkuStockLockApiDTO;
import com.zhihao.newretail.api.product.vo.SkuStockApiVO;
import com.zhihao.newretail.core.exception.ServiceException;
import com.zhihao.newretail.product.dao.SkuStockLockMapper;
import com.zhihao.newretail.product.dao.SkuStockMapper;
import com.zhihao.newretail.product.enums.SkuStockLockEnum;
import com.zhihao.newretail.product.enums.SkuStockTypeEnum;
import com.zhihao.newretail.product.pojo.SkuStock;
import com.zhihao.newretail.product.pojo.SkuStockLock;
import com.zhihao.newretail.product.service.StockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private SkuStockMapper skuStockMapper;

    @Autowired
    private SkuStockLockMapper skuStockLockMapper;

    @Override
    public List<SkuStockApiVO> listSkuStockApiVOS(Set<Integer> skuIdSet) {
        List<SkuStock> skuStockList = skuStockMapper.selectListBySkuIdSet(skuIdSet);
        return skuStockList.stream().map(this::skuStock2SkuStockApiVO).collect(Collectors.toList());
    }

    @Override
    public void stockLock(SkuStockLockApiDTO skuStockLockApiDTO) {
        SkuStockLock skuStockLock = new SkuStockLock();
        BeanUtils.copyProperties(skuStockLockApiDTO, skuStockLock);
        skuStockLock.setStatus(SkuStockLockEnum.LOCK.getCode());
        int insertSkuStockLockRow = skuStockLockMapper.insertSelective(skuStockLock);
        if (insertSkuStockLockRow <= 0) {
            throw new ServiceException("库存锁定失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int stockBatchLock(List<SkuStockLockApiDTO> skuStockLockApiDTOList) {
        /* 获取商品规格id集合 */
        Set<Integer> skuIdSet = skuStockLockApiDTOList.stream().map(SkuStockLockApiDTO::getSkuId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(skuIdSet)) {
            Map<Integer, SkuStockLockApiDTO> skuStockLockApiDTOMap = skuStockLockApiDTOList2Map(skuStockLockApiDTOList);
            List<SkuStock> skuStockList = skuStockMapper.selectListBySkuIdSet(skuIdSet);    // id集合获取库存信息列表
            List<SkuStock> skuStocks = new ArrayList<>();
            for (SkuStock skuStock : skuStockList) {
                SkuStockLockApiDTO skuStockLockApiDTO = skuStockLockApiDTOMap.get(skuStock.getSkuId());
                handleSkuStock(skuStock, skuStockLockApiDTO);
                skuStocks.add(skuStock);
            }
            int updateBatchRow = skuStockMapper.updateBatch(skuStocks);
            if (updateBatchRow >= 1) {
                skuStockBatchLock(skuStockLockApiDTOList);
                return updateBatchRow;
            }
            throw new ServiceException("商品库存锁定失败");
        }
        throw new ServiceException("库存锁定信息为空");
    }

    @Override
    public List<SkuStockLock> listSkuStockLockS(Long orderId) {
        return skuStockLockMapper.selectListByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStockByType(Long orderId, List<SkuStockLock> skuStockLockList, SkuStockTypeEnum skuStockTypeEnum) {
        Set<Integer> skuIdSet = skuStockLockListGetSkuIdSet(skuStockLockList);
        Map<Integer, SkuStockLock> skuStockLockMap = skuStockLockList2Map(skuStockLockList);
        List<SkuStock> skuStockList = skuStockMapper.selectListBySkuIdSet(skuIdSet);
        List<SkuStock> skuStocks = new ArrayList<>();
        if (SkuStockTypeEnum.UN_LOCK.getCode().equals(skuStockTypeEnum.getCode())) {
            /* 解锁库存 */
            for (SkuStock skuStock : skuStockList) {
                SkuStockLock skuStockLock = skuStockLockMap.get(skuStock.getSkuId());
                skuStock.setLockStock(skuStock.getLockStock() - skuStockLock.getCount());   // 去除锁定库存数量
                skuStock.setStock(skuStock.getStock() + skuStockLock.getCount());           // 删减的库存重新添加回去
                skuStocks.add(skuStock);
            }
        } else {
            /* 删减库存 */
            for (SkuStock skuStock : skuStockList) {
                SkuStockLock skuStockLock = skuStockLockMap.get(skuStock.getSkuId());
                skuStock.setActualStock(skuStock.getActualStock() - skuStockLock.getCount());   // 删减实际库存
                skuStock.setLockStock(skuStock.getLockStock() - skuStockLock.getCount());       // 去除锁定库存数量
                skuStocks.add(skuStock);
            }
        }
        int updateBatchRow = skuStockMapper.updateBatch(skuStocks);
        if (updateBatchRow >= 1) {
            int deleteSkuStockLockRow = skuStockLockMapper.deleteByOrderId(orderId);
            if (deleteSkuStockLockRow <= 0) {
                throw new ServiceException("解锁或删减库存失败");
            }
        } else {
            throw new ServiceException("解锁或删减库存失败");
        }
    }

    private SkuStockApiVO skuStock2SkuStockApiVO(SkuStock skuStock) {
        SkuStockApiVO skuStockApiVO = new SkuStockApiVO();
        BeanUtils.copyProperties(skuStock, skuStockApiVO);
        return skuStockApiVO;
    }

    private Map<Integer, SkuStockLockApiDTO> skuStockLockApiDTOList2Map(List<SkuStockLockApiDTO> skuStockLockApiDTOList) {
        return skuStockLockApiDTOList.stream()
                .collect(Collectors.toMap(SkuStockLockApiDTO::getSkuId, skuStockLockApiDTO -> skuStockLockApiDTO));
    }

    private Set<Integer> skuStockLockListGetSkuIdSet(List<SkuStockLock> skuStockLockList) {
        return skuStockLockList.stream().map(SkuStockLock::getSkuId).collect(Collectors.toSet());
    }

    private Map<Integer, SkuStockLock> skuStockLockList2Map(List<SkuStockLock> skuStockLockList) {
        return skuStockLockList.stream().collect(Collectors.toMap(SkuStockLock::getSkuId, skuStockLock -> skuStockLock));
    }

    private void handleSkuStock(SkuStock skuStock, SkuStockLockApiDTO skuStockLockApiDTO) {
        if (skuStock.getLockStock() == 0) {
            skuStock.setStock(skuStock.getActualStock() - skuStockLockApiDTO.getCount());
        } else {
            skuStock.setStock(skuStock.getActualStock() - (skuStock.getLockStock() + skuStockLockApiDTO.getCount()));
        }
        skuStock.setLockStock(skuStock.getLockStock() + skuStockLockApiDTO.getCount());
    }

    /*
    * 批量锁定库存
    * */
    private void skuStockBatchLock(List<SkuStockLockApiDTO> skuStockLockApiDTOList) {
        List<SkuStockLock> skuStockLockList = skuStockLockApiDTOList.stream()
                .map(skuStockLockApiDTO -> {
                    SkuStockLock skuStockLock = new SkuStockLock();
                    BeanUtils.copyProperties(skuStockLockApiDTO, skuStockLock);
                    skuStockLock.setStatus(SkuStockLockEnum.LOCK.getCode());
                    return skuStockLock;
                }).collect(Collectors.toList());
        int insertBatchRow = skuStockLockMapper.insertBatch(skuStockLockList);
        if (insertBatchRow <= 0) {
            throw new ServiceException("库存锁定失败");
        }
    }

}
