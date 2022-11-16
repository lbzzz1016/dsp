package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import com.ruoyi.member.domain.Member;
import lombok.*;

import java.io.Serializable;

@TableName("team_project_log")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProjectLog  extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String memberCode;
    private String content;
    private String remark;
    private String type;
    private String createTime;
    private String sourceCode;
    private String actionType;
    private String toMemberCode;
    private Integer isComment;
    private String projectCode;
    private String icon;
    private Integer isRobot;

    @TableField(exist = false)
    private Member member;

    public Integer getId() {
        return id;
    }

    public ProjectLog setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ProjectLog setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public ProjectLog setMember_code(String memberCode) {
        this.memberCode = memberCode;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ProjectLog setContent(String content) {
        this.content = content;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public ProjectLog setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getType() {
        return type;
    }

    public ProjectLog setType(String type) {
        this.type = type;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public ProjectLog setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public ProjectLog setSource_code(String sourceCode) {
        this.sourceCode = sourceCode;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public ProjectLog setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getToMemberCode() {
        return toMemberCode;
    }

    public ProjectLog setToMemberCode(String toMemberCode) {
        this.toMemberCode = toMemberCode;
        return this;
    }

    public Integer getIsComment() {
        return isComment;
    }

    public ProjectLog setIsComment(Integer isComment) {
        this.isComment = isComment;
        return this;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public ProjectLog setProjectCode(String projectCode) {
        this.projectCode = projectCode;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public ProjectLog setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public Integer getIsRobot() {
        return isRobot;
    }

    public ProjectLog setIsRobot(Integer isRobot) {
        this.isRobot = isRobot;
        return this;
    }
}
