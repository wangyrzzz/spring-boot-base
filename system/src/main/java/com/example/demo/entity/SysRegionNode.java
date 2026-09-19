package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRegionNode {
    private String id;
    private String parentId;
    private String title;
    private String value;
    private String key;
    private Boolean hasChildren;
}
