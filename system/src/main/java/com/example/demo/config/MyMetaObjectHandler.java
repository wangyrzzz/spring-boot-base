package com.example.demo.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.demo.common.AuthUserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        var authenticatedUser = AuthUserContext.get();
        Long userId = authenticatedUser == null ? null : authenticatedUser.getUserId();
        Long deptId = authenticatedUser == null ? null : authenticatedUser.getDeptId();
        strictInsertFill(metaObject, "createBy", Long.class, userId);
        strictInsertFill(metaObject, "createDept", Long.class, deptId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        var authenticatedUser = AuthUserContext.get();
        Long userId = authenticatedUser == null ? null : authenticatedUser.getUserId();
        strictUpdateFill(metaObject, "updateBy", Long.class, userId);
    }
}
