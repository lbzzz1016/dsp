package com.ruoyi.web.controller.task;

import com.ruoyi.common.AjaxResult;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.Constant;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.member.service.MemberAccountService;
import com.ruoyi.member.service.MemberService;
import com.ruoyi.project.service.ProjectLogService;
import com.ruoyi.project.service.ProjectService;
import com.ruoyi.project.service.SourceLinkService;
import com.ruoyi.task.domain.TaskStagesTemplete;
import com.ruoyi.task.service.*;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project")
public class TaskStageController extends BaseController {

    @Autowired
    private TaskStageService taskStageService;

    @Autowired
    private TaskWorkflowService taskWorkflowService;

    @Autowired
    private TaskProjectService taskProjectService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProjectLogService projectLogService;

    @Autowired
    private TaskWorkTimeService taskWorkTimeService;

    @Autowired
    private FileService fileService;

    @Autowired
    private SourceLinkService sourceLinkService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskTagService taskTagService;

    @Autowired
    private TaskToTagService taskToTagService;

    @Autowired
    private TaskMemberService taskMemberService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private  TaskStagesTempleteService taskStagesTempleteService;

    @PostMapping("/task_stages_template/index")
    @ResponseBody
    public AjaxResult taskStagesTemplate(@RequestParam Map<String, Object> mmap) {
    	String code = MapUtils.getString(mmap, "code");
    	if (StringUtils.isEmpty(code)) {
            return AjaxResult.warn("请选择一个项目");
        }
        return AjaxResult.success(Constant.createPageResultMap(taskStagesTempleteService.getTaskStagesTemplate(Constant.createPage(mmap),code)));
    }

    @PostMapping("/task_stages_template/save")
    @ResponseBody
    public AjaxResult taskStagesSave(@RequestParam Map<String,Object> mmap){
        String name = MapUtils.getString(mmap,"name");
        String template_code = MapUtils.getString(mmap,"template_code");
        Integer sort = MapUtils.getInteger(mmap,"sort",0);
        if(StringUtils.isEmpty(name)){
            return AjaxResult.warn("请填写任务名称");
        }
        TaskStagesTemplete tst = TaskStagesTemplete.builder().code(CommUtils.getUUID()).create_time(DateUtils.getTime())
                .name(name).sort(sort).project_template_code(template_code).build();
        boolean result = taskStagesTempleteService.save(tst);
        if(result){
            return AjaxResult.success("添加成功",tst);
        }else{
            return AjaxResult.error("操作失败，请稍候再试！");
        }
    }

    @PostMapping("/task_stages_template/edit")
    @ResponseBody
    public AjaxResult taskStagesEdit(@RequestParam Map<String,Object> mmap){
        String name = MapUtils.getString(mmap,"name");
        String code = MapUtils.getString(mmap,"code");
        Integer sort = MapUtils.getInteger(mmap,"sort",0);
        if(StringUtils.isEmpty(code)){
            return AjaxResult.warn("请选择一个任务");
        }
        TaskStagesTemplete tst = taskStagesTempleteService.getTaskStageTempleteByCode(code);
        if(ObjectUtils.isEmpty(tst)){
            return AjaxResult.warn("该任务已失效");
        }
        tst.setName(name);
        tst.setSort(sort);
        boolean result = taskStagesTempleteService.updateById(tst);
        if(result){
            return AjaxResult.success("编辑任务成功",tst);
        }else{
            return AjaxResult.error("操作失败，请稍候再试！");
        }
    }
    @PostMapping("/task_stages_template/delete")
    @ResponseBody
    public AjaxResult taskStagesDel(@RequestParam Map<String,Object> mmap){
        String code = MapUtils.getString(mmap,"code");
        if(StringUtils.isEmpty(code)){
            return AjaxResult.warn("请选择一个任务");
        }
        TaskStagesTemplete tst = taskStagesTempleteService.getTaskStageTempleteByCode(code);
        if(ObjectUtils.isEmpty(tst)){
            return AjaxResult.warn("该模板不存在");
        }
        boolean result = taskStagesTempleteService.removeById(tst.getId());
        if(result){
            return AjaxResult.success("删除任务成功",tst);
        }else{
            return AjaxResult.error("操作失败，请稍候再试！");
        }
    }
    
//        /**
//         * 保存任务流转
//         *
//         * @return
//         */
//        @PostMapping("/task_workflow/save")
//        @ResponseBody
//        public AjaxResult save(String projectCode, String taskWorkflowName, String taskWorkflowRules) {
//            String orgCode = getOrgCode();
//            JSONObject rules = JSONObject .parseObject(taskWorkflowRules);
//            taskWorkflowService.save(orgCode, projectCode, taskWorkflowName, rules);
//            return AjaxResult.success("保存成功");
//        }
//
//        /**
//         * 编辑任务流转
//         *
//         * @return
//         */
//        @PostMapping("/task_workflow/edit")
//        @ResponseBody
//        public AjaxResult edit(String taskWorkflowCode, String taskWorkflowName, String taskWorkflowRules) {
//            JSONObject rules = JSONObject.parseObject(taskWorkflowRules);
//            taskWorkflowService.update(taskWorkflowCode, taskWorkflowName, rules);
//            return AjaxResult.success("修改成功");
//        }
//
//        /**
//         * 删除任务流转
//         *
//         * @return
//         */
//        @PostMapping("/task_workflow/delete")
//        @ResponseBody
//        public AjaxResult delete(String taskWorkflowCode) {
//            taskWorkflowService.delete(taskWorkflowCode);
//            return AjaxResult.success("删除成功");
//        }
     
}
