package com.ruoyi.project.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.commo.mapper.CommMapper;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.member.domain.ProjectMember;
import com.ruoyi.member.mapper.ProjectMemberMapper;
import com.ruoyi.member.service.MemberAccountService;
import com.ruoyi.member.service.ProjectMemberService;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.mapper.ProjectCollectionMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.task.domain.Task;
import com.ruoyi.task.domain.TaskStage;
import com.ruoyi.task.domain.TaskStagesTemplete;
import com.ruoyi.task.mapper.TaskMapper;
import com.ruoyi.task.mapper.TaskStageMapper;
import com.ruoyi.task.mapper.TaskStagesTempleteMapper;
import com.ruoyi.task.service.TaskProjectService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    TaskStagesTempleteMapper taskStagesTempleteMapper;

    @Autowired
    TaskStageMapper taskStageMapper;

    @Autowired
    CommMapper commMapper;

    @Autowired
    ProjectCollectionMapper projectCollectionMapper;

    @Lazy
    @Autowired
    ProjectMemberMapper projectMemberMapper;

    @Lazy
    @Autowired
    TaskProjectService taskProjectService;

    @Autowired
    MemberAccountService memberAccountService;

//    @Lazy
//    @Autowired
//    MemberService memberService;

    @Autowired
    ISysUserService userService;

    public List<String> selectProAuthNode(List<String> authorizeids) {
        return baseMapper.selectProAuthNode(authorizeids);
    }

    public List<Map> selectOrgByMemCode(Map params) {
        return baseMapper.selectOrgByMemCode(params);
    }

    public List<Map> selectDepByMemCode(Map params) {
        return baseMapper.selectDepByMemCode(params);
    }

    public IPage<Map> getProjectInfoByMemCodeOrgCode(IPage<Map> page, Map params){
        return baseMapper.getProjectInfoByMemCodeOrgCode(page,params);
    }
    public IPage<Map> getProjectInfoByMemCodeOrgCodeCollection(IPage<Map> page, Map params){
        return baseMapper.getProjectInfoByMemCodeOrgCodeCollection(page,params);
    }

    public IPage<Map> getLogBySelfProject(IPage<Map> page, Map params){
        String projectCode = MapUtils.getString(params,"projectCode");
        if(StringUtils.isEmpty(projectCode)){
            List<String> projectCodes = baseMapper.selectProjectCodesByMemberAndOrg(params);
            if(CollectionUtils.isEmpty(projectCodes))return page;
            page = baseMapper.selectTaskLogByProjectCode(page,projectCodes);
        }else{
            //page = baseMapper.selectLogBySelfProjectByMemberCode(page,params);
            page = baseMapper.selectProjectLogByProjectCode(page,projectCode);

            List<Map> record = page.getRecords();
            List resultRecord = new ArrayList();
            if(Optional.ofNullable(record).isPresent()){
                record.stream().forEach(m->{
                    String action_type = MapUtils.getString(m,"action_type");
                    if("task".equals(action_type)){
                        m.put("sourceInfo",taskMapper.selectTaskByCode(MapUtils.getString(m,"source_code")));
                    }else if("project".equals(action_type)){
                        m.put("sourceInfo",projectMapper.selectProjectByCode(MapUtils.getString(m,"source_code")));
                    }
                    resultRecord.add(m);
                });
            }
            page.setRecords(resultRecord);
        }
        return page;
    }

    @Autowired
    ProjectMemberService projectMemberService;

    public AjaxResult projectIndex(IPage page, Map params){
        String archive = MapUtils.getString(params,"archive",null);
        String type = MapUtils.getString(params,"type",null);
        String recycle = MapUtils.getString(params,"recycle",null);
        String all = MapUtils.getString(params,"all",null);
        String memberCode = MapUtils.getString(params,"memberCode");
        String orgCode = MapUtils.getString(params,"orgCode");
        String sql = null;
        String field = " pp.cover,pp.name,pp.code,pp.description,pp.access_control_type,pp.white_list,pp.order,pp.deleted,pp.template_code,pp.schedule,pp.create_time,pp.organization_code,pp.deleted_time,pp.private privated, pp.prefix, pp.open_prefix, pp.archive, pp.archive_time, pp.open_begin_time,pp.open_task_private,pp.task_board_theme,pp.begin_time,pp.end_time,pp.auto_update_schedule";
        if("my".equals(type) || "other".equals(type)){
            sql = String.format("select "+field+",pm.id,pm.project_code,pm.member_code from team_project as pp left join team_project_member as pm on pm.project_code = pp.code where pp.organization_code = '%s' and (pm.member_code = '%s' or pp.private = 0)",orgCode,memberCode);
        }else{
            sql = String.format("select "+field+" from team_project as pp left  join team_project_collection as pc on pc.project_code = pp.code where pp.organization_code = '%s' and pc.member_code = '%s' ",orgCode,memberCode);
        }

        if(!"other".equals(type)){
            sql += " and pp.deleted = 0";
        }
        if(StringUtils.isNotEmpty(archive)){
            sql += " and pp.archive = 1";
            sql += " and pp.deleted = 0";
        }
        if(StringUtils.isNotEmpty(recycle)){
            sql += " and pp.deleted = 1";
        }
        sql += " group by pp.`code` order by pp.id desc";
        page = commMapper.customQueryItem(page,sql);
        List<Map> list = page.getRecords();
        List<Map> listResult = new ArrayList();
        if(null != list){
            for(Map map:list) {
                map.put("collected",0);
                map.put("owner_name","-");
                List<Map> collects=projectCollectionMapper.selectProjectCollection(MapUtils.getString(map,"code"),memberCode);
                if(CollectionUtils.isNotEmpty(collects)){
                    map.put("collected",1);
                }
                //String memberName = projectMemberMapper.selectMemberNameByProjectMember(MapUtils.getString(map,"code"),memberCode);
                List<ProjectMember> owner = projectMemberService.lambdaQuery().eq(ProjectMember::getProjectCode,MapUtils.getString(map,"code")).eq(ProjectMember::getIsOwner,1).list();
                if(CollectionUtils.isEmpty(owner)){
                    continue;
                }
                //Member member = memberService.lambdaQuery().eq(Member::getCode,owner.get(0).getMember_code()).one();
                SysUser user = userService.lambdaQuery().eq(SysUser::getCode, owner.get(0).getMemberCode()).one();
                if(ObjectUtils.isNotEmpty(user)){
                    map.put("owner_name",user.getNickName());
                }
                listResult.add(map);
            }
        }
        page.setRecords(listResult);
        IPage finalPage = page;
        return AjaxResult.success(new HashMap(){{
            put("list", finalPage.getRecords());
            put("total", finalPage.getTotal());
            put("page", finalPage.getCurrent());
        }});

    }

    //根据templateCode获取任务模板名称
    public List<String> getTaskStageTempNameByTemplateCode(String templateCode){
        return baseMapper.getTaskStageTempNameByTemplateCode(templateCode);
    }

    @Autowired
    ProjectLogService projectLogService;

    @Transactional
    public Map saveProject(Project project){
        QueryWrapper<TaskStagesTemplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_template_code", project.getTemplateCode());
        List<TaskStagesTemplete> tsts = taskStagesTempleteMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(tsts)){
            tsts = getDefaultTaskStageTemplate();
        }
        AtomicInteger i= new AtomicInteger(0);
        tsts.stream().forEach(t->{
            TaskStage taskStage = new TaskStage();
            taskStage.setCode(CommUtils.getUUID());
            taskStage.setProjectCode(project.getCode());
            taskStage.setName(t.getName());
            taskStage.setCreateTime(DateUtil.formatDateTime(new Date()));
            taskStage.setSort(i.get());
            taskStageMapper.insert(taskStage);
            i.set(i.get() + 1);
        });
        //项目成员
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectCode(project.getCode());
        projectMember.setIsOwner(1);
        projectMember.setJoinTime(DateUtil.formatDateTime(new Date()));
        projectMember.setMemberCode(LoginHelper.getLoginUser().getCode());
        projectMemberMapper.insert(projectMember);
        //保存项目信息
        save(project);
        projectLogService.run(new HashMap(){{
            put("member_code",projectMember.getMemberCode());
            put("source_code",project.getCode());
            put("type","create");
            put("project_code",project.getCode());
        }});
        projectLogService.run(new HashMap(){{
            put("member_code", projectMember.getMemberCode());
            put("source_code", project.getCode());
            put("type","inviteMember");
            put("to_member_code", projectMember.getMemberCode());
            put("project_code", project.getCode());
        }});
        return baseMapper.getProjectById(project.getId());
    }

    public static List<TaskStagesTemplete> getDefaultTaskStageTemplate(){
        TaskStagesTemplete ts1=new TaskStagesTemplete();
        TaskStagesTemplete ts2=new TaskStagesTemplete();
        TaskStagesTemplete ts3=new TaskStagesTemplete();
        ts1.setName("待处理");ts2.setName("进行中");ts3.setName("已完成");
        return new ArrayList<TaskStagesTemplete>(){{
            add(ts1);add(ts2);add(ts3);
        }};
    }

    public Project getProjectByCodeNotDel(String code){
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCode, code);
        queryWrapper.eq(Project::getDeleted, 0);
        return baseMapper.selectOne(queryWrapper);
    }
    public Project getProjectProjectByCode(String code){
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCode, code);
        return baseMapper.selectOne(queryWrapper);
    }

    //根据projectCode获取project
    public Map getProjectByCode(String projectCode){
        Map project = baseMapper.selectProjectByCode(projectCode);
        project.put("private",project.get("privated"));
        return project;
    }
    //更新归档标识
    public  int updateArctiveByCode(String projectCode,Integer archive,String archiveTime){
        return baseMapper.updateArctiveByCode(projectCode,archive,archiveTime);
    }
    //更新逻辑删除标识（回收站）
    public  int updateRecycleByCode(String projectCode,Integer deleted,String deletedTime){
        return baseMapper.updateRecycleByCode(projectCode,deleted,deletedTime);
    }

    public IPage<Map> selfProjectList(IPage<Map> page, Map params){
        return baseMapper.selfProjectList(page,params);
    }

    public IPage<Map> getMemberProjects(IPage<Map> page, Map params){
        return baseMapper.selectMemberProjects(page,params);
    }

    public Map<String, Object> analysis(Map member, String type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDate.of(now.getYear(), Month.JANUARY, 1);
        Map<String, Object> result = new HashMap<>();
        //项目：每月项目数 项目总数 项目数排行 本月项目数
        List<Map<String, Object>> projectList = new ArrayList<>();
        int projectCount = 0;
        List<Map<String, Object>> projectTop = new ArrayList<>();
        int nowMonthProjectCount = 0;
        int projectSchedule = 0;
        int weekSchedule = 0;
        int daySchedule = 0;
        //任务：每月任务数 任务总数 任务数排行 逾期任务数 逾期率 今日任务数
        List<Map<String, Object>> taskList = new ArrayList<>();
        int taskCount = 0;
        List<Map<String, Object>> taskTop = new ArrayList<>();
        int taskOverdueCount = 0;
        int taskOverduePercent = 0;
        int nowTaskCount = 0;
        int weekRatio = 0;
        int dayRatio = 0;

        String memberCode = MapUtils.getString(member, "memberCode");
        String orgCode = MapUtils.getString(member, "organizationCode");
        //该组织下的所有项目
        List<Project> list = lambdaQuery().select(Project::getCode, Project::getSchedule, Project::getCreateTime).eq(Project::getOrganizationCode, orgCode)
        		.eq(Project::getArchive, 0).eq(Project::getDeleted, 0).list();        
        if (CollUtil.isNotEmpty(list)) {
            List<String> proCodeList = list.parallelStream().map(Project::getCode).collect(Collectors.toList());
            //该组织下的所有项目用户信息
            List<ProjectMember> allProjectList = projectMemberMapper.selectList(Wrappers.<ProjectMember>lambdaQuery().in(ProjectMember::getProjectCode, proCodeList));
            if (CollUtil.isNotEmpty(allProjectList)) {
                Map<String, String> memberCodeName = userService.lambdaQuery().select(SysUser::getCode, SysUser::getNickName).in(SysUser::getCode, allProjectList
                        .parallelStream().map(ProjectMember::getMemberCode).collect(Collectors.toList())).list()
                        .parallelStream().collect(Collectors.toMap(SysUser::getCode, SysUser::getNickName));
//                Map<String, String> memberCodeName = memberService.lambdaQuery().select(Member::getCode, Member::getName).in(Member::getCode, allProjectList
//                        .parallelStream().map(ProjectMember::getMember_code).collect(Collectors.toList())).list()
//                        .parallelStream().collect(Collectors.toMap(Member::getCode, Member::getName));
                Map<String, Long> topNum = new LinkedHashMap<>();
                allProjectList.stream().collect(Collectors.groupingBy(ProjectMember::getMemberCode, Collectors.counting()))
                        .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> topNum.put(x.getKey(), x.getValue()));
                topNum.forEach((key, val) -> {
                    Map<String, Object> map = new HashMap<>(4);
                    map.put("name", memberCodeName.get(key));
                    map.put("total", val);
                    projectTop.add(map);
                });
            }
            projectCount = list.size();
            while (date.getMonthValue() <= now.getMonthValue()) {
                Map<String, Object> map = new HashMap<>(4);
                LocalDate finalDate = date;
                long count = list.stream().filter(o -> {
                    LocalDateTime create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS));
                    return create.getMonthValue() == finalDate.getMonthValue() && create.getYear() == finalDate.getYear();
                }).count();
                if (date.getMonthValue() == now.getMonthValue()) {
                    nowMonthProjectCount = (int) count;
                }
                map.put("日期", date.getMonthValue() + "月");
                map.put("数量", count);
                projectList.add(map);
                date = date.plusMonths(1);
            }
            projectSchedule = (int) list.stream().mapToDouble(Project::getSchedule).average().orElse(0);
            LocalDateTime lastWeek = now.plusWeeks(-1);
            LocalDateTime yesterday = now.plusDays(-1);
            double lastWeekSchedule = list.stream().filter(o -> {
                LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                return create.isBefore(lastWeek.toLocalDate()) || create.equals(lastWeek.toLocalDate());
            }).mapToDouble(Project::getSchedule).average().orElse(0);
            double yesterdaySchedule = list.stream().filter(o -> {
                LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                return create.isBefore(yesterday.toLocalDate()) || create.equals(yesterday.toLocalDate());
            }).mapToDouble(Project::getSchedule).average().orElse(0);
            weekSchedule = lastWeekSchedule == 0 ? 0 : (int) ((projectSchedule - lastWeekSchedule) * 100 / lastWeekSchedule);
            daySchedule = yesterdaySchedule == 0 ? 0 : (int) ((projectSchedule - yesterdaySchedule) * 100 / yesterdaySchedule);
            //任务
            List<Task> tasks = taskProjectService.lambdaQuery().in(Task::getProjectCode, proCodeList).list();

            LocalDate date1 = LocalDate.of(now.getYear(), Month.JANUARY, 1) ;
            if (CollUtil.isNotEmpty(tasks)) {
                taskCount = tasks.size();
                while (date1.getMonthValue() <= now.getMonthValue()) {
                    Map<String, Object> map = new HashMap<>(4);
                    LocalDate finalDate = date1;
                    long count = tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                    	LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                    	return create.getMonthValue() == finalDate.getMonthValue() && create.getYear() == finalDate.getYear();
                    }).count();
