package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.SysMessageBoardBo;
import com.ruoyi.system.domain.vo.SysMessageBoardVo;
import com.ruoyi.system.domain.SysMessageBoard;
import com.ruoyi.system.mapper.SysMessageBoardMapper;
import com.ruoyi.system.service.ISysMessageBoardService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 留言板Service业务层处理
 *
 * @author lbzzz
 * @date 2022-12-07
 */
@RequiredArgsConstructor
@Service
public class SysMessageBoardServiceImpl implements ISysMessageBoardService {

    private final SysMessageBoardMapper baseMapper;

    /**
     * 查询留言板
     */
    @Override
    public SysMessageBoardVo queryById(Long messageId){
        return baseMapper.selectVoById(messageId);
    }

    /**
     * 查询留言板列表
     */
    @Override
    public TableDataInfo<SysMessageBoardVo> queryPageList(SysMessageBoardBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysMessageBoard> lqw = buildQueryWrapper(bo);
        Page<SysMessageBoardVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询留言板列表
     */
    @Override
    public List<SysMessageBoardVo> queryList(SysMessageBoardBo bo) {
        LambdaQueryWrapper<SysMessageBoard> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysMessageBoard> buildQueryWrapper(SysMessageBoardBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysMessageBoard> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getMessageTitle()), SysMessageBoard::getMessageTitle, bo.getMessageTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getMessageType()), SysMessageBoard::getMessageType, bo.getMessageType());
        lqw.eq(StringUtils.isNotBlank(bo.getMessageContent()), SysMessageBoard::getMessageContent, bo.getMessageContent());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysMessageBoard::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增留言板
     */
    @Override
    public Boolean insertByBo(SysMessageBoardBo bo) {
        SysMessageBoard add = BeanUtil.toBean(bo, SysMessageBoard.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        return flag;
    }

    /**
     * 修改留言板
     */
    @Override
    public Boolean updateByBo(SysMessageBoardBo bo) {
        SysMessageBoard update = BeanUtil.toBean(bo, SysMessageBoard.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysMessageBoard entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除留言板
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
