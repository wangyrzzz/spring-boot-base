package com.example.demo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @Author: yanhongwei
 * @Date: 2023-03-07  16:26
 */
@Data
public class LoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
