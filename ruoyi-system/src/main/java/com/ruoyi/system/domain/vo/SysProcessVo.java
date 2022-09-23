package com.ruoyi.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;



/**
 * 请假流程视图对象 sys_process
 *
 * @author LBZ
 * @date 2022-09-15
 */
@Data
@ApiModel("请假流程视图对象")
@ExcelIgnoreUnannotated
public class SysProcessVo {

    private static final long serialVersionUID = 1L;

    /**
     * 流程id
     */
    @ExcelProperty(value = "流程id")
    @ApiModelProperty("流程id")
    private Long processId;

    /**
     * 工作流id
     */
    @ExcelProperty(value = "工作流id")
    @ApiModelProperty("工作流id")
    private String taskId;

    /**
     * 流程创建时间
     */
    @ExcelProperty(value = "流程创建时间")
    @ApiModelProperty("流程创建时间")
    private Date processCtime;

    /**
     * 流程结束时间
     */
    @ExcelProperty(value = "流程结束时间")
    @ApiModelProperty("流程结束时间")
    private Date processEtime;

    /**
     * 流程类型
     */
    @ExcelProperty(value = "流程类型")
    @ApiModelProperty("流程类型")
    private String processType;

    /**
     * 用户id
     */
    @ExcelProperty(value = "用户id")
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 申请人
     */
    @ExcelProperty(value = "申请人")
    @ApiModelProperty("申请人")
    private String userName;

    /**
     * 申请人部门
     */
    @ExcelProperty(value = "申请人部门")
    @ApiModelProperty("申请人部门")
    private String processDept;

    /**
     * 流程原因
     */
    @ExcelProperty(value = "流程原因")
    @ApiModelProperty("流程原因")
    private String processReason;

    /**
     * 审批人
     */
    @ExcelProperty(value = "审批人")
    @ApiModelProperty("审批人")
    private String approver;

    /**
     * 请假开始时间
     */
    @ExcelProperty(value = "请假开始时间")
    @ApiModelProperty("请假开始时间")
    private Date startTime;

    /**
     * 请假结束时间
     */
    @ExcelProperty(value = "请假结束时间")
    @ApiModelProperty("请假结束时间")
    private Date endTime;

    /**
     * 请假时长
     */
    @ExcelProperty(value = "请假时长")
    @ApiModelProperty("请假时长")
    private String processHours;

    /**
     * 流程状态（通过、未通过、删除）
     */
    @ExcelProperty(value = "流程状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "通=过、未通过、删除")
    @ApiModelProperty("流程状态（通过、未通过、删除）")
    private String status;


}
