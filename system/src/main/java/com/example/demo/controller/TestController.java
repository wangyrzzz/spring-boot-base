package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.AuthConstant;
import com.example.demo.es.Consumer;
import com.example.demo.es.ConsumerRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: WangYuanrong
 * @Date: 2022/4/8 11:15
 */
@RestController
@RequestMapping("/test")
@Slf4j
@Tag(name = "测试接口")
@Profile("!test")
@ConditionalOnProperty(prefix = "sys.infra.elasticsearch", name = "enabled", havingValue = "true")
@PreAuth(AuthConstant.DENY_ALL)
public class TestController {

    @Autowired
    private ConsumerRepository consumerRepository;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/openId")
    public Consumer openId(String openId) {
        final Consumer consumer = consumerRepository.findByOpenId(openId);
        return consumer;
    }

    @GetMapping("/likeOpenId")
    public List<Consumer> likeOpenId(String openId) {
        return consumerRepository.findByOpenIdLike(openId);
    }



    @GetMapping("/mobile")
    public List<Consumer> mobile(String mobile) {
        return consumerRepository.findByMobile(mobile);
    }

    @GetMapping("/query")
    public List<Consumer> query(String mobile) {
        return null;
    }

    @GetMapping("delete")
    public Boolean delete(String openId) {
        consumerRepository.deleteByOpenId(openId);
        return Boolean.TRUE;
    }

    @GetMapping("/insert")
    public Boolean insert() {
        Consumer consumer = new Consumer();
        consumer.setId(111L);
        consumer.setOpenId("xxx");
        consumer.setMobile("13111111111");
        consumerRepository.save(consumer);
        return true;
    }
}
