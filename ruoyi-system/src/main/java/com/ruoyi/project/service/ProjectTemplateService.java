package com.ruoyi.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.commo.mapper.CommMapper;
import com.ruoyi.project.domain.ProjectTemplate;
import com.ruoyi.project.mapper.ProjectTemplateMapper;
import com.ruoyi.task.domain.TaskStagesTemplete;
import com.ruoyi.task.mapper.TaskStagesTempleteMapper;
import com.ruoyi.task.service.TaskStagesTempleteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProjectTemplateService extends ServiceImpl<ProjectTemplateMapper, ProjectTemplate> {

    @Autowired
    private TaskStagesTempleteService taskStagesTempleteService;
    @Autowired
    CommMapper commMapper;
    @Autowired
    private TaskStagesTempleteMapper taskStagesTempleteMapper;
    @Value("${mproject.downloadServer}")
    private String downloadServer;

    //根据orginationCode获取模板清单
    public IPage<ProjectTemplate> getProTemplateByOrgCode(ProjectTemplate projectTemplete){

        LambdaQueryWrapper<ProjectTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectTemplate::getOrganizationCode, projectTemplete.getOrganizationCode());
        if(-1 != projectTemplete.getIsSystem()){
            queryWrapper.eq(ProjectTemplate::getIsSystem, projectTemplete.getIsSystem());
        }
        IPage<ProjectTemplate> mapIPage = baseMapper.selectPage(projectTemplete.toPage(),queryWrapper);
        return mapIPage;
    }

    public IPage<Map> getProTemplateByOrgCode(IPage<Map> ipage, Map params){
        return baseMapper.getProTemplateByOrgCode(ipage,params);
    }

    public IPage<Map> getProjectTemplateIndex(IPage<Map> page, Map params){
        String memberCode = MapUtils.getString(params,"memberCode");
        String orgCode = MapUtils.getString(params,"orgCode");
        Integer viewType = MapUtils.getInteger(params,"viewType",-1);
        String sql = " select * from team_project_template as pt ";
        if(-1 == viewType){
            sql += String.format("  where pt.organization_code = '%s' or pt.is_system = 1",orgCode);
        }else if(1==viewType){
            sql += " where pt.is_system = 1";
        }else if(0==viewType){
            sql += String.format(" where pt.organization_code = '%s' and pt.is_system = 0",orgCode);
        }
        page = commMapper.customQueryItem(page,sql);
        List<Map> records = page.getRecords();
        List<Map> resultRecoreds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(records)){
            records.stream().forEach(map -> {
                map.put("task_stages",taskStagesTempleteMapper.selectByProjectTempleteCode(MapUtils.getString(map,"code")));
                resultRecoreds.add(map);
            });
        }
        page.setRecords(resultRecoreds);
        return page;
    }

    //添加项目模板和模板任务
    @Transactional
    public void saveProjectTemplateAndTaskStagesTemplage(ProjectTemplate pt, List<TaskStagesTemplete> lists){
        save(pt);
        taskStagesTempleteService.saveBatch(lists);
    }

    //删除模板和模板任务
    @Transactional
    public void deleteProjectTemplateAndTaskStagesTemplage(Integer ptId, List<Integer> lists){
        baseMapper.deleteById(ptId);
        if(lists != null && lists.size() != 0){
        	taskStagesTempleteService.removeByIds(lists);
        }
        
    }

    //根据 code获取projectTemplete
    public Map getProjectTemplateByCode(String projectTempCode){
        return baseMapper.getProjectTemplateByCode(projectTempCode);
    }


//    //上传项目封面图片
//    @Transactional
//    public Map uploadcover(String templateCode,String originFileName,InputStream in){
//        Map resMap = new HashMap();
//        String uuid = CommUtils.getUUID();
//        String date = DateUtils.dateTimeNow("yyyyMMdd");
//        String file_url = MProjectConfig.getProfile()+"/projectfile/project/cover/"+date+"/";
//        String uploadFileName = uuid+"-"+originFileName;
//        try {
//            // 这里使用Apache的FileUtils方法来进行保存
//            FileUtils.copyInputStreamToFile(in, new File(file_url, uploadFileName));
//            String base_url = "/projectfile/project/cover/"+date+"/"+uploadFileName;
//            String downloadUrl = "/common/image?filePathName="+base_url+"&realFileName="+originFileName;
//            resMap.put("base_url", base_url);
//            resMap.put("url",downloadServer+downloadUrl);
//            resMap.put("filename", uploadFileName);
//            if(!StringUtils.isEmpty(templateCode)){
//            	//编辑，否则 为空
//            	this.lambdaUpdate().eq(ProjectTemplate::getCode,templateCode)
//                .set(ProjectTemplate::getCover,downloadServer+downloadUrl).update();
//            }
//            
//
//        } catch (IOException e) {
//            throw new CustomException(e.getMessage());
//        }
//        return resMap;
//    }
    
}
