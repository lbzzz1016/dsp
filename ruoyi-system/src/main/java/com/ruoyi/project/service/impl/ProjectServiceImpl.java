package com.ruoyi.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.project.domain.bo.ProjectBo;
import com.ruoyi.project.domain.vo.ProjectVo;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.service.ProjectService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 项目Service业务层处理
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper baseMapper;

    /**
     * 查询项目
     */
    @Override
    public ProjectVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询项目列表
     */
    @Override
    public TableDataInfo<ProjectVo> queryPageList(ProjectBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Project> lqw = buildQueryWrapper(bo);
        Page<ProjectVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询项目列表
     */
    @Override
    public List<ProjectVo> queryList(ProjectBo bo) {
        LambdaQueryWrapper<Project> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Project> buildQueryWrapper(ProjectBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Project> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getCover()), Project::getCover, bo.getCover());
        lqw.like(StringUtils.isNotBlank(bo.getName()), Project::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), Project::getCode, bo.getCode());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), Project::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getAccessControlType()), Project::getAccessControlType, bo.getAccessControlType());
        lqw.eq(StringUtils.isNotBlank(bo.getWhiteList()), Project::getWhiteList, bo.getWhiteList());
        lqw.eq(bo.getOrder() != null, Project::getOrder, bo.getOrder());
        lqw.eq(bo.getDeleted() != null, Project::getDeleted, bo.getDeleted());
        lqw.eq(StringUtils.isNotBlank(bo.getTemplateCode()), Project::getTemplateCode, bo.getTemplateCode());
        lqw.eq(bo.getSchedule() != null, Project::getSchedule, bo.getSchedule());
        lqw.eq(StringUtils.isNotBlank(bo.getOrganizationCode()), Project::getOrganizationCode, bo.getOrganizationCode());
        lqw.eq(StringUtils.isNotBlank(bo.getDeletedTime()), Project::getDeletedTime, bo.getDeletedTime());
        lqw.eq(bo.getPrivated() != null, Project::getPrivated, bo.getPrivated());
        lqw.eq(StringUtils.isNotBlank(bo.getPrefix()), Project::getPrefix, bo.getPrefix());
        lqw.eq(bo.getOpenPrefix() != null, Project::getOpenPrefix, bo.getOpenPrefix());
        lqw.eq(bo.getArchive() != null, Project::getArchive, bo.getArchive());
        lqw.eq(StringUtils.isNotBlank(bo.getArchiveTime()), Project::getArchiveTime, bo.getArchiveTime());
        lqw.eq(bo.getOpenBeginTime() != null, Project::getOpenBeginTime, bo.getOpenBeginTime());
        lqw.eq(bo.getOpenTaskPrivate() != null, Project::getOpenTaskPrivate, bo.getOpenTaskPrivate());
        lqw.eq(StringUtils.isNotBlank(bo.getTaskBoardTheme()), Project::getTaskBoardTheme, bo.getTaskBoardTheme());
        lqw.eq(StringUtils.isNotBlank(bo.getBeginTime()), Project::getBeginTime, bo.getBeginTime());
        lqw.eq(StringUtils.isNotBlank(bo.getEndTime()), Project::getEndTime, bo.getEndTime());
        lqw.eq(bo.getAutoUpdateSchedule() != null, Project::getAutoUpdateSchedule, bo.getAutoUpdateSchedule());
        return lqw;
    }

    /**
     * 新增项目
     */
    @Override
    public Boolean insertByBo(ProjectBo bo) {
        Project add = BeanUtil.toBean(bo, Project.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改项目
     */
    @Override
    public Boolean updateByBo(ProjectBo bo) {
        Project update = BeanUtil.toBean(bo, Project.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Project entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除项目
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    public Project getProjectProjectByCode(String code){
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCode, code);
        return baseMapper.selectOne(queryWrapper);
    }
}
