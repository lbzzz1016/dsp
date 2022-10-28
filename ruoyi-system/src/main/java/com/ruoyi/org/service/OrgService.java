package com.ruoyi.org.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.member.domain.Member;
import com.ruoyi.member.domain.MemberAccount;
import com.ruoyi.member.domain.vo.MemberVo;
import com.ruoyi.member.service.MemberAccountService;
import com.ruoyi.member.service.MemberService;
import com.ruoyi.org.domain.Department;
import com.ruoyi.org.domain.DepartmentMember;
import com.ruoyi.org.mapper.OrgMapper;
import com.ruoyi.project.domain.ProjectAuth;
import com.ruoyi.project.domain.ProjectMenu;
import com.ruoyi.project.service.ProjectAuthService;
import com.ruoyi.project.service.ProjectMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrgService{

    @Autowired
    private OrgMapper orgMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentMemberService departmentMemberService;
    @Autowired
    private ProjectAuthService projectAuthService;
    @Autowired
    private ProjectMenuService projectMenuService;

    public List<Map> selectOrgByMemCode(Map params) {
        return orgMapper.selectOrgByMemCode(params);
    }

    public List<Map> _getOrgList(Map params){
        return orgMapper._getOrgList(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<MemberVo> searchInviteMember(String organizationcode, String keyword, String departmentCode) {
        if (StrUtil.isEmpty(departmentCode)) {
            List<Member> memberList = memberService.lambdaQuery().select(Member::getCode, Member::getName, Member::getEmail, Member::getAvatar)
                    .like(Member::getName, keyword).or().eq(Member::getEmail, keyword).list();
            if (CollUtil.isNotEmpty(memberList)) {
                List<String> memberCodeList = memberList.parallelStream().map(Member::getCode).collect(Collectors.toList());
                Map<String, MemberAccount> memberMap = memberAccountService.lambdaQuery().in(MemberAccount::getMemberCode, memberCodeList)
                        .eq(MemberAccount::getOrganizationCode, organizationcode).list()
                        .parallelStream().collect(Collectors.toMap(MemberAccount::getMemberCode, o -> o));
                List<MemberVo> memberVoList = new ArrayList<>();
                memberList.forEach(o -> {
                    MemberVo memberVo;
                    MemberVo.MemberVoBuilder voBuilder = MemberVo.builder().name(o.getName()).accountCode(o.getCode()).avatar(o.getAvatar()).email(o.getEmail());
                    if (ObjectUtil.isNotEmpty(memberMap.get(o.getCode()))) {
                        memberVo = voBuilder.joined(true).build();
                    } else {
                        memberVo = voBuilder.joined(false).build();
                    }
                    memberVoList.add(memberVo);
                });
                return memberVoList;
            }
        } else {
            List<MemberAccount> memberAccountList = memberAccountService.lambdaQuery().eq(MemberAccount::getOrganizationCode, organizationcode)
                    .like(MemberAccount::getName, keyword).or().eq(MemberAccount::getEmail, keyword).list();
            List<MemberVo> memberVoList = new ArrayList<>();
            memberAccountList.forEach(o -> {
                MemberVo memberVo;
                MemberVo.MemberVoBuilder voBuilder = MemberVo.builder().name(o.getName()).accountCode(o.getCode()).avatar(o.getAvatar()).email(o.getEmail());
                
                if (StrUtil.isNotEmpty(o.getDepartmentCode()) && o.getDepartmentCode().contains(departmentCode)) {
                    memberVo = voBuilder.joined(true).build();
                } else {
                    memberVo = voBuilder.joined(false).build();
                }
                memberVoList.add(memberVo);
            });
            return memberVoList;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public String inviteMember(String organizationcode, String accountCode, String departmentCode) {
        if (StrUtil.isNotEmpty(departmentCode)) {
            Department department = departmentService.lambdaQuery().eq(Department::getCode, departmentCode).one();
            MemberAccount one = memberAccountService.lambdaQuery().eq(MemberAccount::getCode, accountCode).one();
            DepartmentMember saveDepartMember = DepartmentMember.builder().code(IdUtil.fastSimpleUUID()).departmentCode(departmentCode).organizationCode(organizationcode)
                    .accountCode(accountCode).isOwner(one.getIsOwner()).isPrincipal(one.getIsOwner()).authorize(one.getAuthorize())
                    .joinTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS))).build();
            boolean save = departmentMemberService.save(saveDepartMember);
            String depCode = StrUtil.isNotEmpty(one.getDepartmentCode()) ? one.getDepartmentCode() + "," + department.getCode() : department.getCode();
            String depStr = StrUtil.isNotEmpty(one.getDepartment()) ? one.getDepartment() + "-" + department.getName() : department.getName();
            boolean update = memberAccountService.lambdaUpdate().set(MemberAccount::getDepartment, depStr).set(MemberAccount::getDepartmentCode, depCode).eq(MemberAccount::getCode, accountCode).update();
            log.info("保存新部门：{}，更新用户信息：{}", save, update);
            return null;
        } else {
            ProjectAuth projectAuth = projectAuthService.lambdaQuery().select(ProjectAuth::getId).eq(ProjectAuth::getOrganizationCode, organizationcode)
                    .eq(ProjectAuth::getIsDefault, "1").one();
            Member one = memberService.lambdaQuery().eq(Member::getCode, accountCode).one();
            MemberAccount saveMemberAccount = MemberAccount.builder().code(IdUtil.fastSimpleUUID()).memberCode(accountCode).organizationCode(organizationcode)
                    .authorize(projectAuth.getId().toString()).name(one.getName()).mobile(one.getMobile()).email(one.getEmail()).avatar(one.getAvatar())
                    .status(1).createTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS))).build();
            boolean save = memberAccountService.save(saveMemberAccount);
            log.info("添加用户到组织：{}", save);
            return null;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Object removeMember(String organizationcode, String accountCode, String departmentCode) {
        MemberAccount one = memberAccountService.lambdaQuery().eq(MemberAccount::getCode, accountCode).one();
        List<String> depList = Arrays.stream(one.getDepartmentCode().split(",")).collect(Collectors.toList());
//        for (String str : depList) {
//            if (StrUtil.equals(str, departmentCode)) {
//                depList.remove(str);
//            }
//        }
       depList.removeIf(str -> StrUtil.equals(str, departmentCode));

        String depCode = null;
        String depStr = null;
        if (CollUtil.isNotEmpty(depList)) {
            List<Department> list = departmentService.lambdaQuery().select(Department::getCode, Department::getName).in(Department::getCode, depList).list();
            depCode = list.stream().map(Department::getCode).collect(Collectors.joining(","));
            depStr = list.stream().map(Department::getName).collect(Collectors.joining("-"));
        }
        boolean remove = departmentMemberService.remove(Wrappers.<DepartmentMember>lambdaQuery().eq(DepartmentMember::getAccountCode, accountCode)
                .eq(DepartmentMember::getDepartmentCode, departmentCode).eq(DepartmentMember::getOrganizationCode, organizationcode));
        boolean update = memberAccountService.lambdaUpdate().set(MemberAccount::getDepartment, depStr).set(MemberAccount::getDepartmentCode, depCode).eq(MemberAccount::getCode, accountCode).update();
        log.info("移除部门：{}，更新用户信息：{}", remove, update);
        return null;
    }

    public Map<String, Object> getCurrentUserMenu(String orgCode) {
        List<ProjectMenu> menuList = projectMenuService.getCurrentUserMenu();
        LoginUser member = LoginHelper.getLoginUser();
        Map<String, Object> result = new HashMap<>(4);
        result.put("menuList", menuList);
        result.put("member", member);
        return result;


    }

}
