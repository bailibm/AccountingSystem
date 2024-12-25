package com.example.jizhang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.jizhang.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
    /**
     * 分页查询记录
     */
    IPage<Record> selectPageWithCondition(IPage<Record> page,
            @Param("type") String type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 获取月度统计数据
     */
    Map<String, Object> getMonthlyStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 获取分类统计数据
     */
    List<Map<String, Object>> getCategoryStatistics(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 获取日期范围内的记录
     */
    List<Record> getRecordsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") String type);
}