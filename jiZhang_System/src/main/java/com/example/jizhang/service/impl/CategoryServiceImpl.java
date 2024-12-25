package com.example.jizhang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jizhang.entity.Category;
import com.example.jizhang.mapper.CategoryMapper;
import com.example.jizhang.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.io.Serializable;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> selectByType(String type, Boolean enabled) {
        log.info("Selecting categories by type: {}, enabled: {}", type, enabled);
        List<Category> categories = baseMapper.selectByType(type, enabled);
        log.info("Found {} categories: {}", categories.size(), categories);
        return categories;
    }

    @Override
    public boolean checkNameExists(String name, String type, Long id) {
        Integer count = baseMapper.checkNameExists(name, type, id);
        return count != null && count > 0;
    }

    @Override
    public boolean updateStatus(Long id, Boolean isEnabled) {
        return baseMapper.updateStatus(id, isEnabled) > 0;
    }

    @Override
    public List<Category> getEnabledCategories(String type) {
        return baseMapper.getEnabledCategories(type);
    }

    @Override
    public boolean save(Category category) {
        if (checkNameExists(category.getName(), category.getType(), null)) {
            return false;
        }
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setIsEnabled(true);
        category.setIsSystem(false);
        return super.save(category);
    }

    @Override
    public boolean updateById(Category category) {
        if (checkNameExists(category.getName(), category.getType(), category.getId())) {
            return false;
        }
        Category existingCategory = getById(category.getId());
        if (existingCategory == null || existingCategory.getIsSystem()) {
            return false;
        }
        category.setUpdatedAt(LocalDateTime.now());
        return super.updateById(category);
    }

    @Override
    public boolean removeById(Serializable id) {
        Category category = getById(id);
        if (category == null || category.getIsSystem()) {
            return false;
        }
        return super.removeById(id);
    }
}