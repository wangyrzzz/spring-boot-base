package com.example.demo.system;

import com.example.demo.common.ApiException;
import com.example.demo.common.AuthUserContext;
import com.example.demo.annotation.BizOperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private static final DateTimeFormatter DELETE_SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> detail(Long id) {
        return jdbcTemplate.queryForMap("select * from sys_document where id=? and is_deleted=0", id);
    }

    public List<Map<String, Object>> page(String type, String keyword) {
        String like = keyword == null ? "%%" : "%" + keyword + "%";
        if (type == null) {
            return jdbcTemplate.queryForList("select id,create_time,update_time,type,code,sort,language_code,title,subheading,description,icon,link,document_version,is_deleted from sys_document where is_deleted=0 and (code like ? or title like ?) order by sort asc,id asc", like, like);
        }
        return jdbcTemplate.queryForList("select id,create_time,update_time,type,code,sort,language_code,title,subheading,description,icon,link,document_version,is_deleted from sys_document where is_deleted=0 and type=? and (code like ? or title like ?) order by sort asc,id asc", type, like, like);
    }

    public List<Map<String, Object>> select(String type) {
        return page(type, null);
    }

    public Map<String, Object> latest(String type) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from sys_document where is_deleted=0 and type=? order by sort asc,id desc limit 1", type);
        return rows.isEmpty() ? null : rows.get(0);
    }

    @Transactional
    @BizOperationLog(bizType = "document", bizName = "文档", operationType = "保存", bizId = "#input['id']")
    public long save(Map<String, Object> input, boolean update) {
        requireAdmin();
        String code = string(input.get("code"));
        if (!StringUtils.hasText(code) || code.length() > 128) throw new ApiException("文档编码不能为空且不能超过128位");
        Long id = input.get("id") == null ? null : Long.valueOf(String.valueOf(input.get("id")));
        if (update || id != null) {
            Map<String, Object> current = detail(id);
            if (!code.equals(String.valueOf(current.get("code")))) throw new ApiException("文档编码不允许修改");
            jdbcTemplate.update("update sys_document set type=?,sort=?,language_code=?,title=?,subheading=?,description=?,icon=?,link=?,content=?,document_version=?,update_time=current_timestamp where id=? and is_deleted=0",
                    input.get("type"), input.getOrDefault("sort", 0), input.get("languageCode"), input.get("title"), input.get("subheading"), input.get("description"), input.get("icon"), input.get("link"), input.get("content"), input.get("documentVersion"), id);
            return id;
        }
        if (jdbcTemplate.queryForObject("select count(1) from sys_document where code=? and is_deleted=0", Integer.class, code) > 0) throw new ApiException("文档编码已存在");
        jdbcTemplate.update("insert into sys_document (type,code,sort,language_code,title,subheading,description,icon,link,content,document_version,is_deleted,create_time,update_time) values (?,?,?,?,?,?,?,?,?,?,?,0,current_timestamp,current_timestamp)",
                input.get("type"), code, input.getOrDefault("sort", 0), input.get("languageCode"), input.get("title"), input.get("subheading"), input.get("description"), input.get("icon"), input.get("link"), input.get("content"), input.get("documentVersion"));
        return jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
    }

    @Transactional
    @BizOperationLog(bizType = "document", bizName = "文档", operationType = "删除", bizId = "#ids")
    public void remove(List<Long> ids) {
        requireAdmin();
        if (ids == null || ids.isEmpty()) return;
        for (Long id : ids) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("select code from sys_document where id=? and is_deleted=0", id);
            if (rows.isEmpty()) continue;
            String oldCode = String.valueOf(rows.get(0).get("code"));
            String released = releasedCode(oldCode, id);
            while (jdbcTemplate.queryForObject("select count(1) from sys_document where code=?", Integer.class, released) > 0) {
                released = releasedCode(oldCode, id + System.nanoTime());
            }
            jdbcTemplate.update("update sys_document set code=?,is_deleted=1,update_time=current_timestamp where id=? and is_deleted=0", released, id);
        }
    }

    static String releasedCode(String code, long id) {
        String suffix = "_delete_" + LocalDateTime.now().format(DELETE_SUFFIX) + "_" + Math.abs(id % 100000);
        String base = code.substring(0, Math.min(code.length(), Math.max(0, 128 - suffix.length())));
        return (base + suffix).substring(0, Math.min(128, base.length() + suffix.length()));
    }

    private void requireAdmin() {
        if (AuthUserContext.get() == null || !StringUtils.hasText(AuthUserContext.get().getRoleName())
                || !(AuthUserContext.get().getRoleName().toLowerCase().contains("admin")
                || AuthUserContext.get().getRoleName().contains("管理员"))) {
            throw new ApiException(403, "仅管理员可操作文档");
        }
    }

    private String string(Object value) { return value == null ? null : String.valueOf(value); }
}
