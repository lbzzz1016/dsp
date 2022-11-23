package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.project.domain.Project;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.domain.vo.SysFileVo;
import com.ruoyi.system.domain.bo.SysFileBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 文件Service接口
 *
 * @author lbzzz
 * @date 2022-09-23
 */
public interface ISysFileService extends IService<SysFile> {

    /**
     * 查询文件
     */
    SysFileVo queryById(Integer id);

    /**
     * 查询文件列表
     */
    TableDataInfo<SysFileVo> queryPageList(SysFileBo bo, PageQuery pageQuery);

    /**
     * 查询文件列表
     */
    List<SysFileVo> queryList(SysFileBo bo);

    /**
     * 修改文件
     */
    Boolean insertByBo(SysFileBo bo);

    /**
     * 修改文件
     */
    Boolean updateByBo(SysFileBo bo);

    /**
     * 校验并批量删除文件信息
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);

    /**
     * 根据文件编码查询文件
     * @param fileCode
     * @return
     */
    public Map getFileByCode(String fileCode);

    /**
     * 恢复文件
     * @param fileCode
     */
    public void recovery(String fileCode);

    /**
     * 删除文件
     * @param fileCode
     */
    public void deleteFile(String fileCode);

    /**
     * 移动文件
     * @param fileCode
     */
    public void moveFile(String fileCode, String fileType);

    /**
     * 上传文件
     * @param file
     * @param memberCode
     * @param projectCode
     * @return
     */
    public boolean uploadFiles(SysFile file, String memberCode, String projectCode);
}
