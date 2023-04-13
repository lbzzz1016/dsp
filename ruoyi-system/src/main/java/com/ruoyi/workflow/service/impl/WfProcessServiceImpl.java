package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.helper.ProcessHoursHelper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.common.constant.TaskConstants;
import com.ruoyi.flowable.core.FormConf;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.utils.ModelUtils;
import com.ruoyi.flowable.utils.ProcessFormUtils;
import com.ruoyi.flowable.utils.TaskUtils;
import com.ruoyi.system.domain.SysClockIn;
import com.ruoyi.system.domain.SysProcess;
import com.ruoyi.system.domain.bo.SysProcessBo;
import com.ruoyi.system.service.*;
import com.ruoyi.workflow.domain.WfDeployForm;
import com.ruoyi.workflow.domain.bo.WfProcessBo;
import com.ruoyi.workflow.domain.vo.WfDefinitionVo;
import com.ruoyi.workflow.domain.vo.WfDeployFormVo;
import com.ruoyi.workflow.domain.vo.WfDetailVo;
import com.ruoyi.workflow.domain.vo.WfTaskVo;
import com.ruoyi.workflow.mapper.WfDeployFormMapper;
import com.ruoyi.workflow.service.IWfProcessService;
import com.ruoyi.workflow.service.IWfTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Soundbank;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author KonBAI
 * @createTime 2022/3/24 18:57
 */
@RequiredArgsConstructor
@Service
public class WfProcessServiceImpl extends FlowServiceFactory implements IWfProcessService {

    private static final String PROCESS_STATUS_NOT_PASS = "未通过";

    private final IWfTaskService wfTaskService;
    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysDeptService deptService;
    private final WfDeployFormMapper deployFormMapper;
    private final ISysProcessService processService;
    private final ISysClockInService clockInService;

    /**
     * 流程定义列表
     *
     * @param pageQuery 分页参数
     * @return 流程定义分页列表数据
     */
    @Override
    public TableDataInfo<WfDefinitionVo> processList(PageQuery pageQuery) {
        Page<WfDefinitionVo> page = new Page<>();
        // 流程定义列表数据查询
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
            .latestVersion()
            .active()
            .orderByProcessDefinitionKey()
            .asc();
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> definitionList = processDefinitionQuery.listPage(offset, pageQuery.getPageSize());

        List<WfDefinitionVo> definitionVoList = new ArrayList<>();
        for (ProcessDefinition processDefinition : definitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfDefinitionVo vo = new WfDefinitionVo();
            vo.setDefinitionId(processDefinition.getId());
            vo.setProcessKey(processDefinition.getKey());
            vo.setProcessName(processDefinition.getName());
            vo.setVersion(processDefinition.getVersion());
            vo.setDeploymentId(processDefinition.getDeploymentId());
            vo.setSuspended(processDefinition.isSuspended());
            // 流程定义时间
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            definitionVoList.add(vo);
        }
        page.setRecords(definitionVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    @Override
    public String selectFormContent(String definitionId, String deployId) {
        InputStream inputStream = repositoryService.getProcessModel(definitionId);
        String bpmnString;
        try {
            bpmnString = IoUtil.readUtf8(inputStream);
        } catch (IORuntimeException exception) {
            throw new RuntimeException("获取流程设计失败！");
        }
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnString);
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        WfDeployFormVo deployFormVo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>()
            .eq(WfDeployForm::getDeployId, deployId)
            .eq(WfDeployForm::getFormKey, startEvent.getFormKey())
            .eq(WfDeployForm::getNodeKey, startEvent.getId()));
        return deployFormVo.getContent();
    }

