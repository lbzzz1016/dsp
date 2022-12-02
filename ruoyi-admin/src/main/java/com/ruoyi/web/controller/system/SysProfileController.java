package com.ruoyi.web.controller.system;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.AjaxResult;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.MProjectConfig;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.system.domain.SysFile;
import com.ruoyi.system.domain.SysOss;
import com.ruoyi.system.service.ISysFileService;
import com.ruoyi.system.service.ISysOssService;
import com.ruoyi.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 *
 * @author LBZ
 */
@Validated
@Api(value = "个人信息控制器", tags = {"个人信息管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    private final ISysUserService userService;
    private final ISysOssService iSysOssService;
    private final ISysFileService iSysFileService;
    @Value("${mproject.downloadServer}")
    private String downloadServer;

    /**
     * 个人信息
     */
    @ApiOperation("个人信息")
    @GetMapping
    public R<Map<String, Object>> profile() {
        SysUser user = userService.selectUserById(getUserId());
        Map<String, Object> ajax = new HashMap<>();
        ajax.put("user", user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(user.getUserName()));
        ajax.put("postGroup", userService.selectUserPostGroup(user.getUserName()));
        return R.ok(ajax);
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改用户")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateProfile(@RequestBody SysUser user) {
        if (StringUtils.isNotEmpty(user.getPhonenumber())
            && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail())
            && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(getUserId());
        user.setUserName(null);
        user.setPassword(null);
        if (userService.updateUserProfile(user) > 0) {
            return R.ok();
        }
        return R.fail("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "oldPassword", value = "旧密码", paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "newPassword", value = "新密码", paramType = "query", dataTypeClass = String.class)
    })
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(String oldPassword, String newPassword) {
        SysUser user = userService.selectUserById(LoginHelper.getUserId());
        String userName = user.getUserName();
        String password = user.getPassword();
        if (!BCrypt.checkpw(oldPassword, password)) {
            return R.fail("修改密码失败，旧密码错误");
        }
        if (BCrypt.checkpw(newPassword, password)) {
            return R.fail("新密码不能与旧密码相同");
        }

        if (userService.resetUserPwd(userName, BCrypt.hashpw(newPassword)) > 0) {
            return R.ok();
        }
        return R.fail("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
//    @ApiOperation("头像上传")
//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "avatarfile", value = "用户头像", paramType = "query", dataTypeClass = File.class, required = true)
//    })
//    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    @ResponseBody
    public R<Map<String, Object>> avatar(@RequestPart("avatarfile") MultipartFile multipartFile) throws Exception {
        Map<String, Object> ajax = new HashMap<>();

        int  chunkNumber= 1;
        int  totalChunks= 1;
        String  taskCode= "avatar";
        String projectCode = "avatar";
        Map loginMember = getLoginMember();
        String orgCode = MapUtils.getString(loginMember,"organizationCode");
        String memberCode = MapUtils.getString(loginMember,"memberCode");
        if (multipartFile.isEmpty()) {
            return  R.fail("上传图片异常，请联系管理员");
        } else {
            String dateTimeNow = DateUtils.dateTimeNow();
            String date = DateUtils.dateTimeNow("yyyyMMdd");
            String uuid = IdUtil.simpleUUID();
            // 文件原名称
            String originFileName = multipartFile.getOriginalFilename().toString();
            // 上传文件重命名
            String uploadFileName = uuid+"-"+originFileName;
            String file_url = MProjectConfig.getProfile()+"/avatarfile/"+memberCode+"/"+date+"/";
            String base_url = "/avatarfile/"+memberCode+"/"+date+"/"+uploadFileName;
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

            if(chunkNumber == totalChunks) {
                //分片读写结束
                //重命名，以免重复
                File newFile = new File(file_url, uploadFileName);
                tempFile.renameTo(newFile);
                SysFile file = SysFile.builder().fsize(0L)
                    .pathName(file_url + uploadFileName)
                    .fileUrl(downloadServer + downloadUrl)
                    .title(originFileName.substring(0, originFileName.lastIndexOf(".")))
                    .fileType("avatar")
                    .code(uuid)
                    .organizationCode(orgCode)
                    .projectCode(projectCode)
                    .deleted(0)
                    .downloads(0l)
                    .taskCode(taskCode)
                    .extension(originFileName.substring(originFileName.lastIndexOf(".") + 1)).build();
                boolean flag = iSysFileService.uploadFiles(file, memberCode, projectCode);
                String avatar = file.getFileUrl();
                if (userService.updateUserAvatar(getUsername(), avatar)) {
                    ajax.put("imgUrl", avatar);
                    return R.ok(ajax);
                }
            }
        }
        return R.fail("上传图片异常，请联系管理员");
    }
}
