package com.ruoyi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.project.domain.CollectionTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@SuppressWarnings("rawtypes")
@Repository
@Mapper
public interface CollectionTeamMapper extends BaseMapper<CollectionTeam> {

    @Select("SELECT * FROM `team_collection` WHERE `source_code` = #{sourceCode} AND `type` = 'task' AND `member_code` = #{memberCode} LIMIT 1")
    Map selectCollection(@Param("sourceCode") String sourceCode, @Param("memberCode") String memberCode);
}
