package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_project")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project  extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String cover;
    private String name;
    private String code;
    private String description;
    private String accessControlType;
    private String whiteList;
    @TableField(exist = false)
    private Long order;
    private Integer deleted;
    private String templateCode;
    private Double schedule;
    private String createTime;
    private String organizationCode;
    private String deletedTime;
    @TableField("private")
    private Integer privated;
    private String prefix;
    private Integer openPrefix;
    private Integer archive;
    private String archiveTime;
    private Integer openBeginTime;
    private Integer openTaskPrivate;
    private String taskBoardTheme;
    private String beginTime;
    private String endTime;
    private Integer autoUpdateSchedule;

    @TableField(exist = false)
    private Integer collected;
    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private String ownerAvatar;

}
