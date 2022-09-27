package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目对象 project
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("project")
public class Project extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 封面
     */
    private String cover;
    /**
     * 名称
     */
    private String name;
    /**
     * 编号
     */
    private String code;
    /**
     * 描述
     */
    private String description;
    /**
     * 访问控制l类型
     */
    private String accessControlType;
    /**
     * 可以访问项目的权限组（白名单）
     */
    private String whiteList;
    /**
     * 排序
     */
    @TableField(exist = false)
    private Integer order;
    /**
     * 删除标记
     */
    private Integer deleted;
    /**
     * 项目类型
     */
    private String templateCode;
    /**
     * 进度
     */
    private BigDecimal schedule;
    /**
     * 组织id
     */
    private String organizationCode;
    /**
     * 删除时间
     */
    private String deletedTime;
    /**
     * 是否私有
     */
    @TableField("private")
    private Integer privated;
    /**
     * 项目前缀
     */
    private String prefix;
    /**
     * 是否开启项目前缀
     */
    private Integer openPrefix;
    /**
     * 是否归档
     */
    private Integer archive;
    /**
     * 归档时间
     */
    private String archiveTime;
    /**
     * 是否开启任务开始时间
     */
    private Integer openBeginTime;
    /**
     * 是否开启新任务默认开启隐私模式
     */
    private Integer openTaskPrivate;
    /**
     * 看板风格
     */
    private String taskBoardTheme;
    /**
     * 项目开始日期
     */
    private String beginTime;
    /**
     * 项目截止日期
     */
    private String endTime;
    /**
     * 自动更新项目进度
     */
    private Integer autoUpdateSchedule;

    @TableField(exist = false)
    private Integer collected;
    @TableField(exist = false)
    private String owner_name;
    @TableField(exist = false)
    private String owner_avatar;
}
