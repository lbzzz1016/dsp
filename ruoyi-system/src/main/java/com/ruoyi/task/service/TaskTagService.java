package com.ruoyi.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.mapper.CommMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.member.domain.Member;
import com.ruoyi.member.service.MemberService;
import com.ruoyi.task.domain.TaskTag;
import com.ruoyi.task.mapper.TaskTagMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class  TaskTagService  extends ServiceImpl<TaskTagMapper, TaskTag> {

    @Autowired
    CommMapper commMapper;
    public List<Map> getTaskTagByProjectCode(String projectCode){
        return baseMapper.selectTaskTagByProjectCode(projectCode);
    }

    public Map getTaskTagByCode(String code){
        return baseMapper.selectTaskTagByCode(code);
    }

    public  Map getTaskTagByNameAndProjectCode(Map params){
        return  baseMapper.selectTaskTagByNameAndProjectCode(params);
    }

    @Autowired
    TaskProjectService taskProjectService;
    @Autowired
    MemberService memberService;

    public IPage<Map> selectListByTaskTag(IPage<Map> page, String taskTagCode){
        String sql = String.format("select t.* from team_task_to_tag as tt join team_task as t on tt.task_code = t.code where tt.tag_code = '%s' order by t.id desc",taskTagCode);
        page = commMapper.customQueryItem(page,sql);
        List<Map> record = page.getRecords();
        List<Map> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(record)){
            record.stream().forEach(map -> {
                String taskCode = MapUtils.getString(map,"code");
                map.put("tags", taskProjectService.getTagsAttr(taskCode));
                map.put("executor",null);
                String assign_to = MapUtils.getString(map,"assign_to");
                if(StringUtils.isNotEmpty(assign_to)){
                    Member member = memberService.getMemberByCode(MapUtils.getString(map,"assign_to"));
                    map.put("executor",member);
                }
                result.add(map);
            });
        }
        page.setRecords(result);
        return page;
    }

}
