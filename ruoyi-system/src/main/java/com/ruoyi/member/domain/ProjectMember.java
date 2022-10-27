package com.ruoyi.member.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_project_member")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember  extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String projectCode;
    private String memberCode;
    private String joinTime;
    private Integer isOwner;
    private String authorize;
}
