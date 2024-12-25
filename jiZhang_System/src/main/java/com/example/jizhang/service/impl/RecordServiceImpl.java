package com.example.jizhang.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jizhang.entity.Record;
import com.example.jizhang.mapper.RecordMapper;
import com.example.jizhang.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordServiceImpl.class);

    @Override
    public IPage<Record> selectPageWithCondition(IPage<Record> page, String type,
            Long categoryId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Querying records with type: {}, categoryId: {}, startDate: {}, endDate: {}",
                type, categoryId, startDate, endDate);
        IPage<Record> result = baseMapper.selectPageWithCondition(page, type, categoryId, startDate, endDate);
        log.info("Found {} records", result.getTotal());
        return result;
    }

    @Override
    public Map<String, Object> getMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return baseMapper.getMonthlyStatistics(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return baseMapper.getCategoryStatistics(startDate, endDate);
    }

    @Override
    public List<Record> getRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate, String type) {
        return baseMapper.getRecordsByDateRange(startDate, endDate, type);
    }

    @Override
    public boolean save(Record record) {
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        if (record.getDate() == null) {
            record.setDate(LocalDateTime.now());
        }
        return super.save(record);
    }

    @Override
    public boolean updateById(Record record) {
        record.setUpdatedAt(LocalDateTime.now());
        return super.updateById(record);
    }
}