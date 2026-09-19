package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "访问令牌响应")
public class TokenPair {
    @Schema(description = "访问令牌")
    private String accessToken;
    @Schema(description = "刷新令牌")
    private String refreshToken;
    @Schema(description = "令牌类型")
    private String tokenType = "Bearer";
    @Schema(description = "访问令牌有效期，单位：秒")
    private long accessTokenExpiresIn;
    @Schema(description = "刷新令牌有效期，单位：秒")
    private Long refreshTokenExpiresIn;
}
