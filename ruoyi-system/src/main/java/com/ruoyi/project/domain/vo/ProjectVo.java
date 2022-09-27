package com.ruoyi.project.domain.vo;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 项目视图对象 project
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@Data
@ApiModel("项目视图对象")
@ExcelIgnoreUnannotated
public class ProjectVo {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private Integer id;

    /**
     * 封面
     */
    @ExcelProperty(value = "封面")
    @ApiModelProperty("封面")
    private String cover;

    /**
     * 名称
     */
    @ExcelProperty(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    @ApiModelProperty("编号")
    private String code;

    /**
     * 描述
     */
    @ExcelProperty(value = "描述")
    @ApiModelProperty("描述")
    private String description;

    /**
     * 访问控制l类型
     */
    @ExcelProperty(value = "访问控制l类型")
    @ApiModelProperty("访问控制l类型")
    private String accessControlType;

    /**
     * 可以访问项目的权限组（白名单）
     */
    @ExcelProperty(value = "可以访问项目的权限组", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "白=名单")
    @ApiModelProperty("可以访问项目的权限组（白名单）")
    private String whiteList;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    @ApiModelProperty("排序")
    private Integer order;

    /**
     * 删除标记
     */
    @ExcelProperty(value = "删除标记")
    @ApiModelProperty("删除标记")
    private Integer deleted;

    /**
     * 项目类型
     */
    @ExcelProperty(value = "项目类型")
    @ApiModelProperty("项目类型")
    private String templateCode;

    /**
     * 进度
     */
    @ExcelProperty(value = "进度")
    @ApiModelProperty("进度")
    private BigDecimal schedule;

    /**
     * 组织id
     */
    @ExcelProperty(value = "组织id")
    @ApiModelProperty("组织id")
    private String organizationCode;

    /**
     * 删除时间
     */
    @ExcelProperty(value = "删除时间")
    @ApiModelProperty("删除时间")
    private String deletedTime;

    /**
     * 是否私有
     */
    @ExcelProperty(value = "是否私有")
    @ApiModelProperty("是否私有")
    private Integer privated;

    /**
     * 项目前缀
     */
    @ExcelProperty(value = "项目前缀")
    @ApiModelProperty("项目前缀")
    private String prefix;

    /**
     * 是否开启项目前缀
     */
    @ExcelProperty(value = "是否开启项目前缀")
    @ApiModelProperty("是否开启项目前缀")
    private Integer openPrefix;

    /**
     * 是否归档
     */
    @ExcelProperty(value = "是否归档")
    @ApiModelProperty("是否归档")
    private Integer archive;

    /**
     * 归档时间
     */
    @ExcelProperty(value = "归档时间")
    @ApiModelProperty("归档时间")
    private String archiveTime;

    /**
     * 是否开启任务开始时间
     */
    @ExcelProperty(value = "是否开启任务开始时间")
    @ApiModelProperty("是否开启任务开始时间")
    private Integer openBeginTime;

    /**
     * 是否开启新任务默认开启隐私模式
     */
    @ExcelProperty(value = "是否开启新任务默认开启隐私模式")
    @ApiModelProperty("是否开启新任务默认开启隐私模式")
    private Integer openTaskPrivate;

    /**
     * 看板风格
     */
    @ExcelProperty(value = "看板风格")
    @ApiModelProperty("看板风格")
    private String taskBoardTheme;

    /**
     * 项目开始日期
     */
    @ExcelProperty(value = "项目开始日期")
    @ApiModelProperty("项目开始日期")
    private String beginTime;

    /**
     * 项目截止日期
     */
    @ExcelProperty(value = "项目截止日期")
    @ApiModelProperty("项目截止日期")
    private String endTime;

    /**
     * 自动更新项目进度
     */
    @ExcelProperty(value = "自动更新项目进度")
    @ApiModelProperty("自动更新项目进度")
    private Integer autoUpdateSchedule;


}
