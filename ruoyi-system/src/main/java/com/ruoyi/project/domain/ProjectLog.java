package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目日志对象 project_log
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("project_log")
public class ProjectLog extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 
     */
    private String code;
    /**
     * 操作人id
     */
    private String memberCode;
    /**
     * 操作内容
     */
    private String content;
    /**
     * 
     */
    private String remark;
    /**
     * 操作类型
     */
    private String type;
    /**
     * 任务id
     */
    private String sourceCode;
    /**
     * 场景类型
     */
    private String actionType;
    /**
     * 
     */
    private String toMemberCode;
    /**
     * 是否评论，0：否
     */
    private Integer isComment;
    /**
     * 
     */
    private String projectCode;
    /**
     * 
     */
    private String icon;
    /**
     * 是否机器人
     */
    private Integer isRobot;

//    @TableField(exist = false)
//    private Member member;

    public Long getId() {
        return id;
    }

    public ProjectLog setId(Long id) {
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

    public ProjectLog setMemberCode(String memberCode) {
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

    public String getSourceCode() {
        return sourceCode;
    }

    public ProjectLog setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public ProjectLog setActionType(String action_type) {
        this.actionType = action_type;
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

    public ProjectLog setIsComment(Integer is_comment) {
        this.isComment = is_comment;
        return this;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public ProjectLog setProjectCode(String project_code) {
        this.projectCode = project_code;
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
