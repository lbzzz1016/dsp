package com.ruoyi.project.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.project.domain.ProjectInfo;
import com.ruoyi.project.mapper.ProjectInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service

public class ProjectInfoService  extends ServiceImpl<ProjectInfoMapper, ProjectInfo> {

    public List<Map> getProjectInfoByProjectCode(String projectCode){
        return baseMapper.selectProjectInfoByProjectCode(projectCode);
    }
}
