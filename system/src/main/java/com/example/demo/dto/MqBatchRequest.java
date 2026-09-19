package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "消息批量操作请求")
public record MqBatchRequest(@Schema(description = "消息记录 ID 列表") List<Long> ids) {
}
