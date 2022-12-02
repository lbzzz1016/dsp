package com.ruoyi.web.controller.member;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.AjaxResult;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.Constant;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.member.domain.Member;
import com.ruoyi.member.domain.MemberAccount;
import com.ruoyi.member.domain.ProjectMember;
import com.ruoyi.member.service.MemberAccountService;
import com.ruoyi.member.service.MemberService;
import com.ruoyi.member.service.ProjectMemberService;
import com.ruoyi.org.service.DepartmentService;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.ProjectAuthService;
import com.ruoyi.project.service.ProjectService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.task.domain.Task;
import com.ruoyi.task.service.TaskMemberService;
import com.ruoyi.task.service.TaskProjectService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class MemberController extends BaseController {
    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private ProjectAuthService projectAuthService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ISysUserService userService;

    /**
     * 账号停用
     * @param mmap
     * @return
     */
    @PostMapping("/index/editPersonal")
    @ResponseBody
    public AjaxResult indexEditPersonal(@RequestBody Map<String,Object> mmap) {
        String name = MapUtils.getString(mmap,"name");
        String description = MapUtils.getString(mmap,"description");
        String code = MapUtils.getString(mmap,"code");
        String avatar = MapUtils.getString(mmap,"avatar");
        Map memberMap = memberService.getMemberMapByCode(code);
        Map memberAccountMap = memberAccountService.getMemberAccountByMemCode(code);
        Member member = new Member();
        MemberAccount memberAccount = new MemberAccount();
        member.setId(MapUtils.getLong(memberMap,"id"));member.setName(name);member.setDescription(description);
        member.setAvatar(avatar);
        memberAccount.setId(MapUtils.getInteger(memberAccountMap,"id"));
        memberAccount.setAvatar(avatar);
        return AjaxResult.success("基本信息更新成功",memberService.updateMemberAccountAndMember(memberAccount,member));
    }

    @Autowired
    TaskProjectService taskService;
    @Autowired
    TaskMemberService taskMemberService;

    @PostMapping("/task_member/inviteMemberBatch")
    @ResponseBody
    public AjaxResult taskMemberInviteMemberBatch(@RequestBody Map<String,Object> mmap) {
        String memberCodes = MapUtils.getString(mmap,"memberCodes");
        String taskCode = MapUtils.getString(mmap,"taskCode");
        if(StringUtils.isEmpty(memberCodes) || StringUtils.isEmpty(taskCode)){
            return AjaxResult.warn("数据异常！");
        }
        Map loginMember = getLoginMember();
        Task task = taskService.getTaskByCode(taskCode);
        if(ObjectUtils.isEmpty(task)){
            return AjaxResult.warn("该任务已失效！");
        }
        taskMemberService.inviteMemberBatch(memberCodes,taskCode);
        return AjaxResult.success();
    }

    /**
     * 账号停用
     * @param mmap
     * @return
     */
    @PostMapping("/account/forbid")
    @ResponseBody
    public AjaxResult setAccountForbid(@RequestBody Map<String,Object> mmap) {
        String accountCode = MapUtils.getString(mmap,"accountCode");
        Integer status = MapUtils.getInteger(mmap,"status");

        MemberAccount ma = new MemberAccount();
        Map memberAccountMap = memberAccountService.getMemberAccountByCode(accountCode);
        if(MapUtils.isNotEmpty(memberAccountMap)){
            ma.setId(MapUtils.getInteger(memberAccountMap,"id"));
            ma.setStatus(status);
            AjaxResult.success(memberAccountService.updateById(ma));
        }
        return AjaxResult.success();
    }

    /**
     * 账号启用
     * @param mmap
     * @return
     */
    @PostMapping("/account/resume")
    @ResponseBody
    public AjaxResult setAccountResume(@RequestBody Map<String,Object> mmap) {
        String accountCode = MapUtils.getString(mmap,"accountCode");
        Integer status = MapUtils.getInteger(mmap,"status");

        MemberAccount ma = new MemberAccount();
        Map memberAccountMap = memberAccountService.getMemberAccountByCode(accountCode);
        if(MapUtils.isNotEmpty(memberAccountMap)){
            ma.setId(MapUtils.getInteger(memberAccountMap,"id"));
            ma.setStatus(status);
            AjaxResult.success(memberAccountService.updateById(ma));
        }
        return AjaxResult.success();
    }

    /**
     * 账号删除
     * @param mmap
     * @return
     */
    @PostMapping("/account/del")
    @ResponseBody
    public AjaxResult accountDel(@RequestBody Map<String,Object> mmap) {
        String accountCode = MapUtils.getString(mmap,"accountCode");
        Map loginMember = getLoginMember();

        memberAccountService.memberAccountDel(accountCode,MapUtils.getString(loginMember,"organizationCode"));
        return AjaxResult.success();
    }

    /**
     * 账号编辑
     * @param mmap
     * @return
     */
    @PostMapping("/account/edit")
    @ResponseBody
    public AjaxResult accountEdit(@RequestBody Map<String,Object> mmap) {
        String name = MapUtils.getString(mmap,"name");
        String mobile = MapUtils.getString(mmap,"mobile");
        String email = MapUtils.getString(mmap,"email");
        String description = MapUtils.getString(mmap,"description");
        String code = MapUtils.getString(mmap,"code");


        if(!CommUtils.isChinaPhoneLegal(mobile)){
            //return AjaxResult.warn("手机号错误！");
        }

        Map memAccMap = memberAccountService.getMemberAccountByCode(code);
        if(MapUtils.isNotEmpty(memAccMap)){
            MemberAccount ma = new MemberAccount();
            ma.setId(MapUtils.getInteger(memAccMap,"id"));
            ma.setName(name);ma.setMobile(mobile);ma.setEmail(email);ma.setDescription(description);
           return AjaxResult.success(memberAccountService.updateById(ma));
        }
        return AjaxResult.success();
    }

    /**
     * 账号编辑
     * @param mmap
     * @return
     */
    @PostMapping("/account/auth")
    @ResponseBody
    public AjaxResult accountAuth(@RequestBody Map<String,Object> mmap) {
        Integer auth = MapUtils.getInteger(mmap,"auth");
        Integer id = MapUtils.getInteger(mmap,"id");

        MemberAccount ma = new MemberAccount();
        ma.setId(id);
        ma.setAuthorize(String.valueOf(auth));
        return AjaxResult.success(memberAccountService.updateById(ma));
    }

    /**
     *首页 > 成员 >vilson >任务安排
     * @param mmap
     * @return
     */
    @PostMapping("/account/read")
    @ResponseBody
    public AjaxResult accountRead(@RequestBody Map<String,Object> mmap) {
        String code = MapUtils.getString(mmap,"code");
        Map memberAccountMap= memberAccountService.getMemberAccountByCode(code);
        String[] depCodeArr ,authorizeArr= null;
        Map depMap = null;
        String  depCodes = MapUtils.getString(memberAccountMap,"department_code","");
        String authorize = MapUtils.getString(memberAccountMap,"authorize","");
        depCodeArr = depCodes.split(",");
        authorizeArr = authorize.split(",");
        Integer status = MapUtils.getInteger(memberAccountMap,"status",-1);
        if(status == 0){
            memberAccountMap.put("statusText","禁用");
        }else if(status == 1){
            memberAccountMap.put("statusText","使用中");
        }
        String depName = "";
        if(null != depCodeArr) {
            for (String deptCode : depCodeArr) {
                depMap = departmentService.getDepartmentByCode(deptCode);
                if (MapUtils.isNotEmpty(depMap)) {
                    depName = ("".equals(depName)) ? MapUtils.getString(depMap,"name") : (depName + "-" + MapUtils.getString(depMap,"name") );
                }
            }
        }
        memberAccountMap.put("departments",depName);
        List authorizeArrList  = new ArrayList();
        if(null != authorizeArr){
            Map authMap = null;
            for(int i=0;i<authorizeArr.length;i++){
                authMap = new HashMap();
                authMap.put(i,authorizeArr[i]);
                authorizeArrList.add(authMap);
            }
        }
        memberAccountMap.put("authorizeArr",authorizeArrList);
        return AjaxResult.success(memberAccountMap);
    }

    /**
     *
     * @param mmap
     * @return
     */
    @PostMapping("/account/index")
    @ResponseBody
    public AjaxResult accountIndex(@RequestBody Map<String,Object> mmap) {

        String departmentCode = MapUtils.getString(mmap, "departmentCode");
        Map loginMember = getLoginMember();
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        Integer searchType = MapUtils.getInteger(mmap, "searchType",-1);
        IPage<Map> ipage = Constant.createPage(mmap);
        ipage = memberAccountService.getAccountIndex(ipage,mmap,orgCode);

        List<Map> records = ipage.getRecords();
        List<Map> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(records)){
            String depCodes,authorize = null;
            String[] depCodeArr ,authorizeArr= null;
            Map depMap = null;
            for(Map map:records){
                depCodes = MapUtils.getString(map,"department_code","");
                authorize = MapUtils.getString(map,"authorize","");

                map.put("membar_account_code",MapUtils.getString(map,"code"));
                depCodeArr = depCodes.split(",");
                authorizeArr = authorize.split(",");
                Integer status = MapUtils.getInteger(map,"status",-1);
                if(status == 0){
                    map.put("statusText","禁用");
                }else if(status == 1){
                    map.put("statusText","使用中");
                }
                String depName = "";
                if(null != depCodeArr) {
                    for (String deptCode : depCodeArr) {
                        depMap = departmentService.getDepartmentByCode(deptCode);
                        if (MapUtils.isNotEmpty(depMap)) {
                            depName = ("".equals(depName)) ? MapUtils.getString(depMap,"name") : (depName + "-" + MapUtils.getString(depMap,"name") );
                        }
                    }
                }
                map.put("departments",depName);
                List authorizeArrList  = new ArrayList();
                if(null != authorizeArr){
                    Map authMap = null;
                    for(int i=0;i<authorizeArr.length;i++){
                        authMap = new HashMap();
                        authMap.put(i,authorizeArr[i]);
                        authorizeArrList.add(authMap);
                    }
                }
                map.put("authorizeArr",authorizeArr);

                resultList.add(map);
            }
        }
        ipage.setRecords(resultList);
        Map resultData = Constant.createPageResultMap(ipage);
        List<Map> listProjectAuth = projectAuthService.getProjectAuthByStatusAndOrgCode("1",orgCode);
        List<Map> authList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(listProjectAuth)){
            for(Map map:listProjectAuth){
                map.put("id", map.get("id").toString());
                String type = MapUtils.getString(map,"type");
                if("admin".equals(type) || "member".equals(type)){
                    map.put("canDelete",0);
                }else{
                    map.put("canDelete",1);
                }
                authList.add(map);
            }
        }
        resultData.put("authList",authList);

        return AjaxResult.success(resultData);
    }

    /**
     * 项目管理	我的项目 邀请新成员 模糊查询
     * @param mmap
     * @return
     */
    @PostMapping("/project_member/searchInviteMember")
    @ResponseBody
    public AjaxResult searchInviteMember(@RequestBody Map<String,Object> mmap) {
        Map loginMember = getLoginMember();
        String projectCode = MapUtils.getString(mmap, "projectCode");
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        String keyword = MapUtils.getString(mmap,"keyword");

        List<Map> listMemberAccounts = memberAccountService.getMemberCountByOrgCodeAndMemberName(orgCode,keyword);
        List<Map> resultData = new ArrayList<>();
        if(!CollectionUtils.isEmpty(listMemberAccounts)){
            Map tmpMap = null;
            for(Map cm:listMemberAccounts){
                tmpMap = new HashMap();
                tmpMap.put("memberCode",MapUtils.getString(cm, "member_code",""));
                tmpMap.put("status",MapUtils.getString(cm, "status",""));
                tmpMap.put("avatar",MapUtils.getString(cm, "avatar",""));
                tmpMap.put("name",MapUtils.getString(cm, "name",""));
                tmpMap.put("email",MapUtils.getString(cm, "email",""));
                tmpMap.put("joined",projectMemberService.isProjectMember(projectCode,MapUtils.getString(cm, "member_code","")));
                resultData.add(tmpMap);
            }
        }
        return AjaxResult.success(resultData);
    }

    /**
     * 项目管理	我的项目 邀请新成员页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project_member/_listForInvite")
    @ResponseBody
    public AjaxResult _listForInvite(@RequestBody Map<String,Object> mmap) {
        Map loginMember = getLoginMember();
        String projectCode = MapUtils.getString(mmap, "projectCode");
        if(StringUtils.isEmpty(projectCode)){
            throw new CustomException("请先选择项目");
        }
        List<MemberAccount> listMemberAccounts = memberAccountService.lambdaQuery()
                // .eq(MemberAccount::getOrganizationCode, ServletUtils.getHeaderParam("organizationCode"))
                .list();
        List<Map> resultData = new ArrayList<>();
        if(!CollectionUtils.isEmpty(listMemberAccounts)){
            Map tmpMap = null;
            for(MemberAccount cm:listMemberAccounts){
                tmpMap = new HashMap();
                tmpMap.put("memberCode",cm.getMemberCode());
                tmpMap.put("status",cm.getStatus());
                tmpMap.put("avatar",cm.getAvatar());
                tmpMap.put("name",cm.getName());
                tmpMap.put("email",cm.getEmail());
                tmpMap.put("joined",projectMemberService.isProjectMember(projectCode,cm.getMemberCode()));
                resultData.add(tmpMap);
            }
        }
        return AjaxResult.success(resultData);
    }


    /**
     * 项目管理	我的项目 项目设置 任务流转  规则创建页面初始化
     * @param mmap
     * @return
     */
    @PostMapping("/project_member/index")
    @ResponseBody
    public AjaxResult getProject(@RequestBody Map<String,Object> mmap)
    {
        String projectCode = MapUtils.getString(mmap,"projectCode");
        Integer page = MapUtils.getInteger(mmap,"page",1);
        Integer pageSize = MapUtils.getInteger(mmap,"pageSize",100);

        IPage<ProjectMember> projectMemberPage = projectMemberService.lambdaQuery().eq(ProjectMember::getProjectCode,projectCode)
                .orderByDesc(ProjectMember::getIsOwner).page(Constant.createPage(new Page<ProjectMember>(),mmap));

        if(ObjectUtil.isNotEmpty(projectMemberPage) && !CollectionUtils.isEmpty(projectMemberPage.getRecords())){
            List<ProjectMember> projectMemberList = projectMemberPage.getRecords();
            List<Map> mapList = new ArrayList<>();
            projectMemberList.forEach(projectMember -> {
                Member member = memberService.lambdaQuery().eq(Member::getCode,projectMember.getMemberCode()).one();
                if(ObjectUtil.isNotEmpty(member)){
                    mapList.add(new HashMap(){{
                        put("name",member.getName());
                        put("avatar",member.getAvatar());
                        put("code",member.getCode());
                        put("email",member.getEmail());
                        put("is_owner",projectMember.getIsOwner());
                    }});
                }
            });
            return AjaxResult.success(Constant.createPageResultMap(mapList,projectMemberPage.getTotal(),projectMemberPage.getCurrent()));
        }

        /*ProjectMember pm = new ProjectMember();
        pm.setCurrent(page);pm.setSize(pageSize);pm.setProject_code(projectCode);

        IPage<Map> idata = projectMemberService.getProjectMemberByProjectCode(pm);
        if(null != idata){
            List<Map> list = idata.getRecords();
            List<Map> lrt = new ArrayList();
            Member member = null;Map map = null;
            Map prm = null;
            for(int i=0;i<list.size();i++){
                prm = list.get(i);
                if(null != prm){
                    member = memberService.getMemberByCode(MapUtils.getString(prm,"member_code"));
                    map = new HashMap();
                    map.put("name",member.getName());map.put("avatar",member.getAvatar());
                    map.put("code",member.getCode());map.put("email",member.getEmail());
                    lrt.add(map);
                }
            }
            return AjaxResult.success(Constant.createPageResultMap(lrt,idata.getTotal(),idata.getCurrent()));
        }*/
        return AjaxResult.success(new HashMap<>());
    }

    @PostMapping("/project_member/inviteMember")
    @ResponseBody
    public AjaxResult inviteMember(@RequestBody Map<String,Object> mmap)
    {
        String memberCode = MapUtils.getString(mmap,"memberCode");
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(projectCode)){
            return AjaxResult.warn("数据异常！");
        }
        /*Project project = projectService.getProjectByCodeNotDel(projectCode);
        if(ObjectUtils.isEmpty(project)){
            return AjaxResult.warn("该项目已失效！");
        }
        boolean bo = projectMemberService.isProjectMember(projectCode,memberCode);
        if(bo){
            return AjaxResult.success();
        }*/
        return AjaxResult.success(projectMemberService.inviteMember(memberCode,projectCode,0));
    }
    @PostMapping("/project_member/removeMember")
    @ResponseBody
    public AjaxResult removeMember(@RequestBody Map<String,Object> mmap)
    {
        String memberCode = MapUtils.getString(mmap,"memberCode");
        String projectCode = MapUtils.getString(mmap,"projectCode");
        if(StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(projectCode)){
            return AjaxResult.warn("数据异常！");
        }
        Project project = projectService.getProjectByCodeNotDel(projectCode);
        if(ObjectUtils.isEmpty(project)){
            return AjaxResult.warn("该项目已失效！");
        }
        boolean bo = projectMemberService.isProjectMember(projectCode,memberCode);
        if(!bo){
            return AjaxResult.success();
        }
        return AjaxResult.success(projectMemberService.removeMember(memberCode,project));

    }
}
