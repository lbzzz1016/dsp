package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysProcess;
import com.ruoyi.system.domain.vo.SysProcessVo;
import com.ruoyi.system.domain.bo.SysProcessBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 请假流程Service接口
 *
 * @author LBZ
 * @date 2022-09-15
 */
public interface ISysProcessService {

    /**
     * 查询请假流程
     */
    SysProcessVo queryById(Long processId);

    /**
     * 查询请假流程
     */
    Long queryByTaskId(String taskId);

    /**
     * 查询请假流程列表
     */
    TableDataInfo<SysProcessVo> queryPageList(SysProcessBo bo, PageQuery pageQuery);

    /**
     * 查询请假流程列表
     */
    List<SysProcessVo> queryList(SysProcessBo bo);

    /**
     * 修改请假流程
     */
    Boolean insert(SysProcess add);

    /**
     * 修改请假流程
     */
    Boolean update(SysProcess update);

    /**
     * 校验并批量删除请假流程信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
