package com.ruoyi.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.vo.ProjectVo;
import com.ruoyi.project.domain.bo.ProjectBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 项目Service接口
 *
 * @author lbzzz
 * @date 2022-09-26
 */
public interface ProjectService {

    /**
     * 查询项目
     */
    ProjectVo queryById(Integer id);

    /**
     * 查询项目列表
     */
    TableDataInfo<ProjectVo> queryPageList(ProjectBo bo, PageQuery pageQuery);

    /**
     * 查询项目列表
     */
    List<ProjectVo> queryList(ProjectBo bo);

    /**
     * 修改项目
     */
    Boolean insertByBo(ProjectBo bo);

    /**
     * 修改项目
     */
    Boolean updateByBo(ProjectBo bo);

    /**
     * 校验并批量删除项目信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);

    public Project getProjectProjectByCode(String code);
}