    /**
     * 根据流程定义ID启动流程实例,记录入库
     *
     * @param procDefId 流程定义Id
     * @param variables 流程变量
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcess(String procDefId, Map<String, Object> variables) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(procDefId).singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                throw new ServiceException("流程已被挂起，请先激活流程");
            }
            // 设置流程发起人Id到流程中
            this.buildProcessVariables(variables);
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
            // 给第一步申请人节点设置任务执行人和意见 todo:第一个节点不设置为申请人节点有点问题？
            wfTaskService.startFirstTask(processInstance, variables);
            if (procDefId.contains("Process_1662088485683")) {
                //将请假信息入库
                SysProcess sysProcess = buildProcessPram(processInstance, variables);
                processService.insert(sysProcess);
            } else if (procDefId.contains("Process_1681347812947")) {
                //将补卡信息入库
                SysClockIn sysClockIn = buildClockPram(processInstance, variables);
                clockInService.insert(sysClockIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("流程启动错误");
        }
    }

    /**
     * 请假流程参数构建
     * @param variables 请假流程参数入库
     */
    private SysProcess buildProcessPram(ProcessInstance processInstance, Map<String, Object> variables) {
        //获取userId
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getNickName();

        ArrayList<String> time = (ArrayList<String>)variables.get("time");
        String startTimeStr = time.get(0);
        String endTimeStr = time.get(1);
        Date startTime = DateUtils.parseStringToDate(startTimeStr);
        Date endTime = DateUtils.parseStringToDate(endTimeStr);
        double hours = ProcessHoursHelper.countHours(startTime, endTime);
        SysProcess sysProcess = SysProcess.builder()
                                .taskId(processInstance.getProcessInstanceId())
                                .processCtime(processInstance.getStartTime())
                                .processDept((String)variables.get("dept"))
                                .processType((String) variables.get("type"))
                                .processReason((String) variables.get("reason"))
                                .status(ProcessStatus.INAPPROVAL.getInfo())
                                .userId(userId)
                                .userName(userName)
                                .startTime(startTime)
                                .endTime(endTime)
                                .processHours(String.valueOf(hours))
                                .build();
        return sysProcess;
    }

    /**
     * 补卡流程参数构建
     * @param variables 补卡流程参数入库
     */
    private SysClockIn buildClockPram(ProcessInstance processInstance, Map<String, Object> variables) {
        //获取userId
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getNickName();

        String clockDateStr = (String)variables.get("date");
        Date clockDate = DateUtils.parseStringToDate(clockDateStr, DateUtils.YYYY_MM_DD);
        SysClockIn sysClockIn = SysClockIn.builder()
            .taskId(processInstance.getProcessInstanceId())
            .processCtime(processInstance.getStartTime())
            .userId(userId)
            .userName(userName)
            .userNumber((String) variables.get("num"))
            .reason((String) variables.get("reason"))
            .checkDate(clockDate)
            .checkTime((String) variables.get("model"))
            .status(ProcessStatus.INAPPROVAL.getInfo())
            .build();
        return sysClockIn;
    }

