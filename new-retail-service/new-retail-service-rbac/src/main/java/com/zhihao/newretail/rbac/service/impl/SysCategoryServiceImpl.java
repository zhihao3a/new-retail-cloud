package com.zhihao.newretail.rbac.service.impl;

import com.zhihao.newretail.api.product.dto.CategoryAddApiDTO;
import com.zhihao.newretail.api.product.dto.CategoryUpdateApiDTO;
import com.zhihao.newretail.api.product.feign.CategoryFeignService;
import com.zhihao.newretail.api.product.vo.CategoryApiVO;
import com.zhihao.newretail.rbac.annotation.RequiresPermission;
import com.zhihao.newretail.rbac.consts.AuthorizationConst;
import com.zhihao.newretail.rbac.service.SysCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysCategoryServiceImpl implements SysCategoryService {

    @Autowired
    private CategoryFeignService categoryFeignService;

    @Override
    @RequiresPermission(scope = AuthorizationConst.COMMON)
    public List<CategoryApiVO> listCategoryApiVOS() {
        return categoryFeignService.listCategoryApiVOS();
    }

    @Override
    @RequiresPermission(scope = AuthorizationConst.COMMON)
    public CategoryApiVO getCategoryApiVO(Integer categoryId) {
        return categoryFeignService.getCategoryApiVO(categoryId);
    }

    @Override
    @RequiresPermission(scope = AuthorizationConst.ADMIN)
    public Integer addCategory(CategoryAddApiDTO categoryAddApiDTO) {
        return categoryFeignService.addCategory(categoryAddApiDTO);
    }

    @Override
    @RequiresPermission(scope = AuthorizationConst.ADMIN)
    public Integer updateCategory(Integer categoryId, CategoryUpdateApiDTO categoryUpdateApiDTO) {
        return categoryFeignService.updateCategory(categoryId, categoryUpdateApiDTO);
    }

    @Override
    @RequiresPermission(scope = AuthorizationConst.ROOT)
    public Integer deleteCategory(Integer categoryId) {
        return categoryFeignService.deleteCategory(categoryId);
    }

}
