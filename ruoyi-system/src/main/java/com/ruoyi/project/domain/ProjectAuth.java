package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_project_auth")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAuth extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private Integer status;

    private Integer sort;

    @TableField("`desc`")
    private String desc;
    private Integer createBy;
    private String createAt;
    private String organizationCode;
    private Integer isDefault;
    private String type;

    @TableField(exist = false)
    private Integer canDelete;
}
