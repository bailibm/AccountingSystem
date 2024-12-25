package com.example.jizhang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jizhang.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 根据类型查询分类列表
     */
    List<Category> selectByType(@Param("type") String type, @Param("enabled") Boolean enabled);

    /**
     * 检查分类名称是否存在
     */
    Integer checkNameExists(@Param("name") String name, @Param("type") String type, @Param("id") Long id);

    /**
     * 更新分类状态
     */
    int updateStatus(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);

    /**
     * 获取启用的分类列表
     */
    List<Category> getEnabledCategories(@Param("type") String type);
}