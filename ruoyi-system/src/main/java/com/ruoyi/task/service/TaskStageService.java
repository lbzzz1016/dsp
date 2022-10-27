package com.ruoyi.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.task.domain.Task;
import com.ruoyi.task.domain.TaskStage;
import com.ruoyi.task.mapper.TaskStageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Service
public class TaskStageService  extends ServiceImpl<TaskStageMapper, TaskStage> {

    @Autowired
    TaskProjectService taskProjectService;
    //根据 项目编号查询taskStage
    public List<Map> selectTaskStageByProjectCode(String projectCode){
        return baseMapper.selectTaskStageByProjectCode(projectCode);
    }

    //根据 项目编号查询taskStage
    public IPage<TaskStage> selectTaskStageByProjectCode(IPage ipage, Map params){
        return baseMapper.selectTaskStageByProjectCodeForPage(ipage,params);
    }

    public TaskStage getTaskStageByCode(String code){
        LambdaQueryWrapper<TaskStage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskStage::getCode, code);
        return baseMapper.selectOne(queryWrapper);
    }

    public void deleteStage(String code){
        TaskStage taskStage = lambdaQuery().eq(TaskStage::getCode,code).one();
        if(ObjectUtils.isEmpty(taskStage)){
            throw new CustomException("该列表不存在！");
        }
        List<Task> tasks = taskProjectService.lambdaQuery().eq(Task::getStageCode,code).eq(Task::getDeleted,0).list();
        if(!CollectionUtils.isEmpty(tasks)){
            throw new CustomException("请先清空此列表上的任务，然后再删除这个列表！");
        }
        lambdaUpdate().eq(TaskStage::getCode,code).remove();
    }

}
