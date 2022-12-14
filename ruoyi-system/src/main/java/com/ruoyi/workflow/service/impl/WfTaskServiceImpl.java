package com.ruoyi.workflow.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.ProcessStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flowable.common.constant.ProcessConstants;
import com.ruoyi.flowable.common.constant.TaskConstants;
import com.ruoyi.flowable.common.enums.FlowComment;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.flow.CustomProcessDiagramGenerator;
import com.ruoyi.flowable.flow.FindNextNodeUtil;
import com.ruoyi.flowable.flow.FlowableUtils;
import com.ruoyi.flowable.utils.TaskUtils;
import com.ruoyi.system.domain.SysProcess;
import com.ruoyi.system.service.ISysProcessService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.workflow.domain.bo.WfTaskBo;
import com.ruoyi.workflow.domain.dto.WfNextDto;
import com.ruoyi.workflow.domain.vo.WfViewerVo;
import com.ruoyi.workflow.service.IWfCopyService;
import com.ruoyi.workflow.service.IWfTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author KonBAI
 * @createTime 2022/3/10 00:12
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WfTaskServiceImpl extends FlowServiceFactory implements IWfTaskService {

    private final ISysUserService sysUserService;

    private final ISysRoleService sysRoleService;

    private final IWfCopyService copyService;

    private final ISysProcessService processService;

    /**
     * ????????????
     *
     * @param taskBo ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void complete(WfTaskBo taskBo) {
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            throw new ServiceException("???????????????");
        }
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.addComment(taskBo.getTaskId(), taskBo.getProcInsId(), FlowComment.DELEGATE.getType(), taskBo.getComment());
            taskService.resolveTask(taskBo.getTaskId());
        } else {
            taskService.addComment(taskBo.getTaskId(), taskBo.getProcInsId(), FlowComment.NORMAL.getType(), taskBo.getComment());
            Long userId = LoginHelper.getUserId();
            taskService.setAssignee(taskBo.getTaskId(), userId.toString());
            if (ObjectUtil.isNotEmpty(taskBo.getVariables())) {
                taskService.complete(taskBo.getTaskId(), taskBo.getVariables(), true);
            } else {
                taskService.complete(taskBo.getTaskId());
            }
        }
        // ????????????????????????
        taskBo.setTaskName(task.getName());
        // ??????????????????
        if (!copyService.makeCopy(taskBo)) {
            throw new RuntimeException("??????????????????");
        }

        //??????????????????
        Long processId = processService.queryByTaskId(task.getProcessInstanceId());
        log.debug("this is processId : " + processId);
        String assignee = LoginHelper.getNickName();
        SysProcess sysProcess = SysProcess.builder()
            .processId(processId)
            .taskId(task.getProcessInstanceId())
            .status(ProcessStatus.AGREE.getInfo())
            .approver(assignee)
            .processEtime(DateUtils.getNowDate())
            .build();
        processService.update(sysProcess);
    }

    /**
     * ????????????
     *
     * @param bo
     */
    @Override
    public void taskReject(WfTaskBo bo) {
        // ???????????? task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("???????????????????????????");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("????????????????????????");
        }
        // ????????????????????????
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // ????????????????????????
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // ??????????????????????????????????????????
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // ??????????????????????????????
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // ?????????????????????
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // ??????????????????
                    source = flowElement;
                }
            }
        }

        // ???????????????????????????????????? targetIds
        // ???????????????????????????????????????????????????
        // ?????????????????????????????????????????????
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new RuntimeException("????????????????????????????????????????????????");
        }
        // ???????????? ID ????????? Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // ???????????????????????????????????????????????????
        List<String> lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicTaskInstanceList);
        // ????????????????????????????????????????????????????????????
        List<String> targetIds = new ArrayList<>();
        // ??????????????????????????????????????????????????????
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number++;
            }
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // number == 1??????????????????????????????
            // number == 2??????????????????????????????????????????????????????
            if (number == 2) {
                break;
            }
            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }


        // ?????????????????????????????????????????? currentIds
        // ???????????????????????????????????????????????????????????????????????????????????????????????????
        UserTask oneUserTask = parentUserTaskList.get(0);
        // ??????????????????????????????????????? Key???????????????????????????????????????????????????????????????????????????
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // ?????????????????????
        List<String> currentIds = new ArrayList<>();
        // ?????????????????????????????????????????? runTaskList ????????????????????????????????????
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));


        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new RuntimeException("??????????????????????????????????????????");
        }

        // ?????????????????????????????????????????????ID???????????????????????????
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // ??????????????????
        currentTaskIds.forEach(item -> taskService.addComment(item, task.getProcessInstanceId(), FlowComment.REJECT.getType(), bo.getComment()));

        try {
            // ???????????????????????? 1 ??????????????????????????????????????????????????????????????????????????????
            if (targetIds.size() > 1) {
                // 1 ??? ??????????????????currentIds ????????????(1)???targetIds ??????????????????(???)
                runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId()).
                    moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // ??????????????????????????????????????????????????????????????????????????????
            if (targetIds.size() == 1) {
                // 1 ??? 1 ??? ??? ??? 1 ?????????currentIds ??????????????????????????????(1??????)???targetIds.get(0) ??????????????????(1)
                runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("???????????????????????????????????????????????????");
        } catch (FlowableException e) {
            throw new RuntimeException("???????????????????????????");
        }
        // ????????????????????????
        bo.setTaskName(task.getName());
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("??????????????????");
        }

        //??????????????????
        Long processId = processService.queryByTaskId(task.getProcessInstanceId());
        log.debug("this is processId : " + processId);
        String assignee = LoginHelper.getNickName();
        SysProcess sysProcess = SysProcess.builder()
            .processId(processId)
            .taskId(task.getProcessInstanceId())
            .status(ProcessStatus.REJECT.getInfo())
            .approver(assignee)
            .processEtime(DateUtils.getNowDate())
            .build();
        processService.update(sysProcess);
    }

    /**
     * ????????????
     *
     * @param bo ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void taskReturn(WfTaskBo bo) {
        // ???????????? task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("???????????????????????????");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("????????????????????????");
        }
        // ????????????????????????
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // ????????????????????????
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // ??????????????????????????????????????????
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // ??????????????????????????????
        FlowElement source = null;
        // ???????????????????????????
        FlowElement target = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // ????????????????????????
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = flowElement;
                }
                // ?????????????????????
                if (flowElement.getId().equals(bo.getTargetKey())) {
                    target = flowElement;
                }
            }
        }

        // ???????????????????????????
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????
        Boolean isSequential = FlowableUtils.iteratorCheckSequentialReferTarget(source, bo.getTargetKey(), null, null);
        if (!isSequential) {
            throw new RuntimeException("????????????????????????????????????????????????????????????????????????");
        }


        // ??????????????????????????????????????? Key???????????????????????????????????????????????????????????????????????????
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // ?????????????????????
        List<String> currentIds = new ArrayList<>();
        // ?????????????????????????????????????????? runTaskList ????????????????????????????????????
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));

        // ?????????????????????????????????????????????ID???????????????????????????
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // ??????????????????
        for (String currentTaskId : currentTaskIds) {
            taskService.addComment(currentTaskId, task.getProcessInstanceId(), FlowComment.REBACK.getType(), bo.getComment());
        }

        try {
            // 1 ??? 1 ??? ??? ??? 1 ?????????currentIds ??????????????????????????????(1??????)???targetKey ??????????????????(1)
            runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdsToSingleActivityId(currentIds, bo.getTargetKey()).changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("???????????????????????????????????????????????????");
        } catch (FlowableException e) {
            throw new RuntimeException("???????????????????????????");
        }
        // ????????????????????????
        bo.setTaskName(task.getName());
        // ??????????????????
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("??????????????????");
        }
        //??????????????????
        Long processId = processService.queryByTaskId(task.getProcessInstanceId());
        log.debug("this is processId : " + processId);
        String assignee = LoginHelper.getNickName();
        SysProcess sysProcess = SysProcess.builder()
            .processId(processId)
            .taskId(task.getProcessInstanceId())
            .status(ProcessStatus.RETURN.getInfo())
            .approver(assignee)
            .processEtime(DateUtils.getNowDate())
            .build();
        processService.update(sysProcess);
    }


    /**
     * ??????????????????????????????
     *
     * @param bo
     * @return
     */
    @Override
    public List<UserTask> findReturnTaskList(WfTaskBo bo) {
        // ???????????? task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        // ????????????????????????
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // ??????????????????????????????????????????????????????
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        // ??????????????????????????????
        UserTask source = null;
        if (flowElements != null) {
            for (FlowElement flowElement : flowElements) {
                // ?????????????????????
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = (UserTask) flowElement;
                }
            }
        }
        // ???????????????????????????
        List<List<UserTask>> roads = FlowableUtils.findRoad(source, null, null, null);
        // ????????????????????????
        List<UserTask> userTaskList = new ArrayList<>();
        for (List<UserTask> road : roads) {
            if (userTaskList.size() == 0) {
                // ????????????????????????????????????
                userTaskList = road;
            } else {
                // ???????????????????????????????????????????????????
                userTaskList.retainAll(road);
            }
        }
        return userTaskList;
    }

    /**
     * ????????????
     *
     * @param bo ??????????????????
     */
    @Override
    public void deleteTask(WfTaskBo bo) {
        // todo ?????????????????????????????????????????? ???????????????????????????????????????????????????
        taskService.deleteTask(bo.getTaskId(), bo.getComment());
    }

    /**
     * ??????/????????????
     *
     * @param taskBo ??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(WfTaskBo taskBo) {
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            throw new ServiceException("???????????????");
        }
        taskService.claim(taskBo.getTaskId(), TaskUtils.getUserId());
    }

    /**
     * ????????????/????????????
     *
     * @param bo ??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unClaim(WfTaskBo bo) {
        taskService.unclaim(bo.getTaskId());
    }

    /**
     * ????????????
     *
     * @param bo ??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(WfTaskBo bo) {
        // ???????????? task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("?????????????????????");
        }
        StringBuilder commentBuilder = new StringBuilder(LoginHelper.getNickName())
            .append("->");
        SysUser user = sysUserService.selectUserById(Long.parseLong(bo.getUserId()));
        if (ObjectUtil.isNotNull(user)) {
            commentBuilder.append(user.getNickName());
        } else {
            commentBuilder.append(bo.getUserId());
        }
        if (StringUtils.isNotBlank(bo.getComment())) {
            commentBuilder.append(": ").append(bo.getComment());
        }
        // ??????????????????
        taskService.addComment(bo.getTaskId(), task.getProcessInstanceId(), FlowComment.DELEGATE.getType(), commentBuilder.toString());
        // ?????????????????????????????????
        taskService.setOwner(bo.getTaskId(), LoginHelper.getUserId().toString());
        // ????????????
        taskService.delegateTask(bo.getTaskId(), bo.getUserId());
        // ????????????????????????
        bo.setTaskName(task.getName());
        // ??????????????????
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("??????????????????");
        }
    }


    /**
     * ????????????
     *
     * @param bo ??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferTask(WfTaskBo bo) {
        // ???????????? task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("?????????????????????");
        }
        StringBuilder commentBuilder = new StringBuilder(LoginHelper.getNickName())
            .append("->");
        SysUser user = sysUserService.selectUserById(Long.parseLong(bo.getUserId()));
        if (ObjectUtil.isNotNull(user)) {
            commentBuilder.append(user.getNickName());
        } else {
            commentBuilder.append(bo.getUserId());
        }
        if (StringUtils.isNotBlank(bo.getComment())) {
            commentBuilder.append(": ").append(bo.getComment());
        }
        // ??????????????????
        taskService.addComment(bo.getTaskId(), task.getProcessInstanceId(), FlowComment.TRANSFER.getType(), commentBuilder.toString());
        // ?????????????????????????????????
        taskService.setOwner(bo.getTaskId(), LoginHelper.getUserId().toString());
        // ????????????
        taskService.setAssignee(bo.getTaskId(), bo.getUserId());
        // ????????????????????????
        bo.setTaskName(task.getName());
        // ??????????????????
        if (!copyService.makeCopy(bo)) {
            throw new RuntimeException("??????????????????");
        }
    }

    /**
     * ????????????
     *
     * @param bo
     * @return
     */
    @Override
    public void stopProcess(WfTaskBo bo) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).list();
        if (CollectionUtils.isEmpty(task)) {
            throw new RuntimeException("??????????????????????????????????????????????????????");
        }

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(bo.getProcInsId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {
                Authentication.setAuthenticatedUserId(LoginHelper.getUserId().toString());
//                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.STOP.getType(),
//                        StringUtils.isBlank(flowTaskVo.getComment()) ? "????????????" : flowTaskVo.getComment());
                // ????????????????????????????????????
                String endId = endNodes.get(0).getId();
                List<Execution> executions = runtimeService.createExecutionQuery()
                    .parentId(processInstance.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                // ??????????????????????????????
                runtimeService.createChangeActivityStateBuilder()
                    .moveExecutionsToSingleActivityId(executionIds, endId).changeState();
            }
        }
    }

    /**
     * ????????????  ??????????????????
     *
     * @param bo
     * @return
     */
    @Override
    public void revokeProcess(WfTaskBo bo) {
        Task task = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).singleResult();
        if (task == null) {
            throw new RuntimeException("????????????????????????????????????????????????");
        }

        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .orderByTaskCreateTime()
            .asc()
            .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        for (HistoricTaskInstance hti : htiList) {
            if (LoginHelper.getUserId().toString().equals(hti.getAssignee())) {
                myTaskId = hti.getId();
                myTask = hti;
                break;
            }
        }
        if (null == myTaskId) {
            throw new RuntimeException("?????????????????????????????????????????????");
        }

        String processDefinitionId = myTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //??????
