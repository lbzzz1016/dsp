package com.ruoyi.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.ProjectLog;
import com.ruoyi.project.mapper.ProjectLogMapper;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectLogService extends ServiceImpl<ProjectLogMapper, ProjectLog> {

    @Lazy
    @Autowired
    ProjectService projectService;

    @Autowired
    ISysUserService sysUserService;

    public IPage<Map> getProjectLogByParam(IPage<Map> ipage, Map params){
        return baseMapper.selectProjectLogByParam(ipage,params);
    }

    public Project run(Map param){
        ProjectLog projectLog = ProjectLog.builder().actionType(MapUtils.getString(param,"action_type")).code(CommUtils.getUUID())
                .createTime(DateUtils.getTime()).toMemberCode(MapUtils.getString(param,"to_member_code"))
                .isComment(MapUtils.getInteger(param,"is_comment")).content(MapUtils.getString(param,"content",""))
                .type(MapUtils.getString(param,"type")).sourceCode(MapUtils.getString(param,"source_code"))
                .memberCode(MapUtils.getString(param,"member_code")).projectCode(MapUtils.getString(param,"project_code")).build();
        Project project = projectService.getProjectProjectByCode(projectLog.getProjectCode());
        projectLog.setProjectCode(project.getCode());
        SysUser toMember = new SysUser();
        if(StringUtils.isNotEmpty(projectLog.getToMemberCode())){
            toMember = sysUserService.getUserByCode(projectLog.getToMemberCode());
        }
        String type = projectLog.getType();
        if("create".equals(type)){
            projectLog.setIcon("plus");
            projectLog.setRemark("创建了项目");
            projectLog.setContent(project.getName());
        }else if("edit".equals(type)){
            projectLog.setIcon("edit");
            projectLog.setRemark("编辑了项目");
            projectLog.setContent(project.getName());
        }else if("name".equals(type)){
            projectLog.setIcon("edit");
            projectLog.setRemark("修改了项目名称");
            projectLog.setContent(project.getName());
        }else if("content".equals(type)){
            projectLog.setIcon("file-text");
            projectLog.setRemark("更新了备注");
            projectLog.setContent(project.getDescription());
        }else if("clearContent".equals(type)){
            projectLog.setIcon("file-text");
            projectLog.setRemark("清空了备注");
        }else if("inviteMember".equals(type)){
            projectLog.setIcon("user-add");
            projectLog.setRemark("邀请"+toMember.getNickName()+"加入项目");
            projectLog.setContent(toMember.getNickName());
        }else if("removeMember".equals(type)){
            projectLog.setIcon("user-delete");
            projectLog.setRemark("移除了成员"+toMember.getNickName());
            projectLog.setContent(toMember.getNickName());
        }else if("recycle".equals(type)){
            projectLog.setIcon("delete");
            projectLog.setRemark("把项目移到了回收站");
        }else if("recovery".equals(type)){
            projectLog.setIcon("undo");
            projectLog.setRemark("恢复了项目");
        }else if("archive".equals(type)){
            projectLog.setIcon("delete");
            projectLog.setRemark("归档了项目");
        }else if("recoveryArchive".equals(type)){
            projectLog.setIcon("undo");
            projectLog.setRemark("恢复了项目");
        }else if("uploadFile".equals(type)){
            projectLog.setIcon("link");
            projectLog.setRemark("上传了文件文件");
            projectLog.setContent("<a target=\"_blank\" class=\"muted\" href=\""+MapUtils.getString(param,"url")+" \">\""+MapUtils.getString(param,"title")+"</a>");
        }else if("deleteFile".equals(type)){
            projectLog.setIcon("disconnect");
            projectLog.setRemark("删除了文件");
            projectLog.setContent("<a target=\"_blank\" class=\"muted\" href=\""+MapUtils.getString(param,"url")+" \">\""+MapUtils.getString(param,"title")+"</a>");
        }else{
            projectLog.setIcon("plus");
            projectLog.setRemark("创建了文件");
        }
        baseMapper.insert(projectLog);
        return project;
    }

}
