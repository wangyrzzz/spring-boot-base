package com.example.demo.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(indexName = "consumer")
@Schema(description = "消费者搜索文档")
public class Consumer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "文档主键")
    private Long id;

    /**
     * 注册类型  1.自动注册 2.微信信息获取
     */
    @Field(value = "register_type")
    @Schema(description = "注册类型：1自动注册，2微信信息获取")
    private Integer registerType;

    /**
     * 小程序openid
     */
    @Field("open_id")
    @Schema(description = "小程序 OpenID")
    private String openId;

    /**
     * 小程序在同一主体下唯一标识
     */
    @Field("union_id")
    @Schema(description = "同一主体下唯一标识")
    private String unionId;

    /**
     * 是否是白象会员 0.不是 1.是，当为1是，id为白象推过来的用户id
     */
    @Field("wet_member")
    @Schema(description = "是否为白象会员：0否，1是")
    private Integer wetMember;

    /**
     * 白象用户id
     */
    @Field("wet_member_id")
    @Schema(description = "白象会员用户 ID")
    private Long wetMemberId;

    /**
     * 昵称
     */
    @Field("nick_name")
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 头像URL
     */
    @Field("avatar_url")
    @Schema(description = "头像 URL")
    private String avatarUrl;

    /**
     * 0.未知 1.男 2.女
     */
    @Field("gender")
    @Schema(description = "性别：0未知，1男，2女")
    private Integer gender;

    /**
     * 用户所在国家
     */
    @Field("country")
    @Schema(description = "所在国家")
    private String country;

    /**
     * 用户所在省份
     */
    @Field("province")
    @Schema(description = "所在省份")
    private String province;

    /**
     * 用户所在城市
     */
    @Field("city")
    @Schema(description = "所在城市")
    private String city;

    /**
     * 手机号码
     */
    @Field("mobile")
    @Schema(description = "手机号码")
    private String mobile;

    /**
     * 状态 1.启用 0.禁用
     */
    @Field("status")
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;

    /**
     * 是否封号  1.未封号 0.封号
     */
    @Field("close")
    @Schema(description = "是否封号：0封号，1未封号")
    private Integer close;

    /**
     * 类型 1.小程序 2.公众号
     */
    @Field("type")
    @Schema(description = "账号类型：1小程序，2公众号")
    private Integer type;

    /**
     * 风险等级
     */
    @Field("risk_level")
    @Schema(description = "风险等级")
    private Integer riskLevel;

    /**
     * 扫码次数
     */
    @Field("scan_count")
    @Schema(description = "扫码次数")
    private Integer scanCount;

    /**
     * 中奖次数
     */
    @Field("reward_count")
    @Schema(description = "中奖次数")
    private Integer rewardCount;

    /**
     * 创建人
     */
    @Field("create_by")
    @Schema(description = "创建人")
    private Long createBy;

    /**
     * 创建日期
     */
    @Field("create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @Field("update_by")
    @Schema(description = "更新人")
    private Long updateBy;

    /**
     * 更新日期
     */
    @Field("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;


}
