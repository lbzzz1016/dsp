package com.ruoyi.task.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.task.domain.Task;
import com.ruoyi.task.domain.TaskWorkflow;
import com.ruoyi.task.domain.TaskWorkflowRule;
import com.ruoyi.task.mapper.TaskWorkflowMapper;
import com.ruoyi.task.mapper.TaskWorkflowRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskWorkflowService extends ServiceImpl<TaskWorkflowMapper, TaskWorkflow> {

    @Autowired
    private TaskProjectService taskProjectService;
    @Autowired
    private TaskWorkflowRuleMapper taskWorkflowRuleMapper;
    @Autowired
    private TaskWorkflowRuleServiceImpl taskWorkflowRuleService;
//    @Autowired
//    private RedisCache redisCache;
    @Lazy
    @Autowired
    private TaskMemberService taskMemberService;

    //根据 项目编号查询taskWorkflow
    public List<Map> selectTaskWorkflowByProjectCode(String projectCode) {
        return baseMapper.selectTaskWorkflowByProjectCode(projectCode);
    }

    //根据 workflow编号查询taskWorkflowrule
    public List<Map> selectTaskWorkflowRuleByWorkflowCode(String workflowCode) {
        return taskWorkflowRuleMapper.selectTaskWorkflowRuleByWorkflowCode(workflowCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean save(String orgCode, String projectCode, String taskWorkflowName, JSONObject rules) {
        String flowCode = IdUtil.fastSimpleUUID();
        TaskWorkflow taskWorkflow = TaskWorkflow.builder().organizationCode(orgCode).projectCode(projectCode).code(flowCode)
                .name(taskWorkflowName).createTime(DateUtils.getTime()).updateTime(DateUtils.getTime()).build();
        boolean save = save(taskWorkflow);
        boolean saveRules = saveRules(flowCode, rules);
        //redisCache.deleteObject(Constants.PROJECTRULE + projectCode);
        log.info("任务流转保存{}，规则保存{}", save, saveRules);
        if (save && saveRules) {
            return true;
        } else {
            throw new CustomException("保存失败！");
        }
    }

    private boolean saveRules(String flowCode, JSONObject rules) {
        TaskWorkflowRule.TaskWorkflowRuleBuilder ruleBuild = TaskWorkflowRule.builder().workflowCode(flowCode)
                .createTime(DateUtils.getTime()).updateTime(DateUtils.getTime());
        String firstObj = (String) rules.get("firstObj");
        if (StrUtil.isNotEmpty(firstObj)) {
            List<TaskWorkflowRule> list = new ArrayList<>();
            TaskWorkflowRule rule01 = ruleBuild.code(IdUtil.fastSimpleUUID()).sort(1).type(0).action(0).objectCode(firstObj).build();
            list.add(rule01);
            JSONObject object02 = rules.getJSONObject("firstAction");
            buildRule(list, object02, ruleBuild, 2, 0);
            JSONObject object03 = rules.getJSONObject("firstResult");
            buildRule(list, object03, ruleBuild, 3, 0);
            JSONObject object04 = rules.getJSONObject("lastResult");
            boolean rule04 = buildRule(list, object04, ruleBuild, 4, 0);
            JSONObject object05 = rules.getJSONObject("state");
            if (rule04) {
                buildRule(list, object05, ruleBuild, 5, 1);
            } else {
                buildRule(list, object05, ruleBuild, 4, 1);
            }
            boolean saveBatch = taskWorkflowRuleService.saveBatch(list);
            log.info("流转g规则保存{}", saveBatch);
            if (!saveBatch) {
                throw new CustomException("保存失败！");
            }
            return true;
        } else {
            throw new CustomException("参数错误！");
        }
    }

    private boolean buildRule(List<TaskWorkflowRule> list, JSONObject object, TaskWorkflowRule.TaskWorkflowRuleBuilder ruleBuild, int sort, int flag) {
        Integer action = (Integer) object.get("action");
        TaskWorkflowRule build;
        if (flag == 1) {
            Integer val = (Integer) object.get("value");
            build = ruleBuild.code(IdUtil.fastSimpleUUID()).type(3).action(val).objectCode(null).sort(sort).build();
        } else {
            if (action == 3) {
                ruleBuild.type(1);
            } else {
                ruleBuild.type(0);
            }
            String val = (String) object.get("value");
            if (sort == 4) {
                if (StrUtil.isEmpty(val)) {
                    return false;
                }
            }
            build = ruleBuild.code(IdUtil.fastSimpleUUID()).action(action).objectCode(val).sort(sort).build();
        }
        return list.add(build);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(String taskWorkflowCode, String taskWorkflowName, JSONObject rules) {
        boolean update = lambdaUpdate().set(TaskWorkflow::getName, taskWorkflowName).set(TaskWorkflow::getUpdateTime, DateUtils.getTime())
                .eq(TaskWorkflow::getCode, taskWorkflowCode).update();
        boolean remove = taskWorkflowRuleService.remove(Wrappers.<TaskWorkflowRule>lambdaQuery().eq(TaskWorkflowRule::getWorkflowCode, taskWorkflowCode));
        boolean saveRules = saveRules(taskWorkflowCode, rules);
        TaskWorkflow one = lambdaQuery().select(TaskWorkflow::getProjectCode).eq(TaskWorkflow::getCode, taskWorkflowCode).one();
        if (one != null) {
            //redisCache.deleteObject(Constants.PROJECTRULE + one.getProject_code());
        }
        log.info("任务流转修改{}，规则移除{}, 规则保存{}", update, remove, saveRules);
        if (update && remove && saveRules) {
            return true;
        } else {
            throw new CustomException("修改失败!");
        }
    }

    public boolean delete(String taskWorkflowCode) {
        boolean remove = remove(Wrappers.<TaskWorkflow>lambdaQuery().eq(TaskWorkflow::getCode, taskWorkflowCode));
        boolean removeRule = taskWorkflowRuleService.remove(Wrappers.<TaskWorkflowRule>lambdaQuery().eq(TaskWorkflowRule::getWorkflowCode, taskWorkflowCode));
        TaskWorkflow one = lambdaQuery().select(TaskWorkflow::getProjectCode).eq(TaskWorkflow::getCode, taskWorkflowCode).one();
        if (one != null) {
            //redisCache.deleteObject(Constants.PROJECTRULE + one.getProject_code());
        }
        log.info("任务移除{}，规则移除{}", remove, removeRule);
        if (remove && removeRule) {
            return true;
        } else {
            throw new CustomException("删除失败!");
        }
    }

    public void queryRule(String projectCode, String stageCode, String taskCode, String memberCode, Integer action) {
        //此项目的所有规则
        List<TaskWorkflow> workflowList = getTaskWorkFlow(projectCode);
        //找到符合条件的规则
        List<TaskWorkflow> theWorkflow = getTheWokFlow(workflowList, stageCode, memberCode, action);
        //遍历规则，做出任务修改
        LambdaUpdateWrapper<Task> taskWrapper = Wrappers.<Task>lambdaUpdate().eq(Task::getCode, taskCode);
        if (theWorkflow != null) {
            theWorkflow.forEach(o -> {
                List<TaskWorkflowRule> ruleList = o.getWorkflowRuleList();
                TaskWorkflowRule rule03 = ruleList.stream().filter(o1 -> o1.getSort() == 3).findFirst().orElse(null);
                if (rule03 != null && rule03.getAction() == 3) {
                    taskWrapper.set(Task::getAssignTo, rule03.getObjectCode());
                    if (StrUtil.isNotEmpty(rule03.getObjectCode())) {
                    	//updateTaskMember(taskCode, rule03.getObject_code());
                    }
                } else if (rule03 != null) {
                    taskWrapper.set(Task::getStageCode, rule03.getObjectCode());
                }
                TaskWorkflowRule rule04 = ruleList.stream().filter(o1 -> o1.getSort() == 4).findFirst().orElse(null);
                TaskWorkflowRule rule05 = ruleList.stream().filter(o1 -> o1.getSort() == 5).findFirst().orElse(null);
                if (rule03 != null && rule04 != null && StrUtil.isNotEmpty(rule04.getObjectCode())) {
                    if (rule03.getAction() == 3) {
                        taskWrapper.set(Task::getStageCode, rule04.getObjectCode());
                    }
                    if (rule03.getAction() == 0) {
                        taskWrapper.set(Task::getAssignTo, rule04.getObjectCode());
                        if (StrUtil.isNotEmpty(rule04.getObjectCode())) {
                        	//updateTaskMember(taskCode, rule04.getObject_code());
                        }
                    }
                } else if (rule04 != null && StrUtil.isEmpty(rule04.getObjectCode())) {
                    rule05 = rule04;
                }
                if (rule05 != null) {
                    if (rule05.getAction() == 1) {
                        taskWrapper.set(Task::getDone, 1);
                    }
                    if (rule05.getAction() == 2) {
                        taskWrapper.set(Task::getDone, 0);
                    }
                }
                boolean update = taskProjectService.update(taskWrapper);
                log.info("根据流转规则修改任务{}", update);
            });
        }
    }
//    private void updateTaskMember(String taskCode, String memberCode) {
//            TaskMember one = taskMemberService.lambdaQuery().eq(TaskMember::getTask_code, taskCode).eq(TaskMember::getMember_code, memberCode).one();
//            boolean update = taskMemberService.lambdaUpdate().set(TaskMember::getIs_executor, 0).eq(TaskMember::getTask_code, taskCode).update();
//            if (one == null) {
//               TaskMember build = TaskMember.builder().task_code(taskCode).member_code(memberCode).is_executor(1).join_time(DateUtils.getTime()).build();
//                boolean save = taskMemberService.save(build);
//                log.info("任务成员修改{}，任务成功新增{}", update, save);
//           } else {
//                boolean update1 = taskMemberService.lambdaUpdate().set(TaskMember::getIs_executor, 1).eq(TaskMember::getTask_code, taskCode).eq(TaskMember::getMember_code, memberCode).update();
//                log.info("任务成员修改{}，成员修改{}", update, update1);
//            }
//    }

    private List<TaskWorkflow> getTheWokFlow(List<TaskWorkflow> workflowList, String stageCode, String memberCode, Integer action) {
        List<String> flowCode = workflowList.stream().filter(o -> {
            List<TaskWorkflowRule> workflowRuleList = o.getWorkflowRuleList();
            boolean flag = false;
            for (TaskWorkflowRule rule : workflowRuleList) {
                if (rule.getSort() == 1) {
                    if (StrUtil.equals(rule.getObjectCode(), stageCode)) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (rule.getSort() == 2) {
                    if (action.equals(rule.getAction())) {
                        if (action == 3) {
                            if (StrUtil.equals(memberCode, rule.getObjectCode())) {
                                flag = true;
                            } else {
                                flag = false;
                                break;
                            }
                        } else {
                            flag = true;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                }
            }
            return flag;
        }).map(TaskWorkflow::getCode).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(flowCode)) {
            return workflowList.parallelStream().filter(o -> flowCode.contains(o.getCode())).collect(Collectors.toList());
        }
        return null;
    }

    private List<TaskWorkflow> getTaskWorkFlow(String projectCode) {
        List<TaskWorkflow> list = new ArrayList<>();//redisCache.getCacheObject(Constants.PROJECTRULE + projectCode);
        if (CollUtil.isEmpty(list)) {
            list = lambdaQuery().eq(TaskWorkflow::getProjectCode, projectCode).list();
            if (CollUtil.isNotEmpty(list)) {
                List<String> codeList = list.parallelStream().map(TaskWorkflow::getCode).collect(Collectors.toList());
                List<TaskWorkflowRule> rules = taskWorkflowRuleService.lambdaQuery().in(TaskWorkflowRule::getWorkflowCode, codeList).list();
                if (CollUtil.isNotEmpty(rules)) {
                    Map<String, List<TaskWorkflowRule>> collect = rules.stream().collect(Collectors.groupingBy(TaskWorkflowRule::getWorkflowCode));
                    list.forEach(o -> {
                        o.setWorkflowRuleList(collect.get(o.getCode()));
                    });
                }
            }
            //redisCache.setCacheObject(Constants.PROJECTRULE + projectCode, list, 60, TimeUnits.MINUTES);
        }
        return list;
    }
}
