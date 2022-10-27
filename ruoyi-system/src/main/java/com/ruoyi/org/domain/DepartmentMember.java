package com.ruoyi.org.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_department_member")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentMember extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String departmentCode;
    private String organizationCode;
    private String accountCode;
    private String joinTime;
    private Integer isPrincipal;
    private Integer isOwner;
    private String authorize;
}
