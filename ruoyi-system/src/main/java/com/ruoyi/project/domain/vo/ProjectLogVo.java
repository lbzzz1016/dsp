package com.ruoyi.project.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;



/**
 * 项目日志视图对象 project_log
 *
 * @author lbzzz
 * @date 2022-09-26
 */
@Data
@ApiModel("项目日志视图对象")
@ExcelIgnoreUnannotated
public class ProjectLogVo {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private Long id;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private String code;

    /**
     * 操作人id
     */
    @ExcelProperty(value = "操作人id")
    @ApiModelProperty("操作人id")
    private String memberCode;

    /**
     * 操作内容
     */
    @ExcelProperty(value = "操作内容")
    @ApiModelProperty("操作内容")
    private String content;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private String remark;

    /**
     * 操作类型
     */
    @ExcelProperty(value = "操作类型")
    @ApiModelProperty("操作类型")
    private String type;

    /**
     * 任务id
     */
    @ExcelProperty(value = "任务id")
    @ApiModelProperty("任务id")
    private String sourceCode;

    /**
     * 场景类型
     */
    @ExcelProperty(value = "场景类型")
    @ApiModelProperty("场景类型")
    private String actionType;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private String toMemberCode;

    /**
     * 是否评论，0：否
     */
    @ExcelProperty(value = "是否评论，0：否")
    @ApiModelProperty("是否评论，0：否")
    private Integer isComment;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private String projectCode;

    /**
     * 
     */
    @ExcelProperty(value = "")
    @ApiModelProperty("")
    private String icon;

    /**
     * 是否机器人
     */
    @ExcelProperty(value = "是否机器人")
    @ApiModelProperty("是否机器人")
    private Integer isRobot;


}
