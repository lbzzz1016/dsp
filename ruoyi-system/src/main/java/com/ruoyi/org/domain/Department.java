package com.ruoyi.org.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_department")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Department  extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String organizationCode;
    private String name;
    private Integer sort;
    private String pcode;
    private String icon;
    private String createTime;
    private String path;
}
