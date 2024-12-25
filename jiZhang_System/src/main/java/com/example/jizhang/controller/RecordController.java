package com.example.jizhang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.jizhang.common.Result;
import com.example.jizhang.entity.Record;
import com.example.jizhang.service.RecordService;
import com.example.jizhang.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@CrossOrigin
public class RecordController {
    @Autowired
    private RecordService recordService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<?> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            Page<Record> pageParam = new Page<>(page, size);
            IPage<Record> recordPage = recordService.selectPageWithCondition(pageParam, type, categoryId, startDate,
                    endDate);
            return Result.success(recordPage);
        } catch (Exception e) {
            return Result.error("获取记录列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result<?> getStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        if (month == null) {
            month = LocalDateTime.now().getMonthValue();
        }

        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);

        Map<String, Object> statistics = recordService.getMonthlyStatistics(startDate, endDate);
        statistics.put("categoryStats", recordService.getCategoryStatistics(startDate, endDate));

        return Result.success(statistics);
    }

    @PostMapping
    public Result<?> create(@RequestBody Record record) {
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("金额必须大于0");
        }
        if (record.getCategoryId() == null) {
            return Result.error("必须选择分类");
        }

        recordService.save(record);
        return Result.success(record);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Record record) {
        Record existingRecord = recordService.getById(id);
        if (existingRecord == null) {
            return Result.error("记录不存在");
        }

        if (record.getAmount() != null && record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("金额必须大于0");
        }

        record.setId(id);
        recordService.updateById(record);
        return Result.success(record);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Record record = recordService.getById(id);
        if (record == null) {
            return Result.error("记录不存在");
        }

        recordService.removeById(id);
        return Result.success(null);
    }
}