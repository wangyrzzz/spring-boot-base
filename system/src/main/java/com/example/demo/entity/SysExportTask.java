package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_export_task")
@Schema(description = "异步导出任务")
public class SysExportTask extends BaseEntity {
    @Schema(description = "任务编号")
    private String taskNo;
    @Schema(description = "导出业务类型")
    private Integer exportBizType;
    @Schema(description = "导出名称")
    private String exportName;
    @Schema(description = "序列化查询参数")
    private String exportParams;
    @Schema(description = "状态：0待开始，1进行中，2已完成，3失败")
    private Integer status;
    @Schema(description = "总行数")
    private Long totalCount;
    @Schema(description = "已导出行数")
    private Long completeCount;
    @Schema(description = "生成的文件路径")
    private String filePath;
    @Schema(description = "下载显示文件名")
    private String fileName;
    @Schema(description = "失败原因")
    private String errorMsg;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
