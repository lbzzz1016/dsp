package com.ruoyi.web.controller.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;

import cn.hutool.core.util.IdUtil;
import com.amazonaws.event.DeliveryMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.AjaxResult;
import com.ruoyi.common.config.MProjectConfig;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.Constant;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.member.domain.Member;
import com.ruoyi.member.service.MemberService;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.ProjectInfoService;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.SysFileVo;
import com.ruoyi.system.domain.bo.SysFileBo;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件Controller
 *
 * @author lbzzz
 * @date 2022-09-23
 */
@Validated
@Api(value = "文件控制器", tags = {"文件管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController {

    private final ISysFileService iSysFileService;

    private final ISysUserService iSysUserService;

    private final MemberService memberService;

    private final ProjectInfoService projectInfoService;
    /**
     * 查询文件列表
     */
    @ApiOperation("查询文件列表")
    @SaCheckPermission("system:file:list")
    @GetMapping("/list")
    public TableDataInfo<SysFileVo> list(SysFileBo bo, PageQuery pageQuery) {
        return iSysFileService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出文件列表
     */
    @ApiOperation("导出文件列表")
    @SaCheckPermission("system:file:export")
    @Log(title = "文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysFileBo bo, HttpServletResponse response) {
        List<SysFileVo> list = iSysFileService.queryList(bo);
        ExcelUtil.exportExcel(list, "文件", SysFileVo.class, response);
    }

    /**
     * 获取文件详细信息
     */
    @ApiOperation("获取文件详细信息")
    @SaCheckPermission("system:file:query")
    @GetMapping("/{id}")
    public R<SysFileVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("id") Integer id) {
        return R.ok(iSysFileService.queryById(id));
    }

    /**
     * 新增文件
     */
    @ApiOperation("新增文件")
    @SaCheckPermission("system:file:add")
    @Log(title = "文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysFileBo bo) {
        return toAjax(iSysFileService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改文件
     */
    @ApiOperation("修改文件")
    @SaCheckPermission("system:file:edit")
    @Log(title = "文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysFileBo bo) {
        return toAjax(iSysFileService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @SaCheckPermission("system:file:remove")
    @Log(title = "文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Integer[] ids) {
        return toAjax(iSysFileService.deleteWithValidByIds(Arrays.asList(ids), true) ? 1 : 0);
    }

    /**
     * 我的文件	移入回收站
     * @param
     * @return
     */
    @PostMapping("/project_info/index")
    @ResponseBody
    public AjaxResult projectProjectInfo(@RequestParam Map<String,Object> mmap){
        String projectCode = MapUtils.getString(mmap,"projectCode");
        List<Map> projectInfoList = projectInfoService.getProjectInfoByProjectCode(projectCode);
        return AjaxResult.success(projectInfoList);

    }

    /**
     * 我的文件	移入回收站
     * @param
     * @return
     */
    @PostMapping("/recycle")
    @ResponseBody
    public AjaxResult projectFileRecycle(@RequestBody Map<String,Object> mmap){
        String fileCode = MapUtils.getString(mmap,"fileCode");

        Map fileMap = iSysFileService.getFileByCode(fileCode);
        if(MapUtils.isEmpty(fileMap)){
            return  AjaxResult.warn("文件不存在");
        }
        if(1== MapUtils.getInteger(fileMap,"deleted")){
            return  AjaxResult.warn("文件已在回收站");
        }
        SysFile projectFile = new SysFile();
        projectFile.setId(MapUtils.getLong(fileMap,"id"));
        projectFile.setDeleted(1);
        projectFile.setDeletedTime(DateUtils.getTime());
        return AjaxResult.success(iSysFileService.updateById(projectFile));
    }
    /**
     * 我的文件	改名
     * @param
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult projectFileEdit(@RequestBody Map<String,Object> mmap){
        String title = MapUtils.getString(mmap,"title");
        String fileCode = MapUtils.getString(mmap,"fileCode");

        Map fileMap = iSysFileService.getFileByCode(fileCode);
        SysFile projectFile = new SysFile();
        projectFile.setId(MapUtils.getLong(fileMap,"id"));
        projectFile.setTitle(title);
        String extension = MapUtils.getString(fileMap, "extension");
        String projectCode = MapUtils.getString(fileMap, "project_code");
        if (!checkFile(title, extension, projectCode)) {
            AjaxResult.error("存在同名文件！");
        }
        return AjaxResult.success(iSysFileService.updateById(projectFile));
    }

    /**
     * 我的文件清单
     * @param
     * @return
     */
    @PostMapping("/index")
    @ResponseBody
    public AjaxResult getProjectFile(@RequestBody Map<String,Object> mmap){
        Integer deleted = MapUtils.getInteger(mmap,"deleted",0);
        IPage<SysFile> page_ = Constant.createPage(new Page<SysFile>(),mmap);
        page_=iSysFileService.lambdaQuery().eq(SysFile::getDeleted,0).page(page_);
        List<SysFile> resultList = new ArrayList<>();
        for(int i=0;page_ !=null && page_.getRecords() !=null && i<page_.getRecords().size();i++){
            SysFile f = page_.getRecords().get(i);
            Member member = memberService.lambdaQuery().eq(Member::getCode,f.getCreateBy()).one();
            f.setCreatorName(member.getName());
            f.setFullName(f.getTitle()+"."+f.getExtension());
            resultList.add(f);
        }
        page_.setRecords(resultList);
        Map data = Constant.createPageResultMap(page_);
        return AjaxResult.success(data);
    }

    @Value("${mproject.downloadServer}")
    private String downloadServer;


    /**
     * 每一个上传块都会包含如下分块信息：
     * chunkNumber: 当前块的次序，第一个块是 1，注意不是从 0 开始的。
     * totalChunks: 文件被分成块的总数。
     * chunkSize: 分块大小，根据 totalSize 和这个值你就可以计算出总共的块数。注意最后一块的大小可能会比这个要大。
     * currentChunkSize: 当前块的大小，实际大小。
     * totalSize: 文件总大小。
     * identifier: 这个就是每个文件的唯一标示。
     * filename: 文件名。
     * relativePath: 文件夹上传的时候文件的相对路径属性。
     * 一个分块可以被上传多次，当然这肯定不是标准行为，但是在实际上传过程中是可能发生这种事情的，这种重传也是本库的特性之一。
     *
     * 根据响应码认为成功或失败的：
     * 200 文件上传完成
     * 201 文加快上传成功
     * 500 第一块上传失败，取消整个文件上传
     * 507 服务器出错自动重试该文件块上传
     *
     * 此处仍不完善，未处理断点续传加密、秒传处理等。
     */
    @PostMapping("/uploadFiles")
    @ResponseBody
    public AjaxResult uploadFiles(HttpServletRequest request, @RequestParam(value = "file") MultipartFile multipartFile)  throws Exception{
        String  fileName= request.getParameter("identifier");
        String  orgFileName= request.getParameter("filename");
        int  chunkNumber= request.getParameter("chunkNumber") == null ?0:new Integer(request.getParameter("chunkNumber"));
        int  totalChunks= request.getParameter("totalChunks") == null ?0:new Integer(request.getParameter("totalChunks"));

        String  taskCode= "taskCode";
        String projectCode = "projectCode";
        Map loginMember = getLoginMember();
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        String memberCode = MapUtils.getString(loginMember,"memberCode");
        if (multipartFile.isEmpty()) {
            return  AjaxResult.warn("文件不能为空！");
        } else {
            String dateTimeNow = DateUtils.dateTimeNow();
            String date = DateUtils.dateTimeNow("yyyyMMdd");
            String uuid = IdUtil.simpleUUID();
            // 文件原名称
            String originFileName = multipartFile.getOriginalFilename().toString();
            //校验重名文件
            String strTitle = originFileName.substring(0,originFileName.lastIndexOf("."));
            String strExtension = originFileName.substring(originFileName.lastIndexOf(".")+1);
            if (!checkFile(strTitle, strExtension, projectCode)) {
                return AjaxResult.error("存在同名文件！");
            }
            // 上传文件重命名
            String uploadFileName = uuid+"-"+originFileName;
            //String file_url = MProjectConfig.getUploadFolderPath()+memberCode+"/"+date+"/"+dateTimeNow+uploadFileName;
            //String base_url = MProjectConfig.getStaticUploadPrefix()+memberCode+"/"+date+"/"+dateTimeNow+uploadFileName;
            String file_url = MProjectConfig.getProfile()+"/openfile/"+memberCode+"/"+date+"/";
            String base_url = "/openfile/"+memberCode+"/"+date+"/"+uploadFileName;
            String downloadUrl = "/common/download?filePathName="+base_url+"&realFileName="+originFileName;
            // 这里使用Apache的FileUtils方法来进行保存
            //FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(file_url, uploadFileName));
            File tempFile= new File(file_url, originFileName);
            Long fileSize = 0L;
            //第一个块,则新建文件
            if(1==chunkNumber && !tempFile.exists()){
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), tempFile);
            }else{
                //进行写文件操作
                try(
                    //将块文件写入文件中
                    InputStream fos=multipartFile.getInputStream();
                    RandomAccessFile raf =new RandomAccessFile(tempFile,"rw")
                ) {
                    int len=-1;
                    byte[] buffer=new byte[1024];
                    raf.seek((chunkNumber-1)*1024*1024);
                    while((len=fos.read(buffer))!=-1){
                        raf.write(buffer,0,len);
                    }
                    //文件大小
                    fileSize = raf.length();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(chunkNumber==1) {
                        tempFile.delete();
                    }
                    throw new Exception("读写文件错误!");
                }
            }

            if(chunkNumber == totalChunks){
                //分片读写结束
                //重命名，以免重复
                File newFile  = new File(file_url, uploadFileName);
                tempFile.renameTo(newFile);
                SysFile file = SysFile.builder().fsize(fileSize)
                    .pathName(file_url+uploadFileName)
                    .fileUrl(downloadServer+downloadUrl)
                    .title(originFileName.substring(0,originFileName.lastIndexOf(".")))
                    .fileType("text/plain")
                    .code(uuid)
                    .organizationCode(orgCode)
                    .projectCode(projectCode)
                    .deleted(0)
                    .downloads(0l)
                    .taskCode(taskCode)
                    .extension(originFileName.substring(originFileName.lastIndexOf(".")+1)).build();
                boolean flag = iSysFileService.uploadFiles(file,memberCode,projectCode);
                Map result = new HashMap();
                result.put("key",file.getPathName());
                result.put("url",file.getFileUrl());
                result.put("flag",flag);
                return AjaxResult.success(result);
            }else {
                //正常返回
                return AjaxResult.success();
            }
        }
    }

    @PostMapping("/recovery")
    @ResponseBody
    public AjaxResult fileRecovery(@RequestBody Map<String,Object> mmap) {
        String fileCode = MapUtils.getString(mmap,"fileCode");
        iSysFileService.recovery(fileCode);
        return  AjaxResult.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult deleteFile(@RequestBody Map<String,Object> mmap) {
        String fileCode = MapUtils.getString(mmap,"fileCode");
        iSysFileService.deleteFile(fileCode);
        return  AjaxResult.success();
    }

    public Boolean checkFile(String title, String extension, String projectCode) {
        SysFile sysFile = iSysFileService.lambdaQuery().eq(SysFile::getTitle, title)
                            .eq(SysFile::getExtension, extension)
                            .eq(SysFile::getProjectCode, projectCode)
                            .eq(SysFile::getDeleted, 0).one();
        return sysFile == null ? true : false;
    }
}
