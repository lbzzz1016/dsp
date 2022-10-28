package com.ruoyi.member.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.commo.mapper.CommMapper;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.member.domain.MemberAccount;
import com.ruoyi.member.mapper.MemberAccountMapper;
import com.ruoyi.project.domain.ProjectAuth;
import com.ruoyi.project.mapper.ProjectAuthMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MemberAccountService extends ServiceImpl<MemberAccountMapper, MemberAccount> {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private CommMapper commMapper;

    //根据orgCode获取memberCount
    public List<Map> getMemberCountByOrgCode(String orgCode){
        return  baseMapper.getMemberCountByOrgCode(orgCode);
    }
    //根据orgCode和name模糊查询获取memberCount
    public List<Map> getMemberCountByOrgCodeAndMemberName(String orgCode,String name){
        return  baseMapper.getMemberCountByOrgCodeAndMemberName(orgCode,name);
    }

    public  Map getMemberAccountByMemCodeAndOrgCode(String memberCode,String orgCode){
        return baseMapper.selectMemberAccountByMemCodeAndOrgCode(memberCode,orgCode);
    }

    public IPage<Map> getMemberAccountByOrgCodeStatusDeptCode(IPage page, Map params){
        return baseMapper.selectMemberAccountByOrgCodeStatusDeptCode(page,params);
    }

    public IPage<Map> getMemberAccountByOrgCodeAndStatus(IPage page, Map params){
        return baseMapper.selectMemberAccountByOrgCodeAndStatus(page,params);
    }

    //Account.php  public function index()
    public IPage<Map> getAccountIndex(IPage<Map> page, Map params, String orgCode) {
        String memberCode = MapUtils.getString(params, "memberCode");
        String departmentCode = MapUtils.getString(params, "departmentCode");
        String account = MapUtils.getString(params, "account");
        String mobile = MapUtils.getString(params, "mobile");
        String email = MapUtils.getString(params, "email");
        String keyword = MapUtils.getString(params, "keyword");
        Integer searchType = MapUtils.getInteger(params, "searchType", -1);
        String sql = " select * from team_member_account a where a.organization_code = '"+ orgCode +"' and 1=1 ";
        if(StringUtils.isNotEmpty(keyword)){
            sql += " and a.name like '%"+keyword+"%' ";
        }
        if(1==searchType){
            sql += " and a.status = 1";
        }else if(2==searchType){
            sql+= " and a.department_code = '' ";
        }else if(3==searchType){
            sql += " and a.status=0 ";
        }else if(4==searchType){
            sql += "  and a.status=1 ";
            sql += " and a.department_code like '%"+departmentCode+"%' ";
        }else{
            sql += "  and a.status=1 ";
        }
        if(StringUtils.isNotEmpty(account)){
            sql += " and a.account like '%"+account+"% ";
        }
        if(StringUtils.isNotEmpty(mobile)){
            sql += " and a.mobile like '%"+mobile+"% ";
        }
        if(StringUtils.isNotEmpty(email)){
            sql += " and a.email like '%"+email+"% ";
        }
        sql += " order by id asc";

        return commMapper.customQueryItem(page,sql);
    }

    public Map getMemberAccountByCode(String code){
        return baseMapper.selectMemberAccountByCode(code);
    }

    @Transactional
    public Integer memberAccountDel(String accountCode,String orgCode){

        Map memAccountMap = baseMapper.selectMemberAccountByCode(accountCode);
        List<Map> listMapProject = projectMapper.selectProjectByOrgCode(orgCode);
        List<String> projectCodes = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(listMapProject)){
            for(Map m:listMapProject){
                projectCodes.add(MapUtils.getString(m,"code"));
            }
        }
//        Map params = new HashMap();
//        params.put("proCodeList",projectCodes);
//        params.put("memCode",MapUtils.getString(memAccountMap,"member_code"));
        Integer delProjectMemberResult = projectMapper.delProjectMember(projectCodes,MapUtils.getString(memAccountMap,"member_code"));
        //Integer delProjectMemberResult = projectMapper.delProjectMember(params);


        Integer delMemberAccountResult = baseMapper.deleteById(MapUtils.getInteger(memAccountMap,"id"));

        Integer delDeptMemberAccountResult=baseMapper.deleteDepartmentMemberByAccCodeAndOrgCode(accountCode,orgCode);

        return delProjectMemberResult+delMemberAccountResult+delDeptMemberAccountResult;

    }

    public Map getMemberAccountByMemCode(String memCode){
        return baseMapper.selectMemberAccountByMemCode(memCode);
    }

    @Lazy
    @Autowired
    ISysUserService sysUserService;
    @Autowired
    ProjectAuthMapper projectAuthMapper;

    public MemberAccount inviteMember(MemberAccount memberAccount){

        MemberAccount hasJoined =lambdaQuery().eq(MemberAccount::getMemberCode,memberAccount.getMemberCode())
                .eq(MemberAccount::getOrganizationCode,memberAccount.getOrganizationCode()).one();
        if(ObjectUtils.isNotEmpty(hasJoined) && ObjectUtils.isNotEmpty(hasJoined.getId())){
            return memberAccount;
        }
        SysUser memberDate = sysUserService.lambdaQuery().eq(SysUser::getCode,memberAccount.getMemberCode())
                .one();
        if(ObjectUtil.isEmpty(memberDate)){
            throw new CustomException("该用户不存在");
        }
        LambdaQueryWrapper<ProjectAuth> projectAuthWQ = new LambdaQueryWrapper<>();
        projectAuthWQ.eq(ProjectAuth::getOrganizationCode,memberAccount.getOrganizationCode());
        projectAuthWQ.eq(ProjectAuth::getIsDefault,1);
        ProjectAuth pa = projectAuthMapper.selectOne(projectAuthWQ);
        if(ObjectUtils.isNotEmpty(pa)){
            memberAccount.setAuthorize(String.valueOf(pa.getId()));
        }
        memberAccount.setCode(CommUtils.getUUID());
        memberAccount.setIsOwner(0);
        memberAccount.setStatus(1);
        memberAccount.setCreateTime(DateUtils.getTime());
        memberAccount.setName(memberDate.getNickName());
        memberAccount.setEmail(memberDate.getEmail());
        save(memberAccount);
        return memberAccount;
    }

    public List<MemberAccount> selectMemberAccountList(String code) {
        return baseMapper.selectList(new LambdaQueryWrapper<MemberAccount>()
            .like(StringUtils.isNotBlank(code), MemberAccount::getMemberCode, code));
    }
}
