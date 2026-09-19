package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基本实体类
 * @Author: WangYuanrong
 * @Date: 2022/4/15 16:17
 */
@Data
@Schema(description = "通用审计实体")
public abstract class BaseEntity implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id,单表时数据库自增，分布式时重写使用雪花id
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    protected Long id;

    /**
     * 创建日期
     */
    @Schema(description = "创建时间")
    protected Date createTime;

    /**
     * 更新日期
     */
    @Schema(description = "更新时间")
    protected Date updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人用户 ID")
    protected Long createBy;

    /**
     * 创建部门
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建部门 ID")
    protected Long createDept;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新人用户 ID")
    protected Long updateBy;


}
