package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.workflow.domain.WfDeployForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.SysProcessBo;
import com.ruoyi.system.domain.vo.SysProcessVo;
import com.ruoyi.system.domain.SysProcess;
import com.ruoyi.system.mapper.SysProcessMapper;
import com.ruoyi.system.service.ISysProcessService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 请假流程Service业务层处理
 *
 * @author LBZ
 * @date 2022-09-15
 */
@RequiredArgsConstructor
@Service
public class SysProcessServiceImpl implements ISysProcessService {

    private final SysProcessMapper baseMapper;

    /**
     * 查询请假流程
     */
    @Override
    public SysProcessVo queryById(Long processId){
        return baseMapper.selectVoById(processId);
    }

    @Override
    public Long queryByTaskId(String taskId){

        SysProcessVo sysProcessVo = baseMapper.selectVoOne(new LambdaQueryWrapper<SysProcess>()
            .eq(SysProcess::getTaskId, taskId));
        return sysProcessVo.getProcessId();
    }

//    /**
//     * 查询请假流程id
//     */
//    @Override
//    public SysProcessVo queryByTaskId(Long taskId){
//        return baseMapper.selectVoById(taskId);
//    }


    /**
     * 查询请假流程列表
     */
    @Override
    public TableDataInfo<SysProcessVo> queryPageList(SysProcessBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysProcess> lqw = buildQueryWrapper(bo);
        Page<SysProcessVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询请假流程列表
     */
    @Override
    public List<SysProcessVo> queryList(SysProcessBo bo) {
        LambdaQueryWrapper<SysProcess> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysProcess> buildQueryWrapper(SysProcessBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysProcess> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getProcessType()), SysProcess::getProcessType, bo.getProcessType());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), SysProcess::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getApprover()), SysProcess::getApprover, bo.getApprover());
        lqw.eq(bo.getStartTime() != null, SysProcess::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, SysProcess::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getProcessHours()), SysProcess::getProcessHours, bo.getProcessHours());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysProcess::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增请假流程
     */
    @Override
    public Boolean insert(SysProcess add) {
        //SysProcess add = BeanUtil.toBean(bo, SysProcess.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            System.out.println(flag);
        }
        return flag;
    }

    /**
     * 修改请假流程
     */
    @Override
    public Boolean update(SysProcess update) {
        //SysProcess update = BeanUtil.toBean(bo, SysProcess.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysProcess entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除请假流程
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
