package com.example.demo.query;

import com.example.demo.common.PageParam;
import lombok.Data;

/**
 * @Author: yanhongwei
 * @Date: 2023-03-06  16:50
 */
@Data
public class UserQuery extends PageParam {


    private String username;

    private String realName;

    private Integer gender;

    private String mobile;
}
