package com.example.demo.controller;

import com.example.demo.common.AuthUserContext;

/**
 * @Author: WangYuanrong
 * @Date: 2021/6/18 17:56
 */
public abstract class BaseController {

    protected Long getUserId() {
        return AuthUserContext.required().getUserId();
    }


//    protected Long getUserId() {
//        return 1L;
//    }
}
