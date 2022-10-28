package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@TableName("team_project_version")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVersion extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String name;
    private String description;
    private String createTime;
    private String updateTime;
    private String organizationCode;
    private String publishTime;
    private String startTime;
    private Integer status;
    private Integer schedule;
    private String planPublishTime;
    private String featuresCode;
}
