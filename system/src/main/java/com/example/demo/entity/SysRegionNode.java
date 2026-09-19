package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "行政区域树节点")
public class SysRegionNode {
    @Schema(description = "节点 ID")
    private String id;
    @Schema(description = "父节点 ID")
    private String parentId;
    @Schema(description = "节点标题")
    private String title;
    @Schema(description = "节点值")
    private String value;
    @Schema(description = "节点键")
    private String key;
    @Schema(description = "是否存在子节点")
    private Boolean hasChildren;
}
