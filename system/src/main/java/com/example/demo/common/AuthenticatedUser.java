package com.example.demo.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AuthenticatedUser implements Serializable {
    private String clientId;
    private Long userId;
    private String account;
    private String userName;
    private String nickName;
    private Long tenantId;
    private Long deptId;
    private String fullDeptId;
    private String postId;
    private String roleName;
    @Builder.Default
    private List<Long> roleIds = Collections.emptyList();
    @Builder.Default
    private Map<String, Object> detail = Collections.emptyMap();
}
