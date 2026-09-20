package com.example.demo.common;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysClient;
import com.example.demo.enums.DeletedFlagEnum;
import com.example.demo.enums.EnableStatusEnum;
import com.example.demo.mapper.SysClientMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/** Client policy lookup and BCrypt credential management. */
@Service
@RequiredArgsConstructor
public class ClientCredentialService extends ServiceImpl<SysClientMapper, SysClient> {
    private static final int DEFAULT_ACCESS_TOKEN_VALIDITY = 900;
    private static final int DEFAULT_REFRESH_TOKEN_VALIDITY = 604800;

    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public ClientPolicy requireActive(String clientCode) {
        if (!StringUtils.hasText(clientCode)) {
            throw new ApiException(401, "clientCode不能为空");
        }
        SysClient client = lambdaQuery().eq(SysClient::getClientCode, clientCode).one();
        if (client == null || !isActive(client)) {
            throw new ApiException(401, "客户端不存在或已禁用");
        }
        return new ClientPolicy(client.getClientCode(), client.getClientSecret(), client.getAuthorizedGrantTypes(),
                valueOrDefault(client.getAccessTokenValidity(), DEFAULT_ACCESS_TOKEN_VALIDITY),
                valueOrDefault(client.getRefreshTokenValidity(), DEFAULT_REFRESH_TOKEN_VALIDITY),
                valueOrDefault(client.getStatus(), EnableStatusEnum.ENABLED.getCode()),
                valueOrDefault(client.getDeleted(), DeletedFlagEnum.NORMAL.getCode()));
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

    public List<SysClient> listClients() {
        return lambdaQuery().orderByDesc(SysClient::getId).list().stream().map(this::sanitize).toList();
    }

    public SysClient detail(Long id) {
        SysClient client = getById(id);
        return client == null ? null : sanitize(client);
    }

    @Transactional
    public long save(java.util.Map<String, Object> input) {
        SysClient client = objectMapper.convertValue(input, SysClient.class);
        if (!StringUtils.hasText(client.getClientCode())) {
            throw new ApiException(400, "clientCode不能为空");
        }
        String rawSecret = client.getClientSecret();
        if (client.getId() == null && !StringUtils.hasText(rawSecret)) {
            throw new ApiException(400, "新增客户端必须提供密钥");
        }
        SysClient old = client.getId() == null ? null : getById(client.getId());
        long duplicate = lambdaQuery().eq(SysClient::getClientCode, client.getClientCode())
                .ne(client.getId() != null, SysClient::getId, client.getId()).count();
        if (duplicate > 0) {
            throw new ApiException(400, "客户端编码已存在");
        }
        if (StringUtils.hasText(rawSecret)) {
            client.setClientSecret(passwordEncoder.encode(rawSecret));
        } else if (old != null) {
            client.setClientSecret(old.getClientSecret());
        }
        if (client.getScope() == null) {
            client.setScope("*");
        }
        if (client.getAuthorizedGrantTypes() == null) {
            client.setAuthorizedGrantTypes("password,refresh_token");
        }
        if (client.getAccessTokenValidity() == null) {
            client.setAccessTokenValidity(DEFAULT_ACCESS_TOKEN_VALIDITY);
        }
        if (client.getRefreshTokenValidity() == null) {
            client.setRefreshTokenValidity(DEFAULT_REFRESH_TOKEN_VALIDITY);
        }
        if (client.getStatus() == null) {
            client.setStatus(EnableStatusEnum.ENABLED.getCode());
        }
        saveOrUpdate(client);
        return client.getId();
    }

    @Transactional
    public void remove(Long id) {
        if (id == null) {
            return;
        }
        lambdaUpdate().eq(SysClient::getId, id).set(SysClient::getStatus, EnableStatusEnum.DISABLED.getCode()).update();
        removeById(id);
    }

    private boolean isActive(SysClient client) {
        return Integer.valueOf(EnableStatusEnum.ENABLED.getCode()).equals(client.getStatus())
                && Integer.valueOf(DeletedFlagEnum.NORMAL.getCode()).equals(client.getDeleted());
    }

    private SysClient sanitize(SysClient client) {
        client.setClientSecret(null);
        return client;
    }

    private int valueOrDefault(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }
}
