package com.zhihao.newretail.product.service.impl;

import com.zhihao.newretail.api.product.dto.CouponsAddApiDTO;
import com.zhihao.newretail.api.product.dto.CouponsUpdateApiDTO;
import com.zhihao.newretail.api.product.vo.CouponsApiVO;
import com.zhihao.newretail.core.util.PageUtil;
import com.zhihao.newretail.product.dao.CouponsMapper;
import com.zhihao.newretail.product.pojo.Coupons;
import com.zhihao.newretail.product.service.CouponsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CouponsServiceImpl implements CouponsService {

    @Autowired
    private CouponsMapper couponsMapper;

    @Override
    public CouponsApiVO getCouponsApiVO(Integer couponsId) {
        Coupons coupons = couponsMapper.selectByPrimaryKey(couponsId);
        return coupons2CouponsApiVO(coupons);
    }

    @Override
    public PageUtil<CouponsApiVO> listCouponsApiVOS(Integer saleable, Integer pageNum, Integer pageSize) {
        PageUtil<CouponsApiVO> pageUtil = new PageUtil<>();
        int count = couponsMapper.countBySaleable(saleable);
        pageUtil.setPageNum(pageNum);
        pageUtil.setPageSize(pageSize);
        pageUtil.setTotal((long) count);
        List<CouponsApiVO> couponsApiVOList = couponsMapper.selectListBySaleable(saleable, pageNum, pageSize).stream()
                .map(this::coupons2CouponsApiVO).collect(Collectors.toList());
        pageUtil.setList(couponsApiVOList);
        return pageUtil;
    }

    @Override
    public List<CouponsApiVO> listCouponsApiVOS(Set<Integer> couponsIdSet) {
        List<Coupons> couponsList = couponsMapper.selectListByCouponsIdSet(couponsIdSet);
        return couponsList.stream().map(this::coupons2CouponsApiVO).collect(Collectors.toList());
    }

    @Override
    public int insertCoupons(CouponsAddApiDTO couponsAddApiDTO) {
        Coupons coupons = new Coupons();
        BeanUtils.copyProperties(couponsAddApiDTO, coupons);
        return couponsMapper.insertSelective(coupons);
    }

    @Override
    public int updateCoupons(Integer couponsId, CouponsUpdateApiDTO couponsUpdateApiDTO) {
        Coupons coupons = new Coupons();
        BeanUtils.copyProperties(couponsUpdateApiDTO, coupons);
        coupons.setId(couponsId);
        return couponsMapper.updateByPrimaryKeySelective(coupons);
    }

    @Override
    public int deleteCoupons(Integer couponsId) {
        return couponsMapper.deleteByPrimaryKey(couponsId);
    }

    private CouponsApiVO coupons2CouponsApiVO(Coupons coupons) {
        CouponsApiVO couponsApiVO = new CouponsApiVO();
        BeanUtils.copyProperties(coupons, couponsApiVO);
        return couponsApiVO;
    }

}
