package com.example.jizhang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jizhang.entity.Record;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RecordService extends IService<Record> {
    /**
     * 分页查询记录
     */
    IPage<Record> selectPageWithCondition(IPage<Record> page, String type,
            Long categoryId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取月度统计数据
     */
    Map<String, Object> getMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取分类统计数据
     */
    List<Map<String, Object>> getCategoryStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取日期范围内的记录
     */
    List<Record> getRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate, String type);
}