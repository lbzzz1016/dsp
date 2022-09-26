package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.bo.SysFileBo;
import com.ruoyi.system.domain.vo.SysFileVo;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.mapper.SysFileMapper;
import com.ruoyi.system.service.ISysFileService;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 文件Service业务层处理
 *
 * @author lbzzz
 * @date 2022-09-23
 */
@RequiredArgsConstructor
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    private final SysFileMapper baseMapper;

    /**
     * 查询文件
     */
    @Override
    public SysFileVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询文件列表
     */
    @Override
    public TableDataInfo<SysFileVo> queryPageList(SysFileBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysFile> lqw = buildQueryWrapper(bo);
        Page<SysFileVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询文件列表
     */
    @Override
    public List<SysFileVo> queryList(SysFileBo bo) {
        LambdaQueryWrapper<SysFile> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysFile> buildQueryWrapper(SysFileBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysFile> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), SysFile::getCode, bo.getCode());
        lqw.like(StringUtils.isNotBlank(bo.getPathName()), SysFile::getPathName, bo.getPathName());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), SysFile::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getExtension()), SysFile::getExtension, bo.getExtension());
        lqw.eq(bo.getSize() != null, SysFile::getFsize, bo.getSize());
        lqw.eq(StringUtils.isNotBlank(bo.getObjectType()), SysFile::getObjectType, bo.getObjectType());
        lqw.eq(StringUtils.isNotBlank(bo.getOrganizationCode()), SysFile::getOrganizationCode, bo.getOrganizationCode());
        lqw.eq(StringUtils.isNotBlank(bo.getTaskCode()), SysFile::getTaskCode, bo.getTaskCode());
        lqw.eq(StringUtils.isNotBlank(bo.getProjectCode()), SysFile::getProjectCode, bo.getProjectCode());
        lqw.eq(bo.getDownloads() != null, SysFile::getDownloads, bo.getDownloads());
        lqw.eq(StringUtils.isNotBlank(bo.getExtra()), SysFile::getExtra, bo.getExtra());
        lqw.eq(bo.getDeleted() != null, SysFile::getDeleted, bo.getDeleted());
        lqw.eq(StringUtils.isNotBlank(bo.getFileUrl()), SysFile::getFileUrl, bo.getFileUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getFileType()), SysFile::getFileType, bo.getFileType());
        lqw.eq(StringUtils.isNotBlank(bo.getDeletedTime()), SysFile::getDeletedTime, bo.getDeletedTime());
        return lqw;
    }

    /**
     * 新增文件
     */
    @Override
    public Boolean insertByBo(SysFileBo bo) {
        SysFile add = BeanUtil.toBean(bo, SysFile.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改文件
     */
    @Override
    public Boolean updateByBo(SysFileBo bo) {
        SysFile update = BeanUtil.toBean(bo, SysFile.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysFile entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除文件
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据文件编码查询文件
     * @param fileCode
     * @return
     */
    @Override
    public Map getFileByCode(String fileCode){
        return baseMapper.selectFileByCode(fileCode);
    }

    /**
     * 更新文件
     * @param fileCode
     */
    public void recovery(String fileCode){
        SysFile file = lambdaQuery().eq(SysFile::getCode,fileCode).one();
        if(ObjectUtils.isEmpty(file)){
            throw new CustomException("文件不存在");
        }
        if(file.getDeleted()==0){
            throw new CustomException("文件已恢复");
        }
        lambdaUpdate().eq(SysFile::getCode, fileCode).set(SysFile::getDeleted,0).update();
    }

    /**
     * 删除文件
     * @param fileCode
     */
    public void deleteFile(String fileCode) {
        SysFile file = lambdaQuery().eq(SysFile::getCode,fileCode).one();
        if(ObjectUtils.isEmpty(file)){
            throw new CustomException("文件不存在");
        }
        lambdaUpdate().eq(SysFile::getCode, fileCode).remove();
    }
}
