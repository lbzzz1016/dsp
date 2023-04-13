package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysClockIn;
import com.ruoyi.system.domain.vo.SysClockInVo;
import com.ruoyi.system.domain.bo.SysClockInBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 补卡流程Service接口
 *
 * @author lbzzz
 * @date 2023-04-12
 */
public interface ISysClockInService {

    /**
     * 查询补卡流程
     */
    SysClockInVo queryById(Long processId);

    /**
     * 查询补卡流程
     */
    Long queryByTaskId(String taskId);

    /**
     * 查询补卡流程列表
     */
    TableDataInfo<SysClockInVo> queryPageList(SysClockInBo bo, PageQuery pageQuery);

    /**
     * 查询补卡流程列表
     */
    List<SysClockInVo> queryList(SysClockInBo bo);

    /**
     * 修改补卡流程
     */
    Boolean insert(SysClockIn add);

    /**
     * 修改补卡流程
     */
    Boolean update(SysClockIn update);

    /**
     * 校验并批量删除补卡流程信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
