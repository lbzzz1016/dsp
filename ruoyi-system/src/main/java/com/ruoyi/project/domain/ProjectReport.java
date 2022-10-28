package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_project_report")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectReport extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String projectCode;
    private String date;
    private String content;
    private String createTime;
    private String updateTime;
}
