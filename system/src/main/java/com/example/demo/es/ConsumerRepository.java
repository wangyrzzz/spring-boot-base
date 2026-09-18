package com.example.demo.es;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * @Author: WangYuanrong
 * @Date: 2022/12/28 11:13
 */
@Profile("!test")
public interface ConsumerRepository extends ElasticsearchRepository<Consumer, Long> {

    Consumer findByOpenId(String openId);

    List<Consumer> findByOpenIdLike(String openId);

    List<Consumer> findByMobile(String mobile);

    void deleteByOpenId(String openId);
}
