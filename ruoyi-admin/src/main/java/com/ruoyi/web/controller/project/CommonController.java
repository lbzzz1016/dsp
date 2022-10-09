package com.ruoyi.web.controller.project;

import com.ruoyi.common.config.MProjectConfig;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.ruoyi.common.utils.ServletUtils.getResponse;

@RestController
public class CommonController {

    /**
     * 通用下载请求
     *
     */
    @GetMapping("/common/download")
    public void fileDownload(@RequestParam Map<String,Object> mmap)
    {
        String filePathName =  MapUtils.getString(mmap,"filePathName");
        String realFileName =  MapUtils.getString(mmap,"realFileName");
        boolean delete =  MapUtils.getBoolean(mmap,"delete",false);
        try
        {
            /*if (!FileUtils.isValidFilename(filePathName))
            {
                throw new CustomException(StringUtils.format("文件名称({})非法，不允许下载。 ", filePathName));
            }*/
            String filePath = MProjectConfig.getProfile() + filePathName;

            getResponse().setCharacterEncoding("utf-8");
            getResponse().setContentType("multipart/form-data");
            getResponse().setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(ServletUtils.getRequest(), realFileName));
            FileUtils.writeBytes(filePath, getResponse().getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {

        }
    }

    @GetMapping("/common/image")
    public void image(@RequestParam Map<String,Object> mmap)
    {
        String filePathName =  MapUtils.getString(mmap,"filePathName");
        String realFileName =  MapUtils.getString(mmap,"realFileName");
        try
        {

            String filePath = MProjectConfig.getProfile() + filePathName;

            FileUtils.writeBytes(filePath, getResponse().getOutputStream());

        }
        catch (Exception e)
        {e.printStackTrace();
            throw new CustomException(e.getMessage());
        }
    }
}
