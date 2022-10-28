package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import com.ruoyi.common.utils.CommUtils;
import lombok.*;

import java.io.Serializable;

@Getter
@TableName("team_project_version_log")
@Data
@ToString
@AllArgsConstructor
@Builder
public class ProjectVersionLog   extends BaseDomain implements Serializable {

    public ProjectVersionLog(){
        setCode(CommUtils.getUUID());
    }
    public Integer getId() {
        return id;
    }

    public ProjectVersionLog setId(Integer id) {
        this.id = id;return this;
    }

    public String getCode() {
        return code;
    }

    public ProjectVersionLog setCode(String code) {
        this.code = code;return this;
    }

    public String getMember_code() {
        return memberCode;
    }

    public ProjectVersionLog setMember_code(String memberCode) {
        this.memberCode = memberCode;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ProjectVersionLog setContent(String content) {
        this.content = content;return this;
    }

    public String getRemark() {
        return remark;
    }

    public ProjectVersionLog setRemark(String remark) {
        this.remark = remark;return this;
    }

    public String getType() {
        return type;
    }

    public ProjectVersionLog setType(String type) {
        this.type = type;
        return this;
    }

    public String getCreate_time() {
        return createTime;
    }

    public ProjectVersionLog setCreate_time(String createTime) {
        this.createTime = createTime;return this;
    }

    public String getSource_code() {
        return sourceCode;
    }

    public ProjectVersionLog setSource_code(String sourceCode) {
        this.sourceCode = sourceCode;return this;
    }

    public String getProject_code() {
        return projectCode;
    }

    public ProjectVersionLog setProject_code(String projectCode) {
        this.projectCode = projectCode;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public ProjectVersionLog setIcon(String icon) {
        this.icon = icon;return this;
    }

    public String getFeatures_code() {
        return featuresCode;
    }

    public ProjectVersionLog setFeatures_code(String featuresCode) {
        this.featuresCode = featuresCode;return this;
    }

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String memberCode;
    private String content;
    private String remark;
    private String type;
    private String createTime;
    private String sourceCode;
    private String projectCode;
    private String icon;
    private String featuresCode;
    public  ProjectVersionLog(String memberCode,String content,String remark,String type,String createTime,String sourceCode,String projectCode,String icon,String featureCode){
        if(!CommUtils.isEmpty(memberCode))setMember_code(memberCode);
        if(!CommUtils.isEmpty(content))setContent(content);
        if(!CommUtils.isEmpty(remark))setRemark(remark);
        if(!CommUtils.isEmpty(type))setType(type);
        if(!CommUtils.isEmpty(createTime))setCreate_time(createTime);
        if(!CommUtils.isEmpty(sourceCode))setSource_code(sourceCode);
        if(!CommUtils.isEmpty(projectCode))setProject_code(projectCode);
        if(!CommUtils.isEmpty(icon))setIcon(icon);
        if(!CommUtils.isEmpty(featureCode))setFeatures_code(featureCode);
        setCode(CommUtils.getUUID());
    }
}
