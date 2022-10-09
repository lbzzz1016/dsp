package com.ruoyi.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.org.domain.DepartmentMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface DepartmentMemberMapper extends BaseMapper<DepartmentMember> {

    @Select("select a.department_code from team_department_member a where a.account_code=#{accountCode} and a.organization_code=#{orgCode}")
    List<String> selectDepartmentCodes(@Param("accountCode") String accountCode, @Param("orgCode") String orgCode);
}
