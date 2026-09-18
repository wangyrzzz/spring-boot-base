package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基本实体类
 * @Author: WangYuanrong
 * @Date: 2022/4/15 16:17
 */
@Data
public abstract class BaseEntity implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id,单表时数据库自增，分布式时重写使用雪花id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    /**
     * 创建日期
     */
    protected Date createTime;

    /**
     * 更新日期
     */
    protected Date updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    protected Long createBy;

    /**
     * 创建部门
     */
    @TableField(fill = FieldFill.INSERT)
    protected Long createDept;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    protected Long updateBy;


}
