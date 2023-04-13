package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.domain.SysProcess;
import com.ruoyi.system.domain.vo.SysProcessVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.SysClockInBo;
import com.ruoyi.system.domain.vo.SysClockInVo;
import com.ruoyi.system.domain.SysClockIn;
import com.ruoyi.system.mapper.SysClockInMapper;
import com.ruoyi.system.service.ISysClockInService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 补卡流程Service业务层处理
 *
 * @author lbzzz
 * @date 2023-04-12
 */
@RequiredArgsConstructor
@Service
public class SysClockInServiceImpl implements ISysClockInService {

    private final SysClockInMapper baseMapper;

    /**
     * 查询补卡流程
     */
    @Override
    public SysClockInVo queryById(Long processId){
        return baseMapper.selectVoById(processId);
    }

    @Override
    public Long queryByTaskId(String taskId){

        SysClockInVo sysClockInVo = baseMapper.selectVoOne(new LambdaQueryWrapper<SysClockIn>()
            .eq(SysClockIn::getTaskId, taskId));
        return sysClockInVo.getProcessId();
    }

    /**
     * 查询补卡流程列表
     */
    @Override
    public TableDataInfo<SysClockInVo> queryPageList(SysClockInBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysClockIn> lqw = buildQueryWrapper(bo);
        Page<SysClockInVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询补卡流程列表
     */
    @Override
    public List<SysClockInVo> queryList(SysClockInBo bo) {
        LambdaQueryWrapper<SysClockIn> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysClockIn> buildQueryWrapper(SysClockInBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysClockIn> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), SysClockIn::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getUserNumber()), SysClockIn::getUserNumber, bo.getUserNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getCheckTime()), SysClockIn::getCheckTime, bo.getCheckTime());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysClockIn::getStatus, bo.getStatus());
        if (StringUtils.isNotBlank(bo.getCheckDate())) {
            lqw.ge(SysClockIn::getCheckDate, DateUtils.dateTime(DateUtils.YYYY_MM_DD, bo.getCheckDate()));

        }
        lqw.orderByDesc(SysClockIn::getCheckDate, SysClockIn::getCreateTime);
        return lqw;
    }

    /**
     * 新增补卡流程
     */
    @Override
    public Boolean insert(SysClockIn add) {
//        SysClockIn add = BeanUtil.toBean(bo, SysClockIn.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            System.out.println(flag);
        }
        return flag;
    }

    /**
     * 修改补卡流程
     */
    @Override
    public Boolean update(SysClockIn update) {
//        SysClockIn update = BeanUtil.toBean(bo, SysClockIn.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysClockIn entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除补卡流程
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
