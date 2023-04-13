package com.ruoyi.web.controller.project;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ruoyi.common.AjaxResult;
import com.ruoyi.common.config.MProjectConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.Constant;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.member.domain.ProjectMember;
import com.ruoyi.member.service.MemberAccountService;
import com.ruoyi.member.service.ProjectMemberService;
import com.ruoyi.project.domain.*;
import com.ruoyi.project.mapper.ProjectLogMapper;
import com.ruoyi.project.service.*;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.task.domain.Task;
import com.ruoyi.task.domain.TaskStagesTemplete;
import com.ruoyi.task.service.TaskProjectService;
import com.ruoyi.task.service.TaskStagesTempleteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService proService;

    @Autowired
    private ProjectTemplateService projectTemplateService;

//    @Autowired
//    private MemberService memberService;

    @Autowired
    private ProjectCollectionService projectCollectionService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private TaskStagesTempleteService taskStagesTempleteService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private ProjectMenuService projectMenuService;

    @Autowired
    private ISysUserService userService;

    @Value("${mproject.downloadServer}")
    private String downloadServer;

    /**
     * 登录系统后，请求的索引
     * @param
     * @return
     */
    @PostMapping("/index/index")
    @ResponseBody
    public AjaxResult projectIndex(){
        return AjaxResult.success(projectMenuService.getCurrentUserMenu());
    }


    /**
     * 得到自己的项目日志
     * @param
     * @return
     */
    @PostMapping("/project/getLogBySelfProject")
    @ResponseBody
    public AjaxResult getLogBySelfProject(@RequestParam Map<String,Object> mmap){
        String projectCode = MapUtils.getString(mmap,"projectCode");
        Map loginMember = getLoginMember();
        IPage<Map> ipage = Constant.createPage(mmap);
        Map params = new HashMap();
        params.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
//        params.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));
        params.put("projectCode",projectCode);

        IPage<Map> resultData =  proService.getLogBySelfProject(ipage,params);

        if(null != resultData){
            if(StringUtils.isEmpty(projectCode)){
                return new AjaxResult(AjaxResult.Type.SUCCESS, "", resultData.getRecords());
            }else{
                return  AjaxResult.success(Constant.createPageResultMap(ipage));
            }
        }
        return AjaxResult.success();
    }

    @Autowired
    TaskProjectService taskProjectService;
    @Autowired
    ProjectLogMapper projectLogMapper;
    @Autowired
    ProjectLogService projectLogService;
    @PostMapping("/project/_projectStats")
    @ResponseBody
    public AjaxResult _projectStats(@RequestBody Map<String,Object> mmap)  throws Exception {
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(StringUtils.isEmpty(projectCode)){
            return AjaxResult.warn("该项目已失效");
        }
        Map projectMap = proService.getProjectByCode(projectCode);
        if(MapUtils.isEmpty(projectMap)){
            return AjaxResult.warn("该项目已失效");
        }


        List<Task> listTask = taskProjectService.lambdaQuery().eq(Task::getDeleted,0).eq(Task::getProjectCode,projectCode).list();
        if(CollectionUtils.isEmpty(listTask)){
            listTask = new ArrayList<Task>();
        }
        Date now = new Date();
        String today = DateUtils.format("yyyy-MM-dd HH:mm:ss",now);
        String tomorrow = DateUtils.format("yyyy-MM-dd HH:mm:ss",DateUtils.add(now,5,-1));
        String nowTime = DateUtils.format("yyyy-MM-dd HH:mm:ss",now);
        Integer total=0;
        final Integer[] unDone= {0};
        final Integer[] done= {0};
        final Integer[] overdue= {0};
        final Integer[] toBeAssign = {0};
        final Integer[] expireToday={0};
        final Integer[]  doneOverdue= {0};
        listTask.stream().forEach(task -> {
            if(StrUtil.isEmpty(task.getAssignTo())){
                toBeAssign[0]++;
            }
            if(ObjectUtil.isNotEmpty(task.getDone()) && task.getDone()>0){
                done[0] ++;
            }else{
                unDone[0] ++;
            }
            if(StrUtil.isNotEmpty(task.getEndTime())){
                if(ObjectUtil.isNotEmpty(task.getDone()) && task.getDone()==0){
                    if(task.getEndTime().compareTo(tomorrow) == -1 && task.getEndTime().compareTo(today) >=0){
                        doneOverdue[0] ++;
                    }
                    if(-1 == task.getEndTime().compareTo(nowTime)){
                        overdue[0]++;
                    }
                    String endTime = StrUtil.isNotEmpty(task.getEndTime())&&task.getEndTime().length()>=10?task.getEndTime().substring(0,10):"";
                    if(endTime.compareTo(DateUtils.format("yyyy-MM-dd",now)) == 0){
                        expireToday[0] ++;
                    }
                }else{
                    List<ProjectLog> logList = projectLogService.lambdaQuery().eq(ProjectLog::getActionType,"task")
                            .eq(ProjectLog::getSourceCode,task.getCode()).eq(ProjectLog::getType,"done").list();
                    if(!CollectionUtils.isEmpty(logList)){
                        if(task.getEndTime().compareTo(logList.get(0).getCreateTime()) == -1){
                            doneOverdue[0]++;
                        }
                    }
                }
            }
        });
        Map data = new HashMap();
        data.put("total", listTask.size());
        data.put("unDone",unDone[0]);
        data.put("done",done[0]);
        data.put("overdue",overdue[0]);
        data.put("toBeAssign", toBeAssign[0]);
        data.put("expireToday",expireToday[0]);
        data.put("doneOverdue",doneOverdue[0]);
        return AjaxResult.success(data);
    }

    @Autowired
    ProjectReportService projectReportService;
    @PostMapping("/project/_getProjectReport")
    @ResponseBody
    public AjaxResult _getProjectReport(@RequestBody Map<String,Object> mmap)  throws Exception {
        String projectCode = MapUtils.getString(mmap, "projectCode");
        if(StringUtils.isEmpty(projectCode)){
            return AjaxResult.success("项目已失效");
        }
        return AjaxResult.success(projectReportService.getReportByDay(projectCode,10));
    }


    /**
     * 上传头像
     * @param
     * @return
     */
    @PostMapping("/index/uploadAvatar")
    @ResponseBody
    public AjaxResult uploadAvatar(HttpServletRequest request, @RequestParam(value = "avatar") MultipartFile multipartFile)  throws Exception
    {
        String code = request.getParameter("code");
        Map resMap = new HashMap();
         if (multipartFile.isEmpty()) {
             return  AjaxResult.warn("文件名不能为空！");
         } else {
             if(ObjectUtil.isEmpty(multipartFile)){
                 throw new CustomException("请选择上传头像！");
             }
             //resMap = memberService.uploadAvatar(code,multipartFile.getOriginalFilename().toString(),multipartFile.getInputStream());
         }
         return  AjaxResult.success(resMap);
    }

    @Autowired
    SourceLinkService sourceLinkService;

    @PostMapping("/source_link/delete")
    @ResponseBody
    public AjaxResult sourceLinkDel(@RequestBody Map<String,Object> mmap){
        String sourceCode = MapUtils.getString(mmap,"sourceCode");
        Map loginMap = getLoginMember();
        if(StringUtils.isEmpty(sourceCode)){
            return AjaxResult.warn("资源不存在！");
        }
        int i = sourceLinkService.deleteSource(sourceCode,MapUtils.getString(loginMap,"memberCode"));
        return AjaxResult.success(i);
    }

