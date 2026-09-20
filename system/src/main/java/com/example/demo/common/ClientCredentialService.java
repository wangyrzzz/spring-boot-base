package com.example.demo.common;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/** Client policy lookup and BCrypt credential management. */
@Service
@RequiredArgsConstructor
public class ClientCredentialService {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public ClientPolicy requireActive(String clientCode) {
        if (!StringUtils.hasText(clientCode)) {
            throw new ApiException(401, "clientCode不能为空");
        }
        try {
            ClientPolicy policy = jdbcTemplate.queryForObject(
                    "select client_code, client_secret, authorized_grant_types, access_token_validity, "
                            + "refresh_token_validity, status, deleted from sys_client where client_code = ? limit 1",
                    (rs, rowNum) -> new ClientPolicy(rs.getString("client_code"), rs.getString("client_secret"),
                            rs.getString("authorized_grant_types"), rs.getLong("access_token_validity"),
                            rs.getLong("refresh_token_validity"), rs.getInt("status"), rs.getInt("deleted")), clientCode);
            if (policy == null || !policy.active()) {
                throw new ApiException(401, "客户端不存在或已禁用");
            }
            return policy;
        } catch (EmptyResultDataAccessException ex) {
            throw new ApiException(401, "客户端不存在或已禁用");
        }
    }

    public ClientPolicy requireGrant(String clientCode, String grantType) {
        ClientPolicy policy = requireActive(clientCode);
        if (!policy.allows(grantType)) {
            throw new ApiException(401, "客户端不支持当前授权类型");
        }
        return policy;
    }

    public void verifySecret(ClientPolicy policy, String rawSecret) {
        if (!StringUtils.hasText(rawSecret) || !StringUtils.hasText(policy.clientSecret())
                || !passwordEncoder.matches(rawSecret, policy.clientSecret())) {
            throw new ApiException(401, "客户端密钥错误");
        }
    }

    public List<Map<String, Object>> list() {
        return jdbcTemplate.queryForList("select id,client_code as clientCode,resource_ids,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,auto_approve as autoApprove,status,deleted,create_time,update_time from sys_client where deleted=0 order by id desc");
    }

    public Map<String, Object> detail(Long id) {
        return jdbcTemplate.queryForMap("select id,client_code as clientCode,resource_ids,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,auto_approve as autoApprove,status,deleted,create_time,update_time from sys_client where id=? and deleted=0", id);
    }

    @org.springframework.transaction.annotation.Transactional
    public long save(Map<String, Object> in) {
        Long id = in.get("id") == null ? null : Long.valueOf(String.valueOf(in.get("id")));
        String clientCode = in.get("clientCode") == null ? null : String.valueOf(in.get("clientCode"));
        String rawSecret = in.get("clientSecret") == null ? null : String.valueOf(in.get("clientSecret"));
        if (!StringUtils.hasText(clientCode)) throw new ApiException("clientCode不能为空");
        if (id == null) {
            if (!StringUtils.hasText(rawSecret)) throw new ApiException("新增客户端必须提供密钥");
            jdbcTemplate.update("insert into sys_client (client_code,client_secret,resource_ids,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,auto_approve,status,deleted,create_time,update_time) values (?,?,?,?,?,?,?,?,?,?,?,?,0,current_timestamp,current_timestamp)",
                    clientCode, passwordEncoder.encode(rawSecret), in.get("resourceIds"), in.getOrDefault("scope", "*"), in.getOrDefault("authorizedGrantTypes", "password,refresh_token"), in.get("webServerRedirectUri"), in.get("authorities"), in.getOrDefault("accessTokenValidity", 900), in.getOrDefault("refreshTokenValidity", 604800), in.get("additionalInformation"), in.get("autoApprove"), in.getOrDefault("status", 1));
            return jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        }
        if (StringUtils.hasText(rawSecret)) {
            jdbcTemplate.update("update sys_client set client_code=?,client_secret=?,resource_ids=?,scope=?,authorized_grant_types=?,web_server_redirect_uri=?,authorities=?,access_token_validity=?,refresh_token_validity=?,additional_information=?,auto_approve=?,status=?,update_time=current_timestamp where id=? and deleted=0",
                    clientCode, passwordEncoder.encode(rawSecret), in.get("resourceIds"), in.getOrDefault("scope", "*"), in.getOrDefault("authorizedGrantTypes", "password,refresh_token"), in.get("webServerRedirectUri"), in.get("authorities"), in.getOrDefault("accessTokenValidity", 900), in.getOrDefault("refreshTokenValidity", 604800), in.get("additionalInformation"), in.get("autoApprove"), in.getOrDefault("status", 1), id);
        } else {
            jdbcTemplate.update("update sys_client set client_code=?,resource_ids=?,scope=?,authorized_grant_types=?,web_server_redirect_uri=?,authorities=?,access_token_validity=?,refresh_token_validity=?,additional_information=?,auto_approve=?,status=?,update_time=current_timestamp where id=? and deleted=0",
                    clientCode, in.get("resourceIds"), in.getOrDefault("scope", "*"), in.getOrDefault("authorizedGrantTypes", "password,refresh_token"), in.get("webServerRedirectUri"), in.get("authorities"), in.getOrDefault("accessTokenValidity", 900), in.getOrDefault("refreshTokenValidity", 604800), in.get("additionalInformation"), in.get("autoApprove"), in.getOrDefault("status", 1), id);
        }
        return id;
    }

    public void remove(Long id) {
        jdbcTemplate.update("update sys_client set deleted=1,status=0,update_time=current_timestamp where id=?", id);
    }
}
