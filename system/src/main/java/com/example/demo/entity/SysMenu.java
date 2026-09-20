package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单权限")
public class SysMenu extends BaseEntity {
    @Schema(description = "父菜单 ID")
    private Long parentId;
    @Schema(description = "类型：1菜单，2按钮")
    private Integer menuType;
    @Schema(description = "菜单权限名称")
    private String name;
    @Schema(description = "菜单权限编码")
    private String code;
    @Schema(description = "祖先编码路径")
    private String ancestors;
    @Schema(description = "前端菜单路径")
    private String path;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "备注")
    private String remark;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
