package com.zhihao.newretail.product.service.impl;

import com.zhihao.newretail.api.product.dto.SpecParamAddApiDTO;
import com.zhihao.newretail.api.product.dto.SpecParamUpdateApiDTO;
import com.zhihao.newretail.api.product.vo.SpecParamApiVO;
import com.zhihao.newretail.product.dao.SpecParamKeyMapper;
import com.zhihao.newretail.product.dao.SpecParamValueMapper;
import com.zhihao.newretail.product.pojo.SpecParamKey;
import com.zhihao.newretail.product.service.SpecParamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecParamServiceImpl implements SpecParamService {

    @Autowired
    private SpecParamKeyMapper specParamKeyMapper;

    @Autowired
    private SpecParamValueMapper specParamValueMapper;

    @Override
    public List<SpecParamApiVO> listSpecParamApiVOS(Integer categoryId) {
        List<SpecParamKey> specParamKeyList = specParamKeyMapper.selectListByCategoryId(categoryId);
        return specParamKeyList.stream().map(this::specParamKey2SpecParamApiVO).collect(Collectors.toList());
    }

    @Override
    public int insertSpecParamKey(SpecParamAddApiDTO specParamAddApiDTO) {
        SpecParamKey specParamKey = new SpecParamKey();
        BeanUtils.copyProperties(specParamAddApiDTO, specParamKey);
        return specParamKeyMapper.insertSelective(specParamKey);
    }

    @Override
    public int updateSpecParamKey(Integer specParamId, SpecParamUpdateApiDTO specParamUpdateApiDTO) {
        SpecParamKey specParamKey = new SpecParamKey();
        BeanUtils.copyProperties(specParamUpdateApiDTO, specParamKey);
        specParamKey.setId(specParamId);
        return specParamKeyMapper.updateByPrimaryKeySelective(specParamKey);
    }

    @Override
    public int deleteSpecParamKey(Integer specParamId) {
        return specParamKeyMapper.deleteByPrimaryKey(specParamId);
    }

    private SpecParamApiVO specParamKey2SpecParamApiVO(SpecParamKey specParamKey) {
        SpecParamApiVO specParamApiVO = new SpecParamApiVO();
        BeanUtils.copyProperties(specParamKey, specParamApiVO);
        return specParamApiVO;
    }

}
