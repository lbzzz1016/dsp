package com.ruoyi.member.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@TableName("team_member_account")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberAccount extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String memberCode;
    private String organizationCode;
    private String departmentCode;
    private String authorize;
    private Integer isOwner;
    private String name;
    private String mobile;
    private String email;
    private String createTime;
    private String lastLoginTime;
    private Integer status;
    private String description;
    private String avatar;
    private String position;
    private String department;
    @TableField(exist = false)
    private List<String> nodeList;
    @TableField(exist = false)
    private List<Map<String, String>> departList;

}
