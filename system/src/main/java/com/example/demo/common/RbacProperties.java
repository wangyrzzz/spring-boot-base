package com.example.demo.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sys.rbac")
public class RbacProperties {
    private boolean enabled = true;
    private boolean administratorBypass = true;
}
