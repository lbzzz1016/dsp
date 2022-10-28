package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("team_project_info")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String value;
    private String description;
    private String createTime;
    private String updateTime;
    private String organizationCode;
    private String projectCode;
    private Integer sort;
    private String code;
}
