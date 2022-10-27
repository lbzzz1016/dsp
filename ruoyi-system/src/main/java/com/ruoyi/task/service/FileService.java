package com.ruoyi.task.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.CommUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.SourceLink;
import com.ruoyi.project.mapper.SourceLinkMapper;
import com.ruoyi.project.service.ProjectLogService;
import com.ruoyi.task.domain.File;
import com.ruoyi.task.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileService  extends ServiceImpl<FileMapper, File> {

    @Autowired
    SourceLinkMapper sourceLinkMapper;
    @Autowired
    ProjectLogService projectLogService;

    public Map getFileByCode(String fileCode){
        return baseMapper.selectFileByCode(fileCode);
    }

    public IPage<Map> gettFileByProjectCodeAndDelete(IPage<Map> page, Map params){
        return baseMapper.selectFileByProjectCodeAndDelete(page,params);
    }

    public  void recovery(String fileCode){
        File file = lambdaQuery().eq(File::getCode,fileCode).one();
        if(ObjectUtils.isEmpty(file)){
            throw new CustomException("文件不存在");
        }
        if(file.getDeleted()==0){
            throw new CustomException("文件已恢复");
        }
        lambdaUpdate().eq(File::getCode,fileCode).set(File::getDeleted,0).update();
    }
    public  void deleteFile(String fileCode){
        File file = lambdaQuery().eq(File::getCode,fileCode).one();
        if(ObjectUtils.isEmpty(file)){
            throw new CustomException("文件不存在");
        }
        lambdaUpdate().eq(File::getCode,fileCode).remove();
    }
    @Autowired
    TaskProjectService taskProjectService;
    @Transactional
    public Project uploadFiles(File file, String memberCode, String projectCode){
        file.setProjectCode(projectCode);
        file.setCreateBy(memberCode);
        if(StringUtils.isNotEmpty(file.getTaskCode())){

            SourceLink sourceLink = SourceLink.builder().source_type("file").code(CommUtils.getUUID()).
                    create_by(memberCode).organization_code(file.getOrganizationCode()).link_code(file.getTaskCode())
                    .link_type("task").source_code(file.getCode()).source_type("file").sort(0).build();
            sourceLinkMapper.insert(sourceLink);
        }
        baseMapper.insert(file);

        /**
         * is_comment
         * to_member_code
         * content
         * type
         * source_code
         * member_code
         *
         */
        Project project = projectLogService.run(new HashMap(){{
            put("is_comment",0);
            put("to_member_code","");
            put("content","");
            put("type","uploadFile");
            put("source_code",file.getTaskCode());
            put("member_code",memberCode);
            put("action_type","task");
            put("url",file.getFileUrl());
            put("title",file.getTitle());
            put("project_code",projectCode);
        }});
        return project;
    }
}
