package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_dept")
@Schema(description = "用户部门关联")
public class SysUserDept extends BaseEntity {
    @Schema(description = "用户 ID")
    private Long userId;
    @Schema(description = "部门 ID")
    private Long deptId;
}
