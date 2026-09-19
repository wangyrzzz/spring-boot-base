package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
@Schema(description = "系统公告")
public class SysNotice extends BaseEntity {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "公告类型")
    private Integer type;
    @Schema(description = "发布时间")
    private Date releaseTime;
    @Schema(description = "公告内容")
    private String content;
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
