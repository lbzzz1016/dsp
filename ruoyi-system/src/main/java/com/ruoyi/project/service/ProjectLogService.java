package com.ruoyi.project.service;

import com.ruoyi.project.domain.ProjectLog;
import com.ruoyi.project.domain.vo.ProjectLogVo;
import com.ruoyi.project.domain.bo.ProjectLogBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 项目日志Service接口
 *
 * @author lbzzz
 * @date 2022-09-26
 */
public interface ProjectLogService {

    /**
     * 查询项目日志
     */
    ProjectLogVo queryById(Long id);

    /**
     * 查询项目日志列表
     */
    TableDataInfo<ProjectLogVo> queryPageList(ProjectLogBo bo, PageQuery pageQuery);

    /**
     * 查询项目日志列表
     */
    List<ProjectLogVo> queryList(ProjectLogBo bo);

    /**
     * 修改项目日志
     */
    Boolean insertByBo(ProjectLogBo bo);

    /**
     * 修改项目日志
     */
    Boolean updateByBo(ProjectLogBo bo);

    /**
     * 校验并批量删除项目日志信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
