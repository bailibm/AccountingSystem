package com.example.jizhang.controller;

import com.example.jizhang.common.Result;
import com.example.jizhang.entity.Category;
import com.example.jizhang.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<?> getCategories(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean enabled) {
        try {
            log.info("Getting categories with type: {}, enabled: {}", type, enabled);
            List<Category> categories;
            if (type != null && !type.isEmpty()) {
                categories = categoryService.selectByType(type, enabled);
            } else {
                categories = categoryService.list();
            }
            log.info("Found {} categories", categories.size());
            return Result.success(categories);
        } catch (Exception e) {
            log.error("Failed to get categories", e);
            return Result.error("获取分类列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<?> create(@RequestBody Category category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setIsEnabled(true);
        category.setIsSystem(false);
        categoryService.save(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        category.setUpdatedAt(LocalDateTime.now());
        categoryService.updateById(category);
        return Result.success(category);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Boolean isEnabled) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        if (category.getIsSystem()) {
            return Result.error("系统预设分类不能修改状态");
        }
        if (categoryService.updateStatus(id, isEnabled)) {
            return Result.success(null);
        }
        return Result.error("更新状态失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        if (category.getIsSystem()) {
            return Result.error("系统预设分类不能删除");
        }
        categoryService.removeById(id);
        return Result.success(null);
    }
}