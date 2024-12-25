package com.example.jizhang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jizhang.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     * 根据类型查询分类列表
     */
    List<Category> selectByType(String type, Boolean enabled);

    /**
     * 检查分类名称是否存在
     */
    boolean checkNameExists(String name, String type, Long id);

    /**
     * 更新分类状态
     */
    boolean updateStatus(Long id, Boolean isEnabled);

    /**
     * 获取启用的分类列表
     */
    List<Category> getEnabledCategories(String type);
}