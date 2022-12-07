package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysMessageBoard;
import com.ruoyi.system.domain.vo.SysMessageBoardVo;
import com.ruoyi.system.domain.bo.SysMessageBoardBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 留言板Service接口
 *
 * @author lbzzz
 * @date 2022-12-07
 */
public interface ISysMessageBoardService {

    /**
     * 查询留言板
     */
    SysMessageBoardVo queryById(Long messageId);

    /**
     * 查询留言板列表
     */
    TableDataInfo<SysMessageBoardVo> queryPageList(SysMessageBoardBo bo, PageQuery pageQuery);

    /**
     * 查询留言板列表
     */
    List<SysMessageBoardVo> queryList(SysMessageBoardBo bo);

    /**
     * 修改留言板
     */
    Boolean insertByBo(SysMessageBoardBo bo);

    /**
     * 修改留言板
     */
    Boolean updateByBo(SysMessageBoardBo bo);

    /**
     * 校验并批量删除留言板信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