//                    if (date1.getMonthValue() == now.getMonthValue()) {
//                        nowMonthProjectCount = (int) count;
//                    }
                    map.put("日期", date1.getMonthValue() + "月");
                    map.put("任务", count);
                    taskList.add(map);
                    date1 = date1.plusMonths(1);
                }
                List<Task> memberTaskList = tasks.parallelStream().filter(o -> StrUtil.isNotEmpty(o.getAssignTo())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(memberTaskList)) {
                    Set<String> memberCodes = memberTaskList.parallelStream().map(Task::getAssignTo).collect(Collectors.toSet());
//                    Map<String, String> memberCodeName = memberService.lambdaQuery().select(Member::getCode, Member::getName).in(Member::getCode, memberCodes)
//                            .list().parallelStream().collect(Collectors.toMap(Member::getCode, Member::getName));
                    Map<String, String> memberCodeName = userService.lambdaQuery().select(SysUser::getCode, SysUser::getNickName).in(SysUser::getCode, memberCodes)
                            .list().parallelStream().collect(Collectors.toMap(SysUser::getCode, SysUser::getNickName));
                    Map<String, Long> topTaskNum = new LinkedHashMap<>();
                    memberTaskList.stream().collect(Collectors.groupingBy(Task::getAssignTo, Collectors.counting()))
                            .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEachOrdered(x -> topTaskNum.put(x.getKey(), x.getValue()));
                    topTaskNum.forEach((key, val) -> {
                        Map<String, Object> map = new HashMap<>(4);
                        map.put("name", memberCodeName.get(key));
                        map.put("total", val);
                        taskTop.add(map);
                    });
                }
                nowTaskCount = (int) tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                	LocalDateTime create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS));
                	return create.equals(now);
                }).count();
                //逾期任务 逾期率
                taskOverdueCount = (int) tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getEndTime())).filter(o -> {
                    LocalDateTime end = LocalDateTime.parse(o.getEndTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM));
                    return end.isBefore(now) && o.getDone() == 0;
                }).count();
                taskOverduePercent = taskOverdueCount * 100 / taskCount;
                //周同比  日同比
                int taskWeekCount = (int) tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getEndTime())).filter(o -> {
                    LocalDateTime end = LocalDateTime.parse(o.getEndTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM));
                    return end.isBefore(lastWeek) && o.getDone() == 0;
                }).count();
                weekRatio = taskWeekCount == 0 ? 0 : (taskOverdueCount - taskWeekCount) * 100 / taskWeekCount;
                int taskDayCount = (int) tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getEndTime())).filter(o -> {
                    LocalDateTime end = LocalDateTime.parse(o.getEndTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM));
                    return end.isBefore(yesterday) && o.getDone() == 0;
                }).count();
                dayRatio = taskDayCount == 0 ? 0 : (taskOverdueCount - taskDayCount) * 100 / taskDayCount;
            }
        }
        result.put("projectList", projectList);
        result.put("projectCount", projectCount);
        result.put("projectTop", projectTop);
        result.put("nowMonthProjectCount", nowMonthProjectCount);
        result.put("projectSchedule", projectSchedule);
        result.put("weekSchedule", weekSchedule);
        result.put("daySchedule", daySchedule);
        //任务数据
        result.put("taskList", taskList);
        result.put("taskCount", taskCount);
        result.put("taskTop", taskTop);
        result.put("taskOverdueCount", taskOverdueCount);
        result.put("taskOverduePercent", taskOverduePercent);
        result.put("nowTaskCount", nowTaskCount);
        result.put("weekRatio", weekRatio);
        result.put("dayRatio", dayRatio);
        return result;
    }
    

    public Map<String, Object> getTopList(String orgCode, String dateType, String startDate, String endDate) {
        //该组织下的所有项目
        List<Project> projects = lambdaQuery().select(Project::getCode, Project::getSchedule, Project::getCreateTime).eq(Project::getOrganizationCode, orgCode)
                .eq(Project::getArchive, 0).eq(Project::getDeleted, 0).list();
        List<Task> tasks = null;
        List<ProjectMember> projectMemberList = null;
        Map<String, String> memberCodeName = null;
        if (CollUtil.isNotEmpty(projects)) {
            List<String> proCodeList = projects.parallelStream().map(Project::getCode).collect(Collectors.toList());
            //该组织下的所有项目用户信息
            projectMemberList = projectMemberMapper.selectList(Wrappers.<ProjectMember>lambdaQuery().in(ProjectMember::getProjectCode, proCodeList));
            if (CollUtil.isNotEmpty(projectMemberList)) {
//                memberCodeName = memberService.lambdaQuery().select(Member::getCode, Member::getName).in(Member::getCode, projectMemberList
//                        .parallelStream().map(ProjectMember::getMember_code).collect(Collectors.toList())).list()
//                        .parallelStream().collect(Collectors.toMap(Member::getCode, Member::getName));
                memberCodeName = userService.lambdaQuery().select(SysUser::getCode, SysUser::getNickName).in(SysUser::getCode, projectMemberList
                    .parallelStream().map(ProjectMember::getMemberCode).collect(Collectors.toList())).list()
                    .parallelStream().collect(Collectors.toMap(SysUser::getCode, SysUser::getNickName));
            }
            //任务
            tasks = taskProjectService.lambdaQuery().in(Task::getProjectCode, proCodeList).list();
        }
        return buildInfo(dateType, startDate, endDate, projects, tasks, projectMemberList, memberCodeName);
    }

    private Map<String, Object> buildInfo(String dateType, String startDate, String endDate, List<Project> projects, List<Task> tasks,
                          List<ProjectMember> projectMemberList, Map<String, String> memberCodeName) {
        Map<String, Object> result = new HashMap<>();
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> projectList = new ArrayList<>();
        List<Map<String, Object>> projectTop = new ArrayList<>();
        List<Map<String, Object>> taskList = new ArrayList<>();
        List<Map<String, Object>> taskTop = new ArrayList<>();
        if (StrUtil.isNotEmpty(dateType)) {
            switch (dateType) {
                case "day": {
                    //项目数
                    Map<String, Object> projectMap = new HashMap<>(4);
                    long count = projects == null ? 0 : projects.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                        LocalDate begin = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                        return begin.equals(now);
                    }).count();
                    projectMap.put("日期", "今日");
                    projectMap.put("数量", count);
                    projectList.add(projectMap);
                    //项目排行
                    Map<String, Long> topNum = new LinkedHashMap<>();
                    if (projectMemberList != null) {
                        projectMemberList.stream().filter(o -> StrUtil.isNotEmpty(o.getJoinTime())).filter(o -> {
                            LocalDate join = LocalDateTime.parse(o.getJoinTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return join.equals(now);
                        }).collect(Collectors.groupingBy(ProjectMember::getMemberCode, Collectors.counting()))
                                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(x -> topNum.put(x.getKey(), x.getValue()));
                        topNum.forEach((key, val) -> {
                            Map<String, Object> topMap = new HashMap<>(4);
                            topMap.put("name", memberCodeName.get(key));
                            topMap.put("total", val);
                            projectTop.add(topMap);
                        });
                    }
                    //任务数
                    Map<String, Object> taskMap = new HashMap<>(4);
                    long taskCount = tasks == null ? 0 : tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                        LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                        return create.equals(now);
                    }).count();
                    taskMap.put("日期", "今日");
                    taskMap.put("任务", taskCount);
                    taskList.add(taskMap);
                    break;
                }
                case "week": {
                    LocalDate beginDate = now.with(DayOfWeek.MONDAY);
                    LocalDate finalBeginDate1 = beginDate;
                    while (beginDate.isBefore(now) || beginDate.equals(now)) {
                        Map<String, Object> map = new HashMap<>(4);
                        LocalDate finalBeginDate = beginDate;
                        long count = projects == null ? 0 : projects.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                            LocalDate begin = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return begin.equals(finalBeginDate);
                        }).count();
                        String day;
                        if (beginDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                            day = "星期一";
                        } else if (beginDate.getDayOfWeek() == DayOfWeek.TUESDAY) {
                            day = "星期二";
                        } else if (beginDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                            day = "星期三";
                        } else if (beginDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
                            day = "星期四";
                        } else if (beginDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                            day = "星期五";
                        } else if (beginDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                            day = "星期六";
                        } else {
                            day = "星期日";
                        }
                        map.put("日期", day);
                        map.put("数量", count);
                        projectList.add(map);
                        //任务数
                        Map<String, Object> taskMap = new HashMap<>(4);
                        long taskCount = tasks == null ? 0 : tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                            LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return create.equals(finalBeginDate);
                        }).count();
                        taskMap.put("日期", day);
                        taskMap.put("任务", taskCount);
                        taskList.add(taskMap);
                        beginDate = beginDate.plusDays(1);
                    }
                    Map<String, Long> topNum = new LinkedHashMap<>();
                    if (projectMemberList != null) {
                        projectMemberList.stream().filter(o -> StrUtil.isNotEmpty(o.getJoinTime())).filter(o -> {
                            LocalDate join = LocalDateTime.parse(o.getJoinTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return join.isAfter(finalBeginDate1.plusDays(-1)) && join.isBefore(now.plusDays(1));
                        }).collect(Collectors.groupingBy(ProjectMember::getMemberCode, Collectors.counting()))
                                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(x -> topNum.put(x.getKey(), x.getValue()));
                        topNum.forEach((key, val) -> {
                            Map<String, Object> topMap = new HashMap<>(4);
                            topMap.put("name", memberCodeName.get(key));
                            topMap.put("total", val);
                            projectTop.add(topMap);
                        });
                    }
                    break;
                }
                case "month": {
                    LocalDate beginDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
                    LocalDate finalBeginDate1 = beginDate;
                    while (beginDate.isBefore(now) || beginDate.equals(now)) {
                        Map<String, Object> map = new HashMap<>(4);
                        LocalDate finalBeginDate = beginDate;
                        long count = projects == null ? 0 : projects.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                            LocalDate begin = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return begin.equals(finalBeginDate);
                        }).count();
                        map.put("日期", beginDate.getDayOfMonth() + "日");
                        map.put("数量", count);
                        projectList.add(map);
                        //任务数
                        Map<String, Object> taskMap = new HashMap<>(4);
                        long taskCount = tasks == null ? 0 : tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                            LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return create.equals(finalBeginDate);
                        }).count();
                        taskMap.put("日期", beginDate.getDayOfMonth() + "日");
                        taskMap.put("任务", taskCount);
                        taskList.add(taskMap);
                        beginDate = beginDate.plusDays(1);
                    }
                    Map<String, Long> topNum = new LinkedHashMap<>();
                    if (projectMemberList != null) {
                        projectMemberList.stream().filter(o -> StrUtil.isNotEmpty(o.getJoinTime())).filter(o -> {
                            LocalDate join = LocalDateTime.parse(o.getJoinTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return join.getMonthValue() == finalBeginDate1.getMonthValue() && join.getYear() == finalBeginDate1.getYear();
                        }).collect(Collectors.groupingBy(ProjectMember::getMemberCode, Collectors.counting()))
                                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(x -> topNum.put(x.getKey(), x.getValue()));
                        topNum.forEach((key, val) -> {
                            Map<String, Object> topMap = new HashMap<>(4);
                            topMap.put("name", memberCodeName.get(key));
                            topMap.put("total", val);
                            projectTop.add(topMap);
                        });
                    }
                    break;
                }
                case "year": {
                    LocalDate beginDate = LocalDate.of(now.getYear(), 1, 1);
                    LocalDate finalBeginDate1 = beginDate;
                    while (beginDate.isBefore(now) || beginDate == now) {
                        Map<String, Object> map = new HashMap<>(4);
                        LocalDate finalBeginDate = beginDate;
                        long count = projects == null ? 0 : projects.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                            LocalDate begin = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return begin.getMonthValue() == finalBeginDate.getMonthValue() && begin.getYear() == finalBeginDate.getYear();
                        }).count();
                        map.put("日期", beginDate.getMonthValue() + "月");
                        map.put("数量", count);
                        projectList.add(map);
                        //任务数
                        Map<String, Object> taskMap = new HashMap<>(4);
                        long taskCount = tasks == null ? 0 : tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                            LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return create.getMonthValue() == finalBeginDate.getMonthValue() && create.getYear() == finalBeginDate.getYear();
                        }).count();
                        taskMap.put("日期", beginDate.getMonthValue() + "月");
                        taskMap.put("任务", taskCount);
                        taskList.add(taskMap);
                        beginDate = beginDate.plusMonths(1);
                    }
                    Map<String, Long> topNum = new LinkedHashMap<>();
                    if (projectMemberList != null) {
                        projectMemberList.stream().filter(o -> StrUtil.isNotEmpty(o.getJoinTime())).filter(o -> {
                            LocalDate join = LocalDateTime.parse(o.getJoinTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                            return join.getYear() == finalBeginDate1.getYear();
                        }).collect(Collectors.groupingBy(ProjectMember::getMemberCode, Collectors.counting()))
                                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(x -> topNum.put(x.getKey(), x.getValue()));
                        topNum.forEach((key, val) -> {
                            Map<String, Object> topMap = new HashMap<>(4);
                            topMap.put("name", memberCodeName.get(key));
                            topMap.put("total", val);
                            projectTop.add(topMap);
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        } else {
            LocalDate beginDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
            LocalDate finalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));
            finalDate = finalDate.isBefore(now) ? finalDate : now;
            LocalDate finalBeginDate1 = beginDate;
            LocalDate finalDate1 = finalDate;
            while (beginDate.isBefore(finalDate) || beginDate.equals(finalDate)) {
                Map<String, Object> map = new HashMap<>(4);
                LocalDate finalBeginDate = beginDate;
                long count = projects == null ? 0 : projects.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                    LocalDate begin = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                    return begin.equals(finalBeginDate);
                }).count();
                map.put("日期", beginDate.getMonthValue() + "-" + beginDate.getDayOfMonth());
                map.put("数量", count);
                projectList.add(map);
                //任务数
                Map<String, Object> taskMap = new HashMap<>(4);
                long taskCount = tasks == null ? 0 : tasks.stream().filter(o -> StrUtil.isNotEmpty(o.getCreateTime())).filter(o -> {
                    LocalDate create = LocalDateTime.parse(o.getCreateTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                    return create.equals(finalBeginDate);
                }).count();
                taskMap.put("日期", beginDate.getMonthValue() + "-" + beginDate.getDayOfMonth());
                taskMap.put("任务", taskCount);
                taskList.add(taskMap);
                beginDate = beginDate.plusDays(1);
            }
            Map<String, Long> topNum = new LinkedHashMap<>();
            if (projectMemberList != null) {
                projectMemberList.stream().filter(o -> StrUtil.isNotEmpty(o.getJoinTime())).filter(o -> {
                    LocalDate join = LocalDateTime.parse(o.getJoinTime(), DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS)).toLocalDate();
                    return join.isAfter(finalBeginDate1.plusDays(-1)) && join.isBefore(finalDate1.plusDays(1));
                }).collect(Collectors.groupingBy(ProjectMember::getMemberCode, Collectors.counting()))
                        .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> topNum.put(x.getKey(), x.getValue()));
                topNum.forEach((key, val) -> {
                    Map<String, Object> topMap = new HashMap<>(4);
                    topMap.put("name", memberCodeName.get(key));
                    topMap.put("total", val);
                    projectTop.add(topMap);
                });
            }
        }
        //任务排行
        List<Task> memberTaskList = tasks == null ? null : tasks.parallelStream().filter(o -> StrUtil.isNotEmpty(o.getAssignTo())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(memberTaskList)) {
            Map<String, Long> taskTopNum = new LinkedHashMap<>();
            memberTaskList.stream().collect(Collectors.groupingBy(Task::getAssignTo, Collectors.counting()))
                    .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> taskTopNum.put(x.getKey(), x.getValue()));
            taskTopNum.forEach((key, val) -> {
                Map<String, Object> map = new HashMap<>(4);
                map.put("name", memberCodeName.get(key));
                map.put("total", val);
                taskTop.add(map);
            });
        }
        result.put("projectList", projectList);
        result.put("projectTop", projectTop);
        //任务数据
        result.put("taskList", taskList);
        result.put("taskTop", taskTop);
        return result;
    }

    public List<Task> taskPriority(String orgCode) {
        List<Project> list = lambdaQuery().select(Project::getCode).eq(Project::getOrganizationCode, orgCode).list();
        List<Task> taskList = null;
        if (list != null) {
            List<String> codes = list.parallelStream().map(Project::getCode).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(codes)) {
                taskList = taskProjectService.lambdaQuery().select(Task::getCode, Task::getName, Task::getPri, Task::getEndTime).ne(Task::getExecuteStatus, "closed")
                    .eq(Task::getDeleted, 0).eq(Task::getDone, 0).in(Task::getProjectCode, codes).list();
            }
            if (taskList != null) {
            	taskList = taskList.stream().filter(o -> o.getPri() != null && o.getEndTime() != null).sorted(Comparator.comparing(Task::getEndTime, Comparator.reverseOrder())).sorted(Comparator.comparing(Task::getPri, Comparator.reverseOrder())).collect(Collectors.toList());
            }
        }
        return taskList;
    }
}
