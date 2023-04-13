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
 * 补卡流程视图对象 sys_clock_in
 *
 * @author lbzzz
 * @date 2023-04-12
 */
@Data
@ApiModel("补卡流程视图对象")
@ExcelIgnoreUnannotated
public class SysClockInVo {

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
     * 申请人id
     */
    @ExcelProperty(value = "申请人id")
    @ApiModelProperty("申请人id")
    private Long userId;

    /**
     * 申请人
     */
    @ExcelProperty(value = "申请人")
    @ApiModelProperty("申请人")
    private String userName;

    /**
     * 工号
     */
    @ExcelProperty(value = "工号")
    @ApiModelProperty("工号")
    private String userNumber;

    /**
     * 缺卡原因
     */
    @ExcelProperty(value = "缺卡原因")
    @ApiModelProperty("缺卡原因")
    private String reason;

    /**
     * 审批人
     */
    @ExcelProperty(value = "审批人")
    @ApiModelProperty("审批人")
    private String approver;

    /**
     * 补卡日期
     */
    @ExcelProperty(value = "补卡日期")
    @ApiModelProperty("补卡日期")
    private Date checkDate;

    /**
     * 补卡时间（上午、下午）
     */
    @ExcelProperty(value = "补卡时间", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "上=午、下午")
    @ApiModelProperty("补卡时间（上午、下午）")
    private String checkTime;

    /**
     * 流程状态（通过、未通过、删除）
     */
    @ExcelProperty(value = "流程状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "通=过、未通过、删除")
    @ApiModelProperty("流程状态（通过、未通过、删除）")
    private String status;


}
