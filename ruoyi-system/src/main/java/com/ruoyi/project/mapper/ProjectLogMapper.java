package com.ruoyi.project.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.project.domain.ProjectLog;
import com.ruoyi.project.domain.vo.ProjectLogVo;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 项目日志Mapper接口
 *
 * @author lbzzz
 * @date 2022-09-26
 */
public interface ProjectLogMapper extends BaseMapperPlus<ProjectLogMapper, ProjectLog, ProjectLogVo> {
    @Select("SELECT * FROM team_project_log WHERE source_code = #{params.sourceCode} AND action_type = #{params.actionType}")
    IPage<Map> selectProjectLogByParam(IPage<Map> iPage, @Param("params") Map params);

    @Select("select * from team_project_log where action_type='task' and source_code=#{sourceCode} and type='done' order by id desc ")
    List<Map> selectProjectLogBySourceCode(@Param("sourceCode") String sourceCode);

}
