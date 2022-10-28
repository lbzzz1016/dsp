package com.ruoyi.project.service;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.project.domain.CollectionTeam;
import com.ruoyi.project.mapper.CollectionTeamMapper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CollectionTeamService extends ServiceImpl<CollectionTeamMapper, CollectionTeam> {

    public void starTask(String sourceCode,String memberCode,Integer star){
        Map collectionMap = baseMapper.selectCollection(sourceCode,memberCode);
        if(star>0 && MapUtils.isEmpty(collectionMap)){
            save(CollectionTeam.builder().createTime(DateUtils.getTime())
            .code(CommUtils.getUUID()).sourceCode(sourceCode).type("task").memberCode(memberCode)
            .build());
            return ;
        }
        if(star==0){
            LambdaUpdateWrapper<CollectionTeam> collUQ = new LambdaUpdateWrapper<CollectionTeam>();
            collUQ.eq(CollectionTeam::getSourceCode,sourceCode);
            collUQ.eq(CollectionTeam::getType,"task");
            collUQ.eq(CollectionTeam::getMemberCode,memberCode);
            baseMapper.delete(collUQ);
        }
    }
}