    /**
     * 通过DefinitionKey启动流程
     * @param procDefKey 流程定义Key
     * @param variables 扩展参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcessByDefKey(String procDefKey, Map<String, Object> variables) {
        try {
            if (StringUtils.isNoneBlank(procDefKey)) {
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(procDefKey).latestVersion().singleResult();
                if (processDefinition != null && processDefinition.isSuspended()) {
                    throw new ServiceException("流程已被挂起，请先激活流程");
                }
                this.buildProcessVariables(variables);
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDefKey, variables);
                wfTaskService.startFirstTask(processInstance, variables);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("流程启动错误");
        }
    }

    /**
     * 流程详情信息
     *
     * @param procInsId 流程实例ID
     * @param deployId 流程部署ID
     * @param taskId 任务ID
     * @return
     */
    @Override
    public WfDetailVo queryProcessDetail(String procInsId, String deployId, String taskId) {
        WfDetailVo detailVo = new WfDetailVo();
        HistoricTaskInstance taskIns = historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId)
            .includeIdentityLinks()
            .includeProcessVariables()
            .includeTaskLocalVariables()
            .singleResult();
        if (taskIns == null) {
            throw new ServiceException("没有可办理的任务！");
        }
        detailVo.setTaskFormData(currTaskFormData(deployId, taskIns));
        detailVo.setHistoryTaskList(historyTaskList(procInsId));
        detailVo.setProcessFormList(processFormList(procInsId, deployId, taskIns));
        return detailVo;
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageOwnProcessList(PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
            .startedBy(userId.toString())
            .orderByProcessInstanceStartTime()
            .desc();
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery
            .listPage(offset, pageQuery.getPageSize());
        page.setTotal(historicProcessInstanceQuery.count());
        List<WfTaskVo> taskVoList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            WfTaskVo taskVo = new WfTaskVo();
            taskVo.setCreateTime(hisIns.getStartTime());
            taskVo.setFinishTime(hisIns.getEndTime());
            taskVo.setProcInsId(hisIns.getId());

            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                taskVo.setDuration(DateUtils.getDatePoor(hisIns.getEndTime(), hisIns.getStartTime()));
            } else {
                taskVo.setDuration(DateUtils.getDatePoor(DateUtils.getNowDate(), hisIns.getStartTime()));
            }
            // 流程部署实例信息
            Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(hisIns.getDeploymentId()).singleResult();
            taskVo.setDeployId(hisIns.getDeploymentId());
            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            taskVo.setProcDefName(hisIns.getProcessDefinitionName());
            taskVo.setProcDefVersion(hisIns.getProcessDefinitionVersion());
            taskVo.setCategory(deployment.getCategory());
            // 当前所处流程
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollUtil.isNotEmpty(taskList)) {
                taskVo.setTaskId(taskList.get(0).getId());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                taskVo.setTaskId(historicTaskInstance.get(0).getId());
            }
            taskVoList.add(taskVo);
        }
        page.setRecords(taskVoList);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageTodoProcessList(PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        TaskQuery taskQuery = taskService.createTaskQuery()
            .active()
            .includeProcessVariables()
            .taskCandidateOrAssigned(userId.toString())
            .taskCandidateGroupIn(TaskUtils.getCandidateGroup())
            .orderByTaskCreateTime().desc();
        page.setTotal(taskQuery.count());
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
            SysUser startUser = userService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());

            // 流程变量
            flowTask.setProcVars(this.getProcessVariables(task.getId()));

            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageClaimProcessList(WfProcessBo processBo, PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        TaskQuery taskQuery = taskService.createTaskQuery()
            .active()
            .includeProcessVariables()
            .taskCandidateUser(userId.toString())
            .taskCandidateGroupIn(TaskUtils.getCandidateGroup())
            .orderByTaskCreateTime().desc();
        if (StringUtils.isNotBlank(processBo.getProcessName())) {
            taskQuery.processDefinitionNameLike("%" + processBo.getProcessName() + "%");
        }
        page.setTotal(taskQuery.count());
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
            SysUser startUser = userService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());

            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageFinishedProcessList(PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
            .includeProcessVariables()
            .finished()
            .taskAssignee(userId.toString())
            .orderByHistoricTaskInstanceEndTime()
            .desc();
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            WfTaskVo flowTask = new WfTaskVo();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(DateUtil.formatBetween(histTask.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(histTask.getProcessDefinitionId())
                .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(histTask.getProcessInstanceId())
                .singleResult();
            SysUser startUser = userService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());

            // 流程变量
            flowTask.setProcVars(this.getProcessVariables(histTask.getId()));

            hisTaskList.add(flowTask);
        }
        page.setTotal(taskInstanceQuery.count());
        page.setRecords(hisTaskList);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result",page);
//        result.put("finished",true);
        return TableDataInfo.build(page);
    }

    /**
     * 扩展参数构建
     * @param variables 扩展参数
     */
    private void buildProcessVariables(Map<String, Object> variables) {
        String userIdStr = LoginHelper.getUserId().toString();
        identityService.setAuthenticatedUserId(userIdStr);
        variables.put(TaskConstants.PROCESS_INITIATOR, userIdStr);
    }

    /**
     * 获取流程变量
     *
     * @param taskId 任务ID
     * @return 流程变量
     */
    private Map<String, Object> getProcessVariables(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
            .includeProcessVariables()
            .finished()
            .taskId(taskId)
            .singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        }
        return taskService.getVariables(taskId);
    }

    /**
     * 获取当前任务流程表单信息
     */
    private FormConf currTaskFormData(String deployId, HistoricTaskInstance taskIns) {
        WfDeployFormVo deployFormVo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>()
            .eq(WfDeployForm::getDeployId, deployId)
            .eq(WfDeployForm::getFormKey, taskIns.getFormKey())
            .eq(WfDeployForm::getNodeKey, taskIns.getTaskDefinitionKey()));
        if (ObjectUtil.isNotEmpty(deployFormVo)) {
            FormConf currTaskFormData = JsonUtils.parseObject(deployFormVo.getContent(), FormConf.class);
            if (null != currTaskFormData) {
                currTaskFormData.setFormBtns(false);
                ProcessFormUtils.fillFormData(currTaskFormData, taskIns.getTaskLocalVariables());
                return currTaskFormData;
            }
        }
        return null;
    }

    /**
     * 获取流程表单信息（不包括当前任务节点）
     */
    private List<FormConf> processFormList(String procInsId, String deployId, HistoricTaskInstance taskIns) {
        List<FormConf> procFormList = new ArrayList<>();
        HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).includeProcessVariables().singleResult();
        Process process = repositoryService.getBpmnModel(historicProcIns.getProcessDefinitionId()).getMainProcess();

        buildStartFormData(historicProcIns, process, deployId, procFormList);
        buildUserTaskFormData(procInsId, deployId, process, procFormList);
        return procFormList;
    }

    private void buildStartFormData(HistoricProcessInstance historicProcIns, Process process, String deployId, List<FormConf> procFormList) {
        procFormList = procFormList == null ? new ArrayList<>() : procFormList;
        HistoricActivityInstance startInstance = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(historicProcIns.getId())
            .activityId(historicProcIns.getStartActivityId())
            .singleResult();
        StartEvent startEvent = (StartEvent) process.getFlowElement(startInstance.getActivityId());
        WfDeployFormVo startFormInfo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>()
            .eq(WfDeployForm::getDeployId, deployId)
            .eq(WfDeployForm::getFormKey, startEvent.getFormKey())
            .eq(WfDeployForm::getNodeKey, startEvent.getId()));
        if (ObjectUtil.isNotNull(startFormInfo)) {
            FormConf formConf = JsonUtils.parseObject(startFormInfo.getContent(), FormConf.class);
            if (null != formConf) {
                formConf.setTitle(startEvent.getName());
                formConf.setDisabled(true);
                formConf.setFormBtns(false);
                ProcessFormUtils.fillFormData(formConf, historicProcIns.getProcessVariables());
                procFormList.add(formConf);
            }
        }
    }

    private void buildUserTaskFormData(String procInsId, String deployId, Process process, List<FormConf> procFormList) {
        procFormList = procFormList == null ? new ArrayList<>() : procFormList;
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(procInsId).finished()
            .activityType(BpmnXMLConstants.ELEMENT_TASK_USER)
            .orderByHistoricActivityInstanceStartTime().asc()
            .list();
        for (HistoricActivityInstance instanceItem : activityInstanceList) {
            UserTask userTask = (UserTask) process.getFlowElement(instanceItem.getActivityId(), true);
            String formKey = userTask.getFormKey();
            if (formKey == null) {
                continue;
            }
            // 查询任务节点参数，并转换成Map
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(procInsId)
                .taskId(instanceItem.getTaskId())
                .list()
                .stream()
                .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
            WfDeployFormVo deployFormVo = deployFormMapper.selectVoOne(new LambdaQueryWrapper<WfDeployForm>()
                .eq(WfDeployForm::getDeployId, deployId)
                .eq(WfDeployForm::getFormKey, formKey)
                .eq(WfDeployForm::getNodeKey, userTask.getId()));
            if (ObjectUtil.isNotNull(deployFormVo)) {
                FormConf formConf = JsonUtils.parseObject(deployFormVo.getContent(), FormConf.class);
                if (null != formConf) {
                    formConf.setTitle(userTask.getName());
                    formConf.setDisabled(true);
                    formConf.setFormBtns(false);
                    ProcessFormUtils.fillFormData(formConf, variables);
                    procFormList.add(formConf);
                }
            }
        }
    }

    /**
     * 获取历史任务信息列表
     */
    private List<WfTaskVo> historyTaskList(String procInsId) {
        List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(procInsId)
            .orderByHistoricTaskInstanceStartTime().desc()
            .list();
        List<Comment> commentList = taskService.getProcessInstanceComments(procInsId);
        List<WfTaskVo> taskVoList = new ArrayList<>(taskInstanceList.size());
        taskInstanceList.forEach(taskInstance -> {
            WfTaskVo taskVo = new WfTaskVo();
            taskVo.setProcDefId(taskInstance.getProcessDefinitionId());
            taskVo.setTaskId(taskInstance.getId());
            taskVo.setTaskDefKey(taskInstance.getTaskDefinitionKey());
            taskVo.setTaskName(taskInstance.getName());
            taskVo.setCreateTime(taskInstance.getStartTime());
            taskVo.setFinishTime(taskInstance.getEndTime());
            if (StringUtils.isNotBlank(taskInstance.getAssignee())) {
                SysUser user = userService.selectUserById(Long.parseLong(taskInstance.getAssignee()));
                taskVo.setAssigneeId(user.getUserId());
                taskVo.setAssigneeName(user.getNickName());
                taskVo.setDeptName(user.getDept().getDeptName());
            }
            // 展示审批人员
            List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(taskInstance.getId());
            StringBuilder stringBuilder = new StringBuilder();
            for (HistoricIdentityLink identityLink : linksForTask) {
                if ("candidate".equals(identityLink.getType())) {
                    if (StringUtils.isNotBlank(identityLink.getUserId())) {
                        SysUser user = userService.selectUserById(Long.parseLong(identityLink.getUserId()));
                        stringBuilder.append(user.getNickName()).append(",");
                    }
                    if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                        if (identityLink.getGroupId().startsWith(TaskConstants.ROLE_GROUP_PREFIX)) {
                            Long roleId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(), TaskConstants.ROLE_GROUP_PREFIX));
                            SysRole role = roleService.selectRoleById(roleId);
                            stringBuilder.append(role.getRoleName()).append(",");
                        } else if (identityLink.getGroupId().startsWith(TaskConstants.DEPT_GROUP_PREFIX)) {
                            Long deptId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(), TaskConstants.DEPT_GROUP_PREFIX));
                            SysDept dept = deptService.selectDeptById(deptId);
                            stringBuilder.append(dept.getDeptName()).append(",");
                        }
                    }
                }
            }
            if (StringUtils.isNotBlank(stringBuilder)) {
                taskVo.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
            }
            if (ObjectUtil.isNotNull(taskInstance.getDurationInMillis())) {
                taskVo.setDuration(DateUtil.formatBetween(taskInstance.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            }
            // 获取意见评论内容
            if (CollUtil.isNotEmpty(commentList)) {
                List<Comment> comments = new ArrayList<>();
                for (Comment comment : commentList) {
                    if (comment.getTaskId().equals(taskInstance.getId())) {
                        comments.add(comment);
                    }
                }
                taskVo.setCommentList(comments);
            }
            taskVoList.add(taskVo);
        });
        return taskVoList;
    }
}
