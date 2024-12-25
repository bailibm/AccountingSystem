package com.example.jizhang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("records")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private BigDecimal amount;
    private Long categoryId;
    private LocalDateTime date;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String categoryType;
}