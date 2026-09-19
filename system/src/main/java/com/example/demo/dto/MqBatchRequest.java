package com.example.demo.dto;

import java.util.List;

public record MqBatchRequest(List<Long> ids) {
}
