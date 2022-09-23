package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;



/**
 * 文件视图对象 sys_file
 *
 * @author lbzzz
 * @date 2022-09-23
 */
@Data
@ApiModel("文件视图对象")
@ExcelIgnoreUnannotated
public class SysFileVo {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private Integer id;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    @ApiModelProperty("编号")
    private String code;

    /**
     * 相对路径
     */
    @ExcelProperty(value = "相对路径")
    @ApiModelProperty("相对路径")
    private String pathName;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    @ApiModelProperty("名称")
    private String title;

    /**
     * 扩展名
     */
    @ExcelProperty(value = "扩展名")
    @ApiModelProperty("扩展名")
    private String extension;

    /**
     * 文件大小
     */
    @ExcelProperty(value = "文件大小")
    @ApiModelProperty("文件大小")
    private Integer size;

    /**
     * 对象类型
     */
    @ExcelProperty(value = "对象类型")
    @ApiModelProperty("对象类型")
    private String objectType;

    /**
     * 组织编码
     */
    @ExcelProperty(value = "组织编码")
    @ApiModelProperty("组织编码")
    private String organizationCode;

    /**
     * 任务编码
     */
    @ExcelProperty(value = "任务编码")
    @ApiModelProperty("任务编码")
    private String taskCode;

    /**
     * 项目编码
     */
    @ExcelProperty(value = "项目编码")
    @ApiModelProperty("项目编码")
    private String projectCode;

    /**
     * 下载次数
     */
    @ExcelProperty(value = "下载次数")
    @ApiModelProperty("下载次数")
    private Integer downloads;

    /**
     * 额外信息
     */
    @ExcelProperty(value = "额外信息")
    @ApiModelProperty("额外信息")
    private String extra;

    /**
     * 删除标记
     */
    @ExcelProperty(value = "删除标记")
    @ApiModelProperty("删除标记")
    private Integer deleted;

    /**
     * 完整地址
     */
    @ExcelProperty(value = "完整地址")
    @ApiModelProperty("完整地址")
    private String fileUrl;

    /**
     * 文件类型
     */
    @ExcelProperty(value = "文件类型")
    @ApiModelProperty("文件类型")
    private String fileType;

    /**
     * 删除时间
     */
    @ExcelProperty(value = "删除时间")
    @ApiModelProperty("删除时间")
    private String deletedTime;


}
