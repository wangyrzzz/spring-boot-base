package com.example.demo.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "retail.data-scope")
public class DataScopeProperties {
    private boolean enabled = true;
}
