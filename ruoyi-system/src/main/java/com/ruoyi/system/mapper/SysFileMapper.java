package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.domain.vo.SysFileVo;
import com.ruoyi.common.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 文件Mapper接口
 *
 * @author lbzzz
 * @date 2022-09-23
 */
@Repository
@Mapper
public interface SysFileMapper extends BaseMapperPlus<SysFileMapper, SysFile, SysFileVo> {

    @Select("SELECT id, code,path_name,title,extension,size,object_type,organization_code,task_code,project_code,create_by,create_time,downloads,extra,deleted,file_url,file_type,deleted_time FROM team_file WHERE code = #{fileCode}")
    Map selectFileByCode(@Param("fileCode") String fileCode);

    @Select("SELECT * FROM team_file WHERE project_code = #{params.projectCode} AND deleted = #{params.deleted} ORDER BY id DESC")
    IPage<Map> selectFileByProjectCodeAndDelete(IPage<Map> page, @Param("params") Map params);

}
