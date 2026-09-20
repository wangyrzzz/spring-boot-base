package com.example.demo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.SysDept;
import com.example.demo.entity.SysDocument;
import com.example.demo.entity.SysExportTask;
import com.example.demo.entity.SysLogApi;
import com.example.demo.entity.SysMenu;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysScopeData;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SySqlLayerCoverageTest {
    private static final Pattern CREATE_TABLE = Pattern.compile("CREATE TABLE `([^`]+)`", Pattern.MULTILINE);
    private static final List<Layer> LAYERS = List.of(
            layer("sys_user", "com.example.demo.entity.SysUser", "com.example.demo.mapper.SysUserMapper", "com.example.demo.sesrvice.impl.UserServiceImpl"),
            layer("sys_role", "com.example.demo.entity.SysRole", "com.example.demo.mapper.SysRoleMapper", "com.example.demo.system.RoleService"),
            layer("sys_user_role", "com.example.demo.entity.SysUserRole", "com.example.demo.mapper.SysUserRoleMapper", "com.example.demo.system.UserRoleService"),
            layer("sys_menu", "com.example.demo.entity.SysMenu", "com.example.demo.mapper.SysMenuMapper", "com.example.demo.system.MenuService"),
            layer("sys_role_menu", "com.example.demo.entity.SysRoleMenu", "com.example.demo.mapper.SysRoleMenuMapper", "com.example.demo.system.RoleMenuService"),
            layer("sys_dept", "com.example.demo.entity.SysDept", "com.example.demo.mapper.SysDeptMapper", "com.example.demo.system.DeptService"),
            layer("sys_user_dept", "com.example.demo.entity.SysUserDept", "com.example.demo.mapper.SysUserDeptMapper", "com.example.demo.system.UserDeptService"),
            layer("sys_scope_data", "com.example.demo.entity.SysScopeData", "com.example.demo.mapper.SysScopeDataMapper", "com.example.demo.system.ScopeDataService"),
            layer("sys_role_scope", "com.example.demo.entity.SysRoleScope", "com.example.demo.mapper.SysRoleScopeMapper", "com.example.demo.system.RoleScopeService"),
            layer("sys_dict", "com.example.demo.entity.SysDict", "com.example.demo.mapper.SysDictMapper", "com.example.demo.system.DictService"),
            layer("sys_param", "com.example.demo.entity.SysParam", "com.example.demo.mapper.SysParamMapper", "com.example.demo.system.ParamService"),
            layer("sys_export_task", "com.example.demo.entity.SysExportTask", "com.example.demo.mapper.SysExportTaskMapper", "com.example.demo.system.ExportTaskService"),
            layer("sys_oss", "com.example.demo.entity.SysOss", "com.example.demo.mapper.SysOssMapper", "com.example.demo.storage.OssService"),
            layer("sys_client", "com.example.demo.entity.SysClient", "com.example.demo.mapper.SysClientMapper", "com.example.demo.common.ClientCredentialService"),
            layer("sys_dict_biz", "com.example.demo.entity.SysDictBiz", "com.example.demo.mapper.SysDictBizMapper", "com.example.demo.system.DictBizService"),
            layer("sys_attach", "com.example.demo.entity.SysAttach", "com.example.demo.mapper.SysAttachMapper", "com.example.demo.system.AttachService"),
            layer("sys_document", "com.example.demo.entity.SysDocument", "com.example.demo.mapper.SysDocumentMapper", "com.example.demo.system.DocumentService"),
            layer("sys_operation_log", "com.example.demo.entity.SysOperationLog", "com.example.demo.mapper.SysOperationLogMapper", "com.example.demo.system.OperationLogService"),
            layer("sys_biz_param", "com.example.demo.entity.SysBizParam", "com.example.demo.mapper.SysBizParamMapper", "com.example.demo.system.BizParamService"),
            layer("sys_log_api", "com.example.demo.entity.SysLogApi", "com.example.demo.mapper.SysLogApiMapper", "com.example.demo.system.LogApiService"),
            layer("sys_post", "com.example.demo.entity.SysPost", "com.example.demo.mapper.SysPostMapper", "com.example.demo.system.PostService"),
            layer("sys_notice", "com.example.demo.entity.SysNotice", "com.example.demo.mapper.SysNoticeMapper", "com.example.demo.system.NoticeService"),
            layer("sys_region", "com.example.demo.entity.SysRegion", "com.example.demo.mapper.SysRegionMapper", "com.example.demo.system.RegionService"),
            layer("mq_send_message", "com.example.demo.mq.model.MqSendMessage", "com.example.demo.mapper.MqSendMessageMapper", "com.example.demo.mq.core.MqSendMessageService"),
            layer("mq_consume_failure", "com.example.demo.mq.model.MqConsumeFailure", "com.example.demo.mapper.MqConsumeFailureMapper", "com.example.demo.mq.core.MqConsumeFailureService")
    );

    @Test
    void everySySqlTableHasEntityMapperAndService() throws Exception {
        Path sqlPath = Files.exists(Path.of("sy.sql")) ? Path.of("sy.sql") : Path.of("..", "sy.sql");
        String sql = Files.readString(sqlPath);
        Matcher matcher = CREATE_TABLE.matcher(sql);
        List<String> tables = matcher.results().map(item -> item.group(1)).toList();
        assertEquals(25, tables.size());
        assertEquals(LAYERS.stream().map(Layer::table).sorted().toList(), tables.stream().sorted().toList());

        for (Layer layer : LAYERS) {
            Class<?> entity = Class.forName(layer.entity());
            assertEquals(layer.table(), entity.getAnnotation(TableName.class).value());
            Class<?> mapper = Class.forName(layer.mapper());
            assertTrue(mapper.isInterface());
            assertTrue(BaseMapper.class.isAssignableFrom(mapper));
            assertTrue(Class.forName(layer.service()).isAnnotationPresent(Service.class));
        }
    }

    @Test
    void newEntitiesKeepDdlTypesLogicDeleteAndAuditMapping() throws Exception {
        assertEquals(Integer.class, SysDocument.class.getDeclaredField("type").getType());
        assertEquals(String.class, SysLogApi.class.getDeclaredField("serviceName").getType());

        for (Class<?> entity : List.of(SysRole.class, SysMenu.class, SysDept.class,
                SysScopeData.class, SysExportTask.class)) {
            Field deleted = entity.getDeclaredField("deleted");
            TableLogic tableLogic = deleted.getAnnotation(TableLogic.class);
            assertNotNull(tableLogic);
            assertEquals("0", tableLogic.value());
            assertEquals("1", tableLogic.delval());
        }

        for (String fieldName : List.of("id", "createTime", "updateTime", "createBy", "createDept", "updateBy")) {
            assertNotNull(BaseEntity.class.getDeclaredField(fieldName));
        }
    }

    private static Layer layer(String table, String entity, String mapper, String service) {
        return new Layer(table, entity, mapper, service);
    }

    private record Layer(String table, String entity, String mapper, String service) {
    }
}
