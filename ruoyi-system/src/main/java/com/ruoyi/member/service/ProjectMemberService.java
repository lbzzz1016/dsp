package com.ruoyi.member.service;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.member.domain.MemberAccount;
import com.ruoyi.member.domain.ProjectMember;
import com.ruoyi.member.mapper.ProjectMemberMapper;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.ProjectLogService;
import com.ruoyi.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectMemberService extends ServiceImpl<ProjectMemberMapper, ProjectMember> {

    @Autowired
    MemberAccountService memberAccountService;
    @Lazy
    @Autowired
    ProjectService projectService;

    @Lazy
    @Autowired
    ProjectLogService projectLogService;

    //查询项目的拥有者  is_owener=1
    public Map getProjectMemberByProjectCode(String projectCode){
        return baseMapper.getProjectMemberByProjectCodeOwner(projectCode);
    }

    public IPage<Map> getProjectMemberByProjectCode(ProjectMember projectMember){
        IPage<Map> ipage = new Page();
        ipage.setCurrent(projectMember.getCurrent());
        ipage.setSize(projectMember.getSize());
        IPage<Map> mapIPage = baseMapper.getProjectMemberByProjectCode(ipage,projectMember.getProjectCode());
        return mapIPage;
    }

    //根据项目编号和用户编号确定是否是项目成员
    public boolean isProjectMember(String projectCode, String memberCode){
        //List<Map> list = baseMapper.getProjectMemberByProjectCodeAndMemberCode(projectCode,memberCode);
        List<ProjectMember> list = lambdaQuery().eq(ProjectMember::getMemberCode,memberCode)
                .eq(ProjectMember::getProjectCode,projectCode).list();
        if(!CollectionUtils.isEmpty(list)){
            return true;
        }
        return false;
    }

    public Map gettMemberCodeAndNameByProjectCode(String projectCode){
        return baseMapper.selectMemberCodeAndNameByProjectCode(projectCode);
    }

    @Transactional
    public ProjectMember inviteMember(String memberCode, String projectCode, Integer isOwner){
        Project project = projectService.lambdaQuery().eq(Project::getCode,projectCode).one();
        if(ObjectUtil.isEmpty(project)){
            throw new CustomException("该项目已失效！");
        }
        ProjectMember projectMember = lambdaQuery().eq(ProjectMember::getMemberCode,memberCode)
                .eq(ProjectMember::getProjectCode,projectCode)
                .one();
        if(ObjectUtil.isNotEmpty(projectMember)){
            return projectMember;
        }
        projectMember = ProjectMember.builder().memberCode(memberCode).
                projectCode(projectCode).isOwner(isOwner).
                joinTime(DateUtils.getTime()).build();
        save(projectMember);
        memberAccountService.inviteMember(MemberAccount.builder().memberCode(memberCode).
                organizationCode(project.getOrganizationCode()).build());

        projectLogService.run(new HashMap(){{
            put("member_code",memberCode);
            put("source_code",project.getCode());
            put("type","inviteMember");
            put("to_member_code",memberCode);
            put("is_comment",0);
            put("content","");
            put("project_code",project.getCode());
        }});
        return projectMember;
    }
    @Transactional
    public Integer removeMember(String memberCode, Project project){
        LambdaQueryWrapper<ProjectMember> lambdaQueryWrapper=new LambdaQueryWrapper<ProjectMember>();
        lambdaQueryWrapper.eq(ProjectMember::getProjectCode,project.getCode());
        lambdaQueryWrapper.eq(ProjectMember::getMemberCode,memberCode);
        Integer result = baseMapper.delete(lambdaQueryWrapper);
        Integer result2 = memberAccountService.removeMemberAccount(memberCode, project.getOrganizationCode());

        projectLogService.run(new HashMap(){{
            put("member_code",memberCode);
            put("source_code",project.getCode());
            put("type","removeMember");
            put("to_member_code",memberCode);
            put("is_comment",0);
            put("content","");
            put("project_code",project.getCode());
        }});
        return result + result2;
    }


}
