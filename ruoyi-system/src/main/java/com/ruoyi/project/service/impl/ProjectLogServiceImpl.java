package com.ruoyi.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.ProjectService;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.domain.bo.ProjectLogBo;
import com.ruoyi.project.domain.vo.ProjectLogVo;
import com.ruoyi.project.domain.ProjectLog;
import com.ruoyi.project.mapper.ProjectLogMapper;
import com.ruoyi.project.service.ProjectLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 项目日志Service业务层处理
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@RequiredArgsConstructor
@Service
public class ProjectLogServiceImpl implements ProjectLogService {

    private final ProjectLogMapper baseMapper;

    @Autowired
    ProjectService projectService;

    @Autowired
    ISysUserService userService;

    /**
     * 查询项目日志
     */
    @Override
    public ProjectLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询项目日志列表
     */
    @Override
    public TableDataInfo<ProjectLogVo> queryPageList(ProjectLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProjectLog> lqw = buildQueryWrapper(bo);
        Page<ProjectLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询项目日志列表
     */
    @Override
    public List<ProjectLogVo> queryList(ProjectLogBo bo) {
        LambdaQueryWrapper<ProjectLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProjectLog> buildQueryWrapper(ProjectLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProjectLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), ProjectLog::getCode, bo.getCode());
        lqw.eq(StringUtils.isNotBlank(bo.getMemberCode()), ProjectLog::getMemberCode, bo.getMemberCode());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), ProjectLog::getContent, bo.getContent());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), ProjectLog::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceCode()), ProjectLog::getSourceCode, bo.getSourceCode());
        lqw.eq(StringUtils.isNotBlank(bo.getActionType()), ProjectLog::getActionType, bo.getActionType());
        lqw.eq(StringUtils.isNotBlank(bo.getToMemberCode()), ProjectLog::getToMemberCode, bo.getToMemberCode());
        lqw.eq(bo.getIsComment() != null, ProjectLog::getIsComment, bo.getIsComment());
        lqw.eq(StringUtils.isNotBlank(bo.getProjectCode()), ProjectLog::getProjectCode, bo.getProjectCode());
        lqw.eq(StringUtils.isNotBlank(bo.getIcon()), ProjectLog::getIcon, bo.getIcon());
        lqw.eq(bo.getIsRobot() != null, ProjectLog::getIsRobot, bo.getIsRobot());
        return lqw;
    }

    /**
     * 新增项目日志
     */
    @Override
    public Boolean insertByBo(ProjectLogBo bo) {
        ProjectLog add = BeanUtil.toBean(bo, ProjectLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改项目日志
     */
    @Override
    public Boolean updateByBo(ProjectLogBo bo) {
        ProjectLog update = BeanUtil.toBean(bo, ProjectLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProjectLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除项目日志
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }


    public IPage<Map> getProjectLogByParam(IPage<Map> ipage, Map params){
        return baseMapper.selectProjectLogByParam(ipage,params);
    }

    public Project run(Map param){
        ProjectLog projectLog = ProjectLog.builder().actionType(MapUtils.getString(param,"action_type")).code(CommUtils.getUUID())
            .toMemberCode(MapUtils.getString(param,"to_member_code"))
            .isComment(MapUtils.getInteger(param,"is_comment")).content(MapUtils.getString(param,"content",""))
            .type(MapUtils.getString(param,"type")).sourceCode(MapUtils.getString(param,"source_code"))
            .memberCode(MapUtils.getString(param,"member_code")).projectCode(MapUtils.getString(param,"project_code")).build();
        Project project = projectService.getProjectProjectByCode(projectLog.getProjectCode());
        projectLog.setProjectCode(project.getCode());
        SysUser toMember = new SysUser();
        if(StringUtils.isNotEmpty(projectLog.getToMemberCode())){
            toMember = userService.getUserByCode(projectLog.getToMemberCode());
        }
//        Notify notify = new Notify();
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
            projectLog.setContent("<a target=\"_blank\" class=\"muted\" href=\""+ MapUtils.getString(param,"url")+" \">\""+MapUtils.getString(param,"title")+"</a>");
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
