package com.ruoyi.task.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.member.domain.ProjectMember;
import com.ruoyi.member.service.ProjectMemberService;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.ProjectService;
import com.ruoyi.task.domain.Task;
import com.ruoyi.task.domain.TaskMember;
import com.ruoyi.task.mapper.TaskMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskMemberService  extends ServiceImpl<TaskMemberMapper, TaskMember> {

    @Autowired
    TaskProjectService taskProjectService;
    @Autowired
    ProjectMemberService projectMemberService;
    @Autowired
    ProjectService projectService;
    @Autowired
    TaskWorkflowService taskWorkflowService;
    
    public IPage<Map> getTaskMemberByTaskCode(IPage iPage, String taskCode){
        return baseMapper.selectTaskMemberByTaskCode(iPage,taskCode);
    }

    @Transactional
    public TaskMember inviteMember(String memberCode,String taskCode,Integer isExecutor,Integer isOwner,boolean fromCreate ,boolean isRobot){
        memberCode = StringUtils.isEmpty(memberCode)?"":memberCode;
        Task task = taskProjectService.lambdaQuery().eq(Task::getCode,taskCode).eq(Task::getDeleted,0).one();
        if(ObjectUtils.isEmpty(task)){
            throw new CustomException("任务已失效！");
        }
        TaskMember taskExecutor = lambdaQuery().eq(TaskMember::getIsExecutor,1).eq(TaskMember::getTaskCode,taskCode).one();
        if(null != taskExecutor && taskExecutor.getMemberCode().equals(memberCode)){
            return new TaskMember();
        }
        if(isExecutor>0){
            lambdaUpdate().set(TaskMember::getIsExecutor,0).eq(TaskMember::getTaskCode,taskCode).update();
        }
        if(StringUtils.isNotEmpty(memberCode)){
            TaskMember hasJoined = lambdaQuery().eq(TaskMember::getMemberCode,memberCode).eq(TaskMember::getTaskCode,taskCode).one();
            if(!ObjectUtils.isEmpty(hasJoined)){
                taskProjectService.lambdaUpdate().set(Task::getAssignTo,memberCode).eq(Task::getCode,taskCode).update();
                taskWorkflowService.queryRule(task.getProjectCode(), task.getStageCode(), task.getCode(), memberCode, 3);
                
                lambdaUpdate().set(TaskMember::getIsExecutor,1).eq(TaskMember::getTaskCode,taskCode).eq(TaskMember::getMemberCode,memberCode).update();
                String logType ="assign";
                if(LoginHelper.getLoginUser().getCode().equals(memberCode)){
                    logType="claim";
                }
                taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,logType,memberCode,0,
                        "","","",new HashMap(){{
                            put("is_robot",isRobot);
                        }},null);
                return new TaskMember();
            }
        }
        if(StringUtils.isEmpty(memberCode)){
            taskProjectService.lambdaUpdate().set(Task::getAssignTo,memberCode).eq(Task::getCode,taskCode).update();
            if(!fromCreate){
                if(ObjectUtil.isNotEmpty(taskExecutor)){
                    taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,"removeExecutor",taskExecutor.getMemberCode(),0,
                            "","","",new HashMap(){{
                                put("is_robot",isRobot);
                            }},null);
                }
            }
            return new TaskMember();
        }
        TaskMember taskMember = TaskMember.builder().memberCode(memberCode).taskCode(taskCode).
                isExecutor(isExecutor).isOwner(isOwner).joinTime(DateUtils.getTime()).build();
        save(taskMember);
        if(isExecutor>0){
            taskProjectService.lambdaUpdate().eq(Task::getCode,taskCode).set(Task::getAssignTo,memberCode).update();
            if(LoginHelper.getLoginUser().getCode().equals(memberCode)){
                taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,"claim","",0,
                        "","","",new HashMap(){{
                            put("is_robot",isRobot);
                        }},null);
            }else{
                taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,"claim",memberCode,0,
                        "","","",new HashMap(){{
                            put("is_robot",isRobot);
                        }},null);
            }
        }
        if(StringUtils.isNotEmpty(memberCode)){
            Project project = projectService.lambdaQuery().eq(Project::getCode,task.getProjectCode()).one();
            projectMemberService.inviteMember(memberCode,project==null?"":project.getCode(),0);
        }
        return taskMember;
    }


    @Transactional
    public  void inviteMemberBatch(String memberCodes,String taskCode){
        Task task = taskProjectService.lambdaQuery().eq(Task::getCode,taskCode).one();
        if(ObjectUtils.isEmpty(task)){
            throw new CustomException("该任务已失效！");
        }
        boolean isAll = false;
        JSONArray memberCodeArray = JSONUtil.parseArray(memberCodes);
        //List<>
        List<String> memberCodesList = new ArrayList<>();
        if(memberCodes.indexOf("all") != -1){
            isAll = true;
            List<ProjectMember> list= projectMemberService.lambdaQuery().eq(ProjectMember::getProjectCode,task.getProjectCode()).list();
            if(CollectionUtil.isNotEmpty(list)){
                list.forEach(projectMember -> {
                    memberCodesList.add(projectMember.getMemberCode());
                });
            }
        }else{
            if(memberCodeArray != null && memberCodeArray.size() != 0) {
                for (Object obj : memberCodeArray) {
                    if(ObjectUtil.isNotEmpty(obj)){
                        memberCodesList.add(String.valueOf(obj));
                    }
                }
            }
        }
        TaskMember taskMember = lambdaQuery().eq(TaskMember::getIsOwner,1)
                .eq(TaskMember::getTaskCode,taskCode).one();
        boolean finalIsAll = isAll;
        memberCodesList.forEach(memberCode ->{
            if(!memberCode.equals(taskMember.getMemberCode())){
                TaskMember hasJoined = lambdaQuery().eq(TaskMember::getMemberCode,memberCode)
                        .eq(TaskMember::getTaskCode,taskCode).one();
                if(ObjectUtil.isNotEmpty(hasJoined)){
                    if(!finalIsAll){
                        if(hasJoined.getIsExecutor()>0){
                            taskProjectService.lambdaUpdate().eq(Task::getCode,taskCode).set(Task::getAssignTo,"").update();
                            taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,"removeExecutor",memberCode,0,
                                    "","","",null,null);
                        }
                        lambdaUpdate().eq(TaskMember::getTaskCode,taskCode).eq(TaskMember::getMemberCode,memberCode).remove();
                        taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,"removeMember",memberCode,0,
                                "","","",null,null);
                    }
                }else{
                    TaskMember saveTaskMember = TaskMember.builder().memberCode(memberCode)
                            .taskCode(taskCode).isExecutor(0).joinTime(DateUtils.getTime()).build();
                    save(saveTaskMember);
                    taskProjectService.taskHook(LoginHelper.getLoginUser().getCode(),taskCode,"inviteMember",memberCode,0,
                            "","","",null,null);
                }
            }
        });
    }
}
