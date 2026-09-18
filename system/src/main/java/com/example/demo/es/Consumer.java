package com.example.demo.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(indexName = "consumer")
public class Consumer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    /**
     * 注册类型  1.自动注册 2.微信信息获取
     */
    @Field(value = "register_type")
    private Integer registerType;

    /**
     * 小程序openid
     */
    @Field("open_id")
    private String openId;

    /**
     * 小程序在同一主体下唯一标识
     */
    @Field("union_id")
    private String unionId;

    /**
     * 是否是白象会员 0.不是 1.是，当为1是，id为白象推过来的用户id
     */
    @Field("wet_member")
    private Integer wetMember;

    /**
     * 白象用户id
     */
    @Field("wet_member_id")
    private Long wetMemberId;

    /**
     * 昵称
     */
    @Field("nick_name")
    private String nickName;

    /**
     * 头像URL
     */
    @Field("avatar_url")
    private String avatarUrl;

    /**
     * 0.未知 1.男 2.女
     */
    @Field("gender")
    private Integer gender;

    /**
     * 用户所在国家
     */
    @Field("country")
    private String country;

    /**
     * 用户所在省份
     */
    @Field("province")
    private String province;

    /**
     * 用户所在城市
     */
    @Field("city")
    private String city;

    /**
     * 手机号码
     */
    @Field("mobile")
    private String mobile;

    /**
     * 状态 1.启用 0.禁用
     */
    @Field("status")
    private Integer status;

    /**
     * 是否封号  1.未封号 0.封号
     */
    @Field("close")
    private Integer close;

    /**
     * 类型 1.小程序 2.公众号
     */
    @Field("type")
    private Integer type;

    /**
     * 风险等级
     */
    @Field("risk_level")
    private Integer riskLevel;

    /**
     * 扫码次数
     */
    @Field("scan_count")
    private Integer scanCount;

    /**
     * 中奖次数
     */
    @Field("reward_count")
    private Integer rewardCount;

    /**
     * 创建人
     */
    @Field("create_by")
    private Long createBy;

    /**
     * 创建日期
     */
    @Field("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @Field("update_by")
    private Long updateBy;

    /**
     * 更新日期
     */
    @Field("update_time")
    private Date updateTime;


}
