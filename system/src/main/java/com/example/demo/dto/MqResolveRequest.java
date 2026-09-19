package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "消息失败记录处理请求")
public record MqResolveRequest(@Schema(description = "处理备注") String remark) {
}
