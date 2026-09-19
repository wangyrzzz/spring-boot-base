package com.example.demo.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * @Author: yanhongwei
 * @Date: 2023-03-07  16:26
 */
@Data
@Schema(description = "登录请求")
public class LoginDto {

    @Schema(description = "客户端 ID")
    private String clientId;

    @Schema(description = "客户端密钥")
    private String clientSecret;

    @NotBlank
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
