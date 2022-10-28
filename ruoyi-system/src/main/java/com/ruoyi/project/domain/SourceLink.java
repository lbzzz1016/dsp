package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@TableName("team_source_link")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceLink  extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String sourceType;
    private String sourceCode;
    private String linkType;
    private String linkCode;
    private String organizationCode;
    private String createBy;
    private String createTime;
    private Integer sort;

    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private Map sourceDetail;
}
