package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@TableName("team_project_template")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTemplate extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Integer sort;
    private String createTime;
    private String code;
    private String organizationCode;
    private String cover;
    private String memberCode;
    @TableField("is_system")
    private Integer isSystem;
    @TableField(exist = false)
    private List<String> taskStages;
}