//    /**
//     * 项目管理	我的项目 项目设置 项目删除（回收站）
//     * @param mmap
//     * @return
//     */
//    @PostMapping("/invite_link/save")
//    @ResponseBody
//    public AjaxResult inviteLinkSave(@RequestParam Map<String,Object> mmap)  throws Exception
//    {
//        Map loginMember = getLoginMember();
//        String inviteType = MapUtils.getString(mmap,"inviteType");
//        String sourceCode = MapUtils.getString(mmap,"sourceCode");
//        String memberCode = MapUtils.getString(loginMember,"memberCode");
//        Map inviteLink = inviteLinkService.getInviteLinkByInSoCr("project","sourceCode","memberCode");
//
//        if(MapUtils.isEmpty(inviteLink)){
//            InviteLink il = new InviteLink();
//            il.setCode(CommUtils.getUUID());
//            il.setCreate_by(memberCode);
//            il.setInvite_type("project");
//            il.setCreate_time(DateUtils.formatDateTime(new Date()));
//            il.setOver_time(DateUtils.formatDateTime(DateUtils.add(new Date(),5,1)));
//            il.setSource_code(sourceCode);
//            inviteLinkService.save(il);
//            return AjaxResult.success(il);
//        }
//        return AjaxResult.success(inviteLink);
//    }

    /**
     * 项目管理	我的项目 项目设置 项目删除（回收站）
     * @param mmap
     * @return
     */
    @PostMapping("/project/recycle")
    @ResponseBody
    public AjaxResult recycle(@RequestBody Map<String,Object> mmap)  throws Exception
        {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        Map projectMap = proService.getProjectByCode(projectCode);
        if(MapUtils.isEmpty(projectMap)){
            return AjaxResult.warn("文件不存在");
        }
        if("1".equals(MapUtils.getString(projectMap,"deleted"))){
            return AjaxResult.warn("文件已在回收站");
        }
        int i = proService.updateRecycleByCode(projectCode,1,DateUtils.formatDateTime(new Date()));
        return AjaxResult.success(i);
    }
    /**
     * 项目管理	我的项目 项目设置 项目删除恢复（回收站）
     * @param mmap
     * @return
     */
    @PostMapping("/project/recovery")
    @ResponseBody
    public AjaxResult recovery(@RequestBody Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        int i = proService.updateRecycleByCode(projectCode,0,DateUtils.formatDateTime(new Date()));
        return AjaxResult.success(i);
    }

    /**
     * 项目管理	我的项目 项目设置 项目归档
     * @param mmap
     * @return
     */
    @PostMapping("/project/archive")
    @ResponseBody
    public AjaxResult archive(@RequestBody Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        int i = proService.updateArctiveByCode(projectCode,1,DateUtils.formatDateTime(new Date()));
        return AjaxResult.success(i);
    }

    /**
     * 项目管理	已归档项目  取消归档
     * @param mmap
     * @return
     */
    @PostMapping("/project/recoveryArchive")
    @ResponseBody
    public AjaxResult recoveryArchive(@RequestBody Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        int i = proService.updateArctiveByCode(projectCode,0,"");
        return AjaxResult.success(i);
    }

    @PostMapping("/project/quit")
    @ResponseBody
    public AjaxResult projectQuit(@RequestBody Map<String,Object> mmap)  throws Exception
    {
        String projectCode = MapUtils.getString(mmap,"projectCode");
        Project project = proService.lambdaQuery().eq(Project::getCode,projectCode).one();
        if(ObjectUtils.isEmpty(project)){
            throw new CustomException("项目不存在");
        }
        ProjectMember projectMember = projectMemberService.lambdaQuery().eq(ProjectMember::getProjectCode,projectCode)
                .eq(ProjectMember::getMemberCode, LoginHelper.getLoginUser().getCode()).one();
        if(ObjectUtils.isEmpty(projectMember)){
            throw new CustomException("你不是该项目成员");
        }
        if(projectMember.getIsOwner()>0){
            throw new CustomException("创建者不能退出项目");
        }
        return AjaxResult.success(projectMemberService.removeById(projectMember.getId()));
    }


    /**
     * 项目管理	我的项目 项目设置 编辑保存
     * @param mmap
     * @return
     */
    @PostMapping("/project/edit")
    @ResponseBody
    public AjaxResult projectEdit(@RequestBody Map<String,Object> mmap)  throws Exception
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));

        Project project = proService.lambdaQuery().eq(Project::getCode,projectCode).one();
        if(ObjectUtils.isEmpty(project)){
            throw new CustomException("项目不存在");
        }


        //Map projectMap = proService.getProjectByCode(projectCode);


        //Project project = BeanMapUtils.mapToBean(mmap,Project.class);
        if(StringUtils.isNotEmpty(MapUtils.getString(mmap,"privated"))){
            project.setPrivated(MapUtils.getInteger(mmap,"privated"));
        }

        project.setName(MapUtils.getString(mmap,"name",project.getName()));
        project.setDescription(MapUtils.getString(mmap,"description",project.getDescription()));
        project.setCover(MapUtils.getString(mmap,"cover",project.getCover()));

        project.setPrefix(MapUtils.getString(mmap,"prefix",project.getPrefix()));
        project.setTaskBoardTheme(MapUtils.getString(mmap,"task_board_theme",project.getTaskBoardTheme()));

        project.setOpenBeginTime(MapUtils.getInteger(mmap,"open_begin_time",project.getOpenBeginTime()));
        project.setOpenTaskPrivate(MapUtils.getInteger(mmap,"open_task_private",project.getOpenTaskPrivate()));
        project.setSchedule(MapUtils.getDouble(mmap,"schedule",project.getSchedule()));
        project.setOpenPrefix(MapUtils.getInteger(mmap,"open_prefix",project.getOpenPrefix()));
        project.setAccessControlType(MapUtils.getString(mmap,"access_control_type",project.getAccessControlType()));
        project.setAutoUpdateSchedule(MapUtils.getInteger(mmap,"auto_update_schedule",project.getAutoUpdateSchedule()));
        project.setBeginTime(MapUtils.getString(mmap,"begin_time",project.getBeginTime()));
        project.setEndTime(MapUtils.getString(mmap,"end_time",project.getEndTime()));
        boolean result = proService.updateById(project);
        return AjaxResult.success(result);
    }

    @PostMapping("/project/uploadCover")
    @ResponseBody
    public AjaxResult projectUploadCover(HttpServletRequest request, @RequestParam(value = "cover") MultipartFile multipartFile)
    {
        String projectCode = request.getParameter("projectCode");
        if (multipartFile.isEmpty()) {
            return  AjaxResult.warn("文件不能为空！");
        } else {
            String dateTimeNow = DateUtils.dateTimeNow();
            String date = DateUtils.dateTimeNow("yyyyMMdd");
            String uuid = CommUtils.getUUID();
            // 文件原名称
            String originFileName = multipartFile.getOriginalFilename().toString();
            // 上传文件重命名
            String uploadFileName = uuid+"-"+originFileName;
            String file_url = MProjectConfig.getProfile()+"/projectfile/project/cover/"+date+"/";
            String base_url = "/projectfile/project/cover/"+date+"/"+uploadFileName;
            String downloadUrl = "/common/image?filePathName="+base_url+"&realFileName="+originFileName;
            try{
                // 这里使用Apache的FileUtils方法来进行保存
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(file_url, uploadFileName));

                Map result = new HashMap();
                result.put("base_url",base_url);
                result.put("url",downloadServer+downloadUrl);
                result.put("filename",originFileName);
                return AjaxResult.success(result);
            }catch(Exception e){
                throw new CustomException("上传文件错误");
            }

        }
    }

    /**
     * 项目管理	我的项目 项目设置打开
     * @param mmap
     * @return
     */
    @PostMapping("/project/read")
    @ResponseBody
    public AjaxResult projectRead(@RequestBody Map<String,Object> mmap)
    {
        String projectCode = MapUtils.getString(mmap,"projectCode");//String.valueOf(mmap.get("projectCode"));

        Project project = proService.lambdaQuery().eq(Project::getCode,projectCode).one();
        if(ObjectUtils.isEmpty(project)){
            return AjaxResult.warn("项目信息有误！");
        }
        project.setCollected(0);
        ProjectCollection projectCollection = projectCollectionService.lambdaQuery().eq(ProjectCollection::getProjectCode,project.getCode())
            .eq(ProjectCollection::getMemberCode,LoginHelper.getLoginUser().getCode()).one();
        if(ObjectUtils.isNotEmpty(projectCollection)){
            project.setCollected(1);
        }
        ProjectMember projectMember = projectMemberService.lambdaQuery().eq(ProjectMember::getProjectCode,project.getCode())
                .eq(ProjectMember::getIsOwner,1).one();
        if(ObjectUtils.isNotEmpty(projectMember)){
            //Member member = memberService.lambdaQuery().eq(Member::getCode,projectMember.getMember_code()).one();
            SysUser sysUser = userService.lambdaQuery().eq(SysUser::getCode, projectMember.getMemberCode()).one();
            if(ObjectUtils.isNotEmpty(sysUser)){
                project.setOwnerName(sysUser.getNickName());
                project.setOwnerAvatar(sysUser.getAvatar());
            }
        }
        return AjaxResult.success(project);
        /*Map loginMember = getLoginMember();

        Map resultData = new HashMap();
        Map projectMap = proService.getProjectByCode(projectCode);
        resultData.putAll(projectMap);
        Map pm = projectMemberService.getProjectMemberByProjectCode(projectCode);
        List<Map> pc = projectCollectionService.getProjectCollection(projectCode,MapUtils.getString(loginMember,"memberCode"));

        if(pc!=null && pc.size()>0 && null!=pc.get(0).get("member_code")){
            resultData.put("collected",1);
        }else{
            resultData.put("collected",0);
        }

        if(ObjectUtils.isNotEmpty(pm)){
            Member member = memberService.getMemberByCode(MapUtils.getString(projectMap,"member_code"));
            if(ObjectUtils.isNotEmpty(member)){
                resultData.put("owner_name",member.getName());
                resultData.put("owner_avatar",member.getAvatar());
            }

        }
        //resultData.put("private",projectMap.get("privated"));
        return AjaxResult.success(resultData);*/
    }

    /**
     * 项目管理	我的项目 点击项目进行详细页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project/selfList")
    @ResponseBody
    public AjaxResult projectSelfList(@RequestBody Map<String,Object> mmap)
    {
        LoginUser loginUser = LoginHelper.getLoginUser();
        Map loginMember = getLoginMember();
        Integer archive = MapUtils.getInteger(mmap,"archive",-1);
        Integer type =  MapUtils.getInteger(mmap,"type",0);
        Integer delete = MapUtils.getInteger(mmap,"delete",-1);
        String memberCode = MapUtils.getString(mmap,"memberCode","");
        SysUser member = null;
        if(StringUtils.isNotEmpty(memberCode)){
            //member = memberService.getMemberByCode(memberCode);
            member = userService.getUserByCode(memberCode);
        }else{
            //member = memberService.getMemberByCode(MapUtils.getString(loginMember,"memberCode"));
            member = userService.getUserByCode(MapUtils.getString(loginMember, "memberCode"));
        }
        if(ObjectUtils.isEmpty(member)){
            return AjaxResult.warn("参数有误");
        }

        Integer deleted = delete == -1?1:delete;
        if(type == 0){
            deleted = 0;
        }

        IPage<Map> iPage = Constant.createPage(mmap);

        Map params = new HashMap();
        params.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        params.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));
        params.put("deleted",deleted);params.put("archive",archive);


        iPage = proService.getMemberProjects(iPage,params);

        List<Map> resultList = new ArrayList<>();
        List<Map> records = iPage.getRecords();
        List<Map> pc = null;
        if(!CollectionUtils.isEmpty(records)){
            for(Map map:records){
                map.put("owner_name","-");
                if(StringUtils.isNotEmpty(MapUtils.getString(map,"project_code"))){

                }
                pc = projectCollectionService.getProjectCollection(MapUtils.getString(map,"code"),MapUtils.getString(map,"member_code"));
                if(pc!=null && pc.size()>0 && null!=pc.get(0).get("member_code")){
                    map.put("collected",1);
                }else{
                    map.put("collected",0);
                }
                Map pm = projectMemberService.gettMemberCodeAndNameByProjectCode(MapUtils.getString(map,"code"));
                if(MapUtils.isNotEmpty(pm)){
                    map.put("owner_name",pm.get("name"));
                }
                resultList.add(map);
            }
        }
        iPage.setRecords(resultList);
        Map data = Constant.createPageResultMap(iPage);
        return new AjaxResult(AjaxResult.Type.SUCCESS, "", data);
    }

    /**
     * 项目管理	我的项目 加入/取消收藏
     * @param mmap
     * @return
     */
    @PostMapping("/project_collect/collect")
    @ResponseBody
    public AjaxResult projectCollect(@RequestBody Map<String,Object> mmap)
    {
        String projectCode = String.valueOf(mmap.get("projectCode"));
        String type = String.valueOf(mmap.get("type"));
        if(StringUtils.isEmpty(projectCode)){
            return AjaxResult.warn("请先选择项目");
        }
        projectCollectionService.collect(LoginHelper.getLoginUser().getCode(),projectCode,type);
        return AjaxResult.success("collect".equals(type)?"加入收藏成功":"取消收藏成功");
        /*

        Map loginMember = getLoginMember();



        ProjectCollection pc = new ProjectCollection();
        pc.setProject_code(projectCode);
        pc.setMember_code(String.valueOf(loginMember.get("memberCode")));
        pc.setCreate_time(DateUtil.formatDateTime(new Date()));
        if("collect".equals(type)){
            projectCollectionService.collect(pc);
            return AjaxResult.success("加入收藏成功");
        }else{
            projectCollectionService.cancel(pc);
            return AjaxResult.success("取消收藏成功");
        }*/

    }

    /**
     * 项目管理	我的项目 页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project/index")
    @ResponseBody
    public AjaxResult projectIndex(@RequestBody Map<String,Object> mmap)
    {
        System.out.println(mmap.size());
        System.out.println("this is type" + MapUtils.getString(mmap,"type",null));
        Map loginMember = getLoginMember();
        String archive = MapUtils.getString(mmap,"archive",null);
        String type = MapUtils.getString(mmap,"type",null);
        String recycle = MapUtils.getString(mmap,"recycle",null);
        String all = MapUtils.getString(mmap,"all",null);
        mmap.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        mmap.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));


        IPage<Map> ipage = Constant.createPage(mmap);
        return proService.projectIndex(ipage,mmap);
       
    }

    @PostMapping("/project_template/index")
    @ResponseBody
    public AjaxResult projectTemplateIndex(@RequestBody Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        mmap.put("memberCode",MapUtils.getString(loginMember,"memberCode"));
        mmap.put("orgCode",MapUtils.getString(loginMember,"organizationCode"));
        return AjaxResult.success(Constant.createPageResultMap(projectTemplateService.getProjectTemplateIndex(Constant.createPage(mmap),mmap)));
    }

    
//    /**
//     * 上传项目模板图片
//     * @param
//     * @return
//     */
//    @PostMapping("/project_template/uploadcover")
//    @ResponseBody
//    public AjaxResult uploadcover(HttpServletRequest request, @RequestParam("cover") MultipartFile multipartFile)  throws Exception
//    {
//        Map resMap = new HashMap();
//        String code = request.getParameter("code");
//
//         if (multipartFile.isEmpty()) {
//             return  AjaxResult.warn("文件名不能为空！");
//         } else {
//             if(ObjectUtil.isEmpty(multipartFile)){
//                 throw new CustomException("请选择上传图片！");
//             }
//             resMap = projectTemplateService.uploadcover(code,multipartFile.getOriginalFilename().toString(),multipartFile.getInputStream());
//         }
//         return  AjaxResult.success(resMap);
//    }

    
    
    /**
     * 项目管理  基础设置  项目模板  项目模板删除
     * @param mmap
     * @return
     */
    @PostMapping("/project_template/delete")
    @ResponseBody
    public AjaxResult projectTemplateDelete(@RequestBody Map<String,Object> mmap)
    {
        String code = MapUtils.getString(mmap,"code");
        Map projectTempMap = projectTemplateService.getProjectTemplateByCode(code);
        if(!MapUtils.isEmpty(projectTempMap)){
            String projectTempleteCode = MapUtils.getString(projectTempMap,"code");
            Integer projectTempleteId = MapUtils.getInteger(projectTempMap,"id");
            List<Integer> taskStagesTempIds = taskStagesTempleteService.selectIdsByProjectTempleteCode(projectTempleteCode);
            projectTemplateService.deleteProjectTemplateAndTaskStagesTemplage(projectTempleteId,taskStagesTempIds);
            return AjaxResult.success("删除成功");
        }else{
            return AjaxResult.warn("模板编号查询失败");
        }

    }

    /**
     * 项目管理  基础设置  项目模板  项目模板编辑保存
     * @param mmap
     * @return
     */
    @PostMapping("/project_template/edit")
    @ResponseBody
    public AjaxResult projectTemplateEdit(@RequestBody Map<String,Object> mmap)
    {
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String cover = MapUtils.getString(mmap,"cover");
        String code = MapUtils.getString(mmap,"code");

        Map projectTempMap = projectTemplateService.getProjectTemplateByCode(code);
        if(!MapUtils.isEmpty(projectTempMap)){
            ProjectTemplate pt = new ProjectTemplate();
            pt.setId(MapUtils.getInteger(projectTempMap,"id"));
            pt.setName(name);pt.setDescription(description);pt.setCover(cover);
            boolean bo = projectTemplateService.updateById(pt);
            return AjaxResult.success(bo);
        }else{
            return AjaxResult.warn("模板编号查询失败");
        }
    }
    @Autowired
    ProjectInfoService projectInfoService;
    @PostMapping("/project_info/save")
    @ResponseBody
    public AjaxResult projectInfoSave(@RequestBody Map<String,Object> mmap)
    {
        String name = MapUtils.getString(mmap,"name");
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(StringUtils.isEmpty(name)){
            throw new CustomException("请填写项目信息名称");
        }
        Project project = proService.lambdaQuery().eq(Project::getCode,projectCode).eq(Project::getDeleted,0).one();
        if(ObjectUtils.isEmpty(project)){
            throw new CustomException("该项目已失效");
        }
        ProjectInfo projectInfo = ProjectInfo.builder()
                .projectCode(projectCode).code(CommUtils.getUUID())
                .createTime(DateUtils.getTime())
                .description(MapUtils.getString(mmap,"description"))
                .organizationCode(ServletUtils.getHeaderParam("organizationCode"))
                .value(MapUtils.getString(mmap,"value"))
                .sort(MapUtils.getInteger(mmap,"description",0))
                .name(MapUtils.getString(mmap,"name")).build();
        projectInfoService.save(projectInfo);
        return AjaxResult.success(projectInfo);
    }
    @PostMapping("/project_info/edit")
    @ResponseBody
    public AjaxResult projectInfoEdit(@RequestBody Map<String,Object> mmap)
    {
        String name = MapUtils.getString(mmap,"name");
        String value = MapUtils.getString(mmap,"value");
        String description = MapUtils.getString(mmap,"description");
        String infoCode = MapUtils.getString(mmap,"infoCode");
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(StringUtils.isEmpty(name)){
            throw new CustomException("请填写项目信息名称");
        }
        if(StringUtils.isEmpty(infoCode)){
            throw new CustomException("请选择一个项目信息");
        }
        ProjectInfo projectInfo = projectInfoService.lambdaQuery().eq(ProjectInfo::getCode,infoCode).one();
        if(ObjectUtil.isEmpty(projectInfo)){
            throw new CustomException("该项目信息已失效");
        }
        List<ProjectInfo> projectInfoList = projectInfoService.lambdaQuery().eq(ProjectInfo::getName,name).eq(ProjectInfo::getProjectCode,projectCode).list();
        if(CollectionUtils.isNotEmpty(projectInfoList)){
            throw new CustomException("该项目信息名称已存在！");
        }

        projectInfo.setName(name);
        projectInfo.setDescription(description);
        projectInfo.setValue(value);
        projectInfoService.updateById(projectInfo);
        return AjaxResult.success(projectInfo);
    }
    @PostMapping("/project_info/delete")
    @ResponseBody
    public AjaxResult projectInfoDelete(@RequestBody Map<String,Object> mmap)
    {
        String infoCode = MapUtils.getString(mmap,"infoCode");
        projectInfoService.lambdaUpdate().eq(ProjectInfo::getCode,infoCode).remove();
        return AjaxResult.success();
    }

    /**
     * 项目管理  基础设置  项目模板  制作项目模板保存
     * @param mmap
     * @return
     */
    @PostMapping("/project_template/save")
    @ResponseBody
    public AjaxResult projectTemplateSave(@RequestBody Map<String,Object> mmap)
    {
        Map loginMember = getLoginMember();
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String cover = MapUtils.getString(mmap,"cover");
        String code = MapUtils.getString(mmap,"code");
        ProjectTemplate pt = new ProjectTemplate();
        pt.setName(name);pt.setDescription(description);pt.setCover(cover);pt.setCode(CommUtils.getUUID());
        pt.setCreateTime(DateUtils.formatDateTime(new Date()));
        pt.setMemberCode(MapUtils.getString(loginMember,"memberCode"));
        pt.setOrganizationCode(MapUtils.getString(loginMember,"organizationCode"));
        TaskStagesTemplete tst1 = new TaskStagesTemplete();
        TaskStagesTemplete tst2 = new TaskStagesTemplete();
        TaskStagesTemplete tst3 = new TaskStagesTemplete();
        List<TaskStagesTemplete> listTst = new ArrayList();
        tst1.setCode(CommUtils.getUUID());tst1.setCreateTime(DateUtils.formatDateTime(new Date()));tst1.setName("待处理");
        tst1.setProjectTemplateCode(pt.getCode());tst1.setSort(0);listTst.add(tst1);
        tst2.setCode(CommUtils.getUUID());tst2.setCreateTime(DateUtils.formatDateTime(new Date()));tst2.setName("进行中");
        tst2.setProjectTemplateCode(pt.getCode());tst2.setSort(0);listTst.add(tst2);
        tst3.setCode(CommUtils.getUUID());tst3.setCreateTime(DateUtils.formatDateTime(new Date()));tst3.setName("已完成");
        tst3.setProjectTemplateCode(pt.getCode());tst3.setSort(0);listTst.add(tst3);

        //boolean bo = projectTemplateService.save(pt);
        //boolean bo1 = taskStagesTempleteService.saveBatch(listTst);
        try{
            projectTemplateService.saveProjectTemplateAndTaskStagesTemplage(pt,listTst);
            return AjaxResult.success(true);
        }catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 创建新项目->保存
     * @param mmap
     * @return
     */
    @PostMapping("/project/save")
    @ResponseBody
    public AjaxResult saveProject(@RequestBody Map<String,Object> mmap)
    {
        String name = MapUtils.getString(mmap,"name");
        if(StringUtils.isEmpty(name)){
            throw new CustomException("请填写项目名称！");
        }
        Map loginMember = getLoginMember();
        Project project = Project.builder()
                .name(name)
                .description(MapUtils.getString(mmap,"description"))
                .templateCode(MapUtils.getString(mmap,"templateCode"))
                .createTime(DateUtils.getTime())
                .organizationCode(ServletUtils.getHeaderParam("organizationCode"))
                .code(CommUtils.getUUID())
                .cover(MapUtils.getString(mmap,"cover"))
                .taskBoardTheme("simple").build();
        return AjaxResult.success(proService.saveProject(project));
    }

    @PostMapping("/project/analysis")
    public AjaxResult analysis(String type) {
        Map member = getLoginMember();
        return AjaxResult.success("", proService.analysis(member, type));
    }
    
    @PostMapping("/project/getTopList")
    public AjaxResult getTopList(@RequestBody Map<String,Object> mmap) {
        String orgCode = getOrgCode();
        String dateType = MapUtils.getString(mmap, "dateType");
        String startDate = MapUtils.getString(mmap, "startDate");
        String endDate = MapUtils.getString(mmap, "endDate");
        return AjaxResult.success("", proService.getTopList(orgCode, dateType, startDate, endDate));
    }

    @PostMapping("/project/taskPriority")
    public AjaxResult taskPriority() {
        String orgCode = getOrgCode();
        return AjaxResult.success("", proService.taskPriority(orgCode));
    }
    
//    @Autowired
//    private SystemConfigService systemConfigService;
//
//    @PostMapping("/index/systemConfig")
//    @ResponseBody
//    public AjaxResult systemConfig(){
//        return AjaxResult.success(new HashMap(){{
//            put("app_name",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"app_name").one().getValue());
//            put("app_version",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"site_name").one().getValue());
//            put("app_version",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"app_version").one().getValue());
//            put("site_copy",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"site_copy").one().getValue());
//            put("site_name",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"site_name").one().getValue());
//            put("app_desc",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"app_desc").one().getValue());
//            put("app_title",systemConfigService.lambdaQuery().eq(SystemConfig::getName,"app_title").one().getValue());
//        }});
//    }
 
}
