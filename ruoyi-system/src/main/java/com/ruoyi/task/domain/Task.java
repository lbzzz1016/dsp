package com.ruoyi.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.domain.BaseDomain;
import com.ruoyi.project.domain.Project;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@TableName("team_task")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("code")
    private String code;
    @TableField("project_code")
    private String projectCode;
    @TableField("name")
    private String name;
    @TableField("pri")
    private Integer pri;
    @TableField(exist = false)
    private String priText;
    @TableField("execute_status")
    private String executeStatus;
    @TableField("description")
    private String description;
    @TableField("create_by")
    private String createBy;
    @TableField("create_time")
    private String createTime;
    @TableField("assign_to")
    private String assignTo;
    @TableField("deleted")
    private Integer deleted;
    @TableField("stage_code")
    private String stageCode;
    @TableField("task_tag")
    private String taskTag;
    @TableField("done")
    private Integer done;
    @TableField("begin_time")
    private String beginTime;
    @TableField("end_time")
    private String endTime;
    @TableField("remind_time")
    private String remindTime;
    @TableField("pcode")
    private String pcode;
    @TableField(exist = false)
    private String pName;
    @TableField("sort")
    private Integer sort;
    @TableField("liked")
    private Integer liked;
    @TableField(exist = false)
    private Integer like;
    public Integer getLike(){
        return liked;
    }
    @TableField("star")
    private Integer star;
    @TableField("deleted_time")
    private String deletedTime;
    @TableField("private")
    private Integer privated;
    @TableField("id_num")
    private Integer idNum;
    @TableField("path")
    private String path;
    @TableField("schedule")
    private Integer schedule;
    @TableField("version_code")
    private String versionCode;
    @TableField("features_code")
    private String featuresCode;
    @TableField("work_time")
    private Integer workTime;
    @TableField("status")
    private Integer status;

    @TableField(exist = false)
    private List<Task> childList;

    public Integer getPrivate(){
        return privated;
    }

    @TableField(exist = false)
    private SysUser executor;
    @TableField(exist = false)
    private Project projectInfo;
}
