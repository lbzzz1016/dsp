package com.ruoyi.project.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.project.domain.ProjectFeatures;
import com.ruoyi.project.mapper.ProjectFeaturesMapper;
import com.ruoyi.task.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProjectFeaturesService  extends ServiceImpl<ProjectFeaturesMapper, ProjectFeatures> {

    @Autowired
    private TaskMapper taskMapper;
    public List<Map> getProjectFeaturesByProjectCode(String projectCode){
        return baseMapper.selectProjectFeaturesByProjectCode(projectCode);
    }

    public Map getProjectFeaturesByCode(String code){
        return baseMapper.selectProjectFeaturesByCode(code);
    }

    public Map getProjectFeaturesOneByNameAndProjectCode(String name,String projectCode){
        return baseMapper.selectProjectFeaturesOneByNameAndProjectCode(name,projectCode);
    }
    @Transactional
    public Integer delProjectFeaturesAndTask(String code){
        Integer i1= baseMapper.deleteProjectFeaturesByCode(code);
        Integer i2 = taskMapper.updateTaskFeaAndVerByFeaCode(code);
        return i1+i2;
    }
}
