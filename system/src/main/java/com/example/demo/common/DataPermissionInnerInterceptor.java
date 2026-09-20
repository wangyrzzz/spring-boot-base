package com.example.demo.common;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataPermissionInnerInterceptor implements InnerInterceptor {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([a-zA-Z][a-zA-Z0-9_]*)}");
    private static final String APPLIED_MARKER = "__dataScopeApplied";
    private final JdbcTemplate jdbcTemplate;
    private final DataScopeProperties properties;

    public DataPermissionInnerInterceptor(JdbcTemplate jdbcTemplate, DataScopeProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                            ResultHandler resultHandler, BoundSql boundSql) {
        if (!boundSql.hasAdditionalParameter(APPLIED_MARKER)) {
            apply(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        }
    }

    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                               ResultHandler resultHandler, BoundSql boundSql) {
        if (!boundSql.hasAdditionalParameter(APPLIED_MARKER)) {
            apply(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        }
        return true;
    }

    private void apply(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                       ResultHandler resultHandler, BoundSql boundSql) {
        if (!properties.isEnabled() || ms.getSqlCommandType() != SqlCommandType.SELECT
                || ms.getStatementType() == StatementType.CALLABLE) {
            return;
        }
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null) {
            return;
        }
        String originalSql = boundSql.getSql().trim();
        if (originalSql.endsWith(";")) {
            throw new IllegalArgumentException("数据权限 SQL 不允许包含分号");
        }
        String mapperId = ms.getId().endsWith("_mpCount")
                ? ms.getId().substring(0, ms.getId().length() - "_mpCount".length()) : ms.getId();
        RuleResolution resolution = findRule(mapperId, user);
        if (!resolution.hasRule()) {
            return;
        }
        if (resolution.denied()) {
            PluginUtils.mpBoundSql(boundSql).sql("select * from (" + originalSql + ") scope where 1 = 0");
            boundSql.setAdditionalParameter(APPLIED_MARKER, Boolean.TRUE);
            return;
        }
        DataScopeCondition condition = condition(resolution.rule(), user);
        if (condition == null) {
            boundSql.setAdditionalParameter(APPLIED_MARKER, Boolean.TRUE);
            return;
        }
        DataScopeRule rule = resolution.rule();
        String field = StringUtils.hasText(rule.scopeField()) ? rule.scopeField() : "*";
        if (!"*".equals(field)) {
            field = safeIdentifier(field);
        }
        PluginUtils.mpBoundSql(boundSql).sql("select " + field + " from (" + originalSql + ") scope " + condition.sql());
        for (int i = 0; i < condition.parameters().size(); i++) {
            ScopeParameter boundParameter = condition.parameters().get(i);
            String property = "__dataScope" + i;
            boundSql.setAdditionalParameter(property, boundParameter.value());
            boundSql.getParameterMappings().add(new ParameterMapping.Builder(
                    ms.getConfiguration(), property, boundParameter.type()).build());
        }
        boundSql.setAdditionalParameter(APPLIED_MARKER, Boolean.TRUE);
    }

    private RuleResolution findRule(String mapperId, AuthenticatedUser user) {
        List<DataScopeRule> allRules = jdbcTemplate.query(
                "select id, resource_code, scope_column, scope_field, scope_class, scope_type, scope_value "
                        + "from sys_scope_data where scope_class = ? and coalesce(status, 1) = 1 and coalesce(deleted, 0) = 0 "
                        + "order by id", this::mapRule, mapperId);
        if (!allRules.isEmpty()) {
            if (user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
                return RuleResolution.deniedResolution();
            }
            String holders = String.join(",", user.getRoleIds().stream().map(id -> "?").toList());
            List<Object> args = new ArrayList<>();
            args.add(mapperId);
            args.addAll(user.getRoleIds());
            List<DataScopeRule> assigned = jdbcTemplate.query(
                    "select sd.id, sd.resource_code, sd.scope_column, sd.scope_field, sd.scope_class, sd.scope_type, sd.scope_value "
                            + "from sys_scope_data sd inner join sys_role_scope rs on rs.scope_id = sd.id "
                            + "where sd.scope_class = ? and coalesce(sd.status, 1) = 1 and coalesce(sd.deleted, 0) = 0 "
                            + "and rs.role_id in (" + holders + ") order by rs.priority asc, sd.id asc limit 1",
                    this::mapRule, args.toArray());
            return assigned.isEmpty() ? RuleResolution.deniedResolution() : RuleResolution.ruleResolution(assigned.get(0));
        }
        DataAuth annotation = findAnnotation(mapperId);
        if (annotation == null) {
            return RuleResolution.none();
        }
        return RuleResolution.ruleResolution(new DataScopeRule(0L, annotation.code(), annotation.column(), annotation.field(),
                mapperId, annotation.type().getCode(), annotation.value()));
    }

    private DataAuth findAnnotation(String mapperId) {
        int lastDot = mapperId.lastIndexOf('.');
        if (lastDot < 0) {
            return null;
        }
        try {
            Class<?> mapper = Class.forName(mapperId.substring(0, lastDot));
            String methodName = mapperId.substring(lastDot + 1);
            for (Method method : mapper.getMethods()) {
                if (method.getName().equals(methodName)) {
                    DataAuth annotation = method.getAnnotation(DataAuth.class);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {
            // XML-only mappers do not expose annotation metadata.
        }
        return null;
    }

    private DataScopeCondition condition(DataScopeRule rule, AuthenticatedUser user) {
        DataScopeEnum scope = DataScopeEnum.of(rule.scopeType());
        if (scope == null || !StringUtils.hasText(rule.scopeColumn())) {
            throw new IllegalArgumentException("数据权限规则类型或字段无效");
        }
        if (scope == DataScopeEnum.ALL || isAdministrator(user)) {
            return null;
        }
        if (scope == DataScopeEnum.CUSTOM) {
            return customCondition(rule.scopeValue(), user);
        }
        List<Long> ids = new ArrayList<>();
        if (scope == DataScopeEnum.OWN) {
            if (user.getUserId() != null) {
                ids.add(user.getUserId());
            }
        } else if (scope == DataScopeEnum.OWN_DEPT) {
            if (user.getDeptId() != null) {
                ids.add(user.getDeptId());
            }
        } else if (scope == DataScopeEnum.OWN_DEPT_CHILD) {
            if (user.getDeptId() != null) {
                ids.add(user.getDeptId());
                ids.addAll(jdbcTemplate.queryForList(
                        "select id from sys_dept where concat(',', ancestors, ',') like concat('%,', ?, ',%') and deleted = 0",
                        Long.class, user.getDeptId()));
            }
        }
        if (ids.isEmpty()) {
            return new DataScopeCondition("where 1 = 0", List.of());
        }
        return new DataScopeCondition("where scope." + safeIdentifier(rule.scopeColumn()) + " in (" + ids.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("") + ")", List.of());
    }

    private DataScopeCondition customCondition(String raw, AuthenticatedUser user) {
        if (!StringUtils.hasText(raw) || raw.contains(";")
                || !raw.toLowerCase(Locale.ROOT).trim().startsWith("where ")) {
            throw new IllegalArgumentException("CUSTOM 数据权限必须以 where 开头");
        }
        Matcher matcher = PLACEHOLDER.matcher(raw);
        StringBuffer result = new StringBuffer();
        List<ScopeParameter> parameters = new ArrayList<>();
        while (matcher.find()) {
            Object value = switch (matcher.group(1)) {
                case "userId" -> user.getUserId();
                case "deptId" -> user.getDeptId();
                case "roleId" -> user.getRoleIds() == null || user.getRoleIds().isEmpty() ? null : user.getRoleIds().get(0);
                case "account" -> user.getAccount();
                default -> throw new IllegalArgumentException("CUSTOM 数据权限包含不允许的占位符");
            };
            Object boundValue = value;
            Class<?> valueType = value instanceof String ? String.class : Long.class;
            if (value == null && valueType == Long.class) {
                boundValue = -1L;
            }
            parameters.add(new ScopeParameter(boundValue, valueType));
            matcher.appendReplacement(result, "?");
        }
        matcher.appendTail(result);
        return new DataScopeCondition(result.toString(), parameters);
    }

    private String safeIdentifier(String identifier) {
        if (!identifier.matches("[A-Za-z_][A-Za-z0-9_.]*")) {
            throw new IllegalArgumentException("数据权限字段无效");
        }
        return identifier;
    }

    private boolean isAdministrator(AuthenticatedUser user) {
        return user.getRoleName() != null && user.getRoleName().toLowerCase(Locale.ROOT).contains("administrator");
    }

    private DataScopeRule mapRule(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new DataScopeRule(rs.getLong("id"), rs.getString("resource_code"), rs.getString("scope_column"),
                rs.getString("scope_field"), rs.getString("scope_class"), rs.getInt("scope_type"), rs.getString("scope_value"));
    }

    private record DataScopeRule(Long id, String resourceCode, String scopeColumn, String scopeField,
                                 String scopeClass, Integer scopeType, String scopeValue) {
    }

    private record RuleResolution(DataScopeRule rule, boolean hasRule, boolean denied) {
        static RuleResolution none() { return new RuleResolution(null, false, false); }
        static RuleResolution deniedResolution() { return new RuleResolution(null, true, true); }
        static RuleResolution ruleResolution(DataScopeRule rule) { return new RuleResolution(Objects.requireNonNull(rule), true, false); }
    }

    private record DataScopeCondition(String sql, List<ScopeParameter> parameters) {
    }

    private record ScopeParameter(Object value, Class<?> type) {
    }
}