//      Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
        String myActivityId = null;
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
            .executionId(myTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : haiList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }
        FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);

        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);

        //?????????????????????
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>(flowNode.getOutgoingFlows());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * ?????????????????????
     *
     * @param processId
     * @return
     */
    @Override
    public InputStream diagram(String processId) {
        String processDefinitionId;
        // ???????????????????????????
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        // ????????????????????????????????????????????????
        if (Objects.isNull(processInstance)) {
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();

            processDefinitionId = pi.getProcessDefinitionId();
        } else {// ???????????????????????????????????????????????????
            // ??????????????????ID?????????????????????????????????ActivityId??????
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }

        // ?????????????????????
        List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();

        List<String> highLightedFlows = new ArrayList<>();
        List<String> highLightedNodes = new ArrayList<>();
        //?????????
        for (HistoricActivityInstance tempActivity : highLightedFlowList) {
            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
                //?????????
                highLightedFlows.add(tempActivity.getActivityId());
            } else {
                //????????????
                highLightedNodes.add(tempActivity.getActivityId());
            }
        }

        //???????????????
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
        //??????????????????????????????
        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
        return diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(),
            configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);

    }

    /**
     * ????????????????????????
     *
     * @param procInsId
     * @return
     */
    @Override
    public WfViewerVo getFlowViewer(String procInsId) {
        // ??????????????????
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(procInsId);
        List<HistoricActivityInstance> allActivityInstanceList = query.list();
        if (CollUtil.isEmpty(allActivityInstanceList)) {
            return new WfViewerVo();
        }
        // ??????????????????Id??????
        String processDefinitionId = allActivityInstanceList.get(0).getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        // ??????????????????????????????
        List<HistoricActivityInstance> finishedElementList = allActivityInstanceList.stream()
            .filter(item -> ObjectUtil.isNotNull(item.getEndTime())).collect(Collectors.toList());
        // ????????????????????????
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        // ??????????????????????????????
        Set<String> finishedTaskSet = new HashSet<>();
        finishedElementList.forEach(item -> {
            //if ("sequenceFlow".equals(item.getActivityType())) {
            if (BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW.equals(item.getActivityType())) {
                finishedSequenceFlowSet.add(item.getActivityId());
            } else {
                finishedTaskSet.add(item.getActivityId());
            }
        });
        // ??????????????????????????????
        Set<String> unfinishedTaskSet = allActivityInstanceList.stream()
            .filter(item -> ObjectUtil.isNull(item.getEndTime()))
            .map(HistoricActivityInstance::getActivityId)
            .collect(Collectors.toSet());
        // DFS ??????????????????????????????
        Set<String> rejectedSet = FlowableUtils.dfsFindRejects(bpmnModel, unfinishedTaskSet, finishedSequenceFlowSet, finishedTaskSet);
        return new WfViewerVo(finishedTaskSet, finishedSequenceFlowSet, unfinishedTaskSet, rejectedSet);
    }

    /**
     * ??????????????????
     *
     * @param taskId ??????ID
     * @return ????????????
     */
    @Override
    public Map<String, Object> getProcessVariables(String taskId) {
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
     * ??????????????????
     *
     * @param bo ??????
     * @return
     */
    @Override
    public WfNextDto getNextFlowNode(WfTaskBo bo) {
        // Step 1. ??????????????????????????????????????????
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        WfNextDto nextDto = new WfNextDto();
        if (Objects.nonNull(task)) {
            // Step 2. ????????????????????????????????????(????????????????????????????????????)
            Map<String, Object> variables = taskService.getVariables(task.getId());
            List<UserTask> nextUserTask = FindNextNodeUtil.getNextUserTasks(repositoryService, task, variables);
            if (CollectionUtils.isNotEmpty(nextUserTask)) {
                for (UserTask userTask : nextUserTask) {
                    MultiInstanceLoopCharacteristics multiInstance = userTask.getLoopCharacteristics();
                    // ????????????
                    if (Objects.nonNull(multiInstance)) {
                        List<SysUser> list = sysUserService.selectUserList(new SysUser());

                        nextDto.setVars(ProcessConstants.PROCESS_MULTI_INSTANCE_USER);
                        nextDto.setType(ProcessConstants.PROCESS_MULTI_INSTANCE);
                        nextDto.setUserList(list);
                    } else {

                        // ??????????????????????????? ????????????????????????????????????????????????????????????
                        String dataType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_DATA_TYPE);
                        String userType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_USER_TYPE);

                        if (ProcessConstants.DATA_TYPE.equals(dataType)) {
                            // ??????????????????
                            if (ProcessConstants.USER_TYPE_ASSIGNEE.equals(userType)) {
                                List<SysUser> list = sysUserService.selectUserList(new SysUser());

                                nextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                                nextDto.setType(ProcessConstants.USER_TYPE_ASSIGNEE);
                                nextDto.setUserList(list);
                            }
                            // ????????????(??????)
                            if (ProcessConstants.USER_TYPE_USERS.equals(userType)) {
                                List<SysUser> list = sysUserService.selectUserList(new SysUser());

                                nextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                                nextDto.setType(ProcessConstants.USER_TYPE_USERS);
                                nextDto.setUserList(list);
                            }
                            // ?????????
                            if (ProcessConstants.USER_TYPE_ROUPS.equals(userType)) {
                                List<SysRole> sysRoles = sysRoleService.selectRoleAll();

                                nextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                                nextDto.setType(ProcessConstants.USER_TYPE_ROUPS);
                                nextDto.setRoleList(sysRoles);
                            }
                        }
                    }
                }
            } else {
                return null;
            }
        }
        return nextDto;
    }

    /**
     * ?????????????????????
     * @param processInstance ????????????
     * @param variables ????????????
     */
    @Override
    public void startFirstTask(ProcessInstance processInstance, Map<String, Object> variables) {
        // ????????????????????????????????????????????????????????? todo:?????????????????????????????????????????????????????????
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        if (Objects.nonNull(task)) {
            String userIdStr = (String) variables.get(TaskConstants.PROCESS_INITIATOR);
            if (!StrUtil.equalsAny(task.getAssignee(), userIdStr)) {
                throw new ServiceException("???????????????????????????????????????????????????????????????????????????????????????????????????????????????");
            }
            taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), LoginHelper.getNickName() + "??????????????????");
            // taskService.setAssignee(task.getId(), userIdStr);
            taskService.complete(task.getId(), variables);
        }
    }
}
