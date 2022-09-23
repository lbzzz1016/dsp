package com.ruoyi.system.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 请假流程业务对象 sys_process
 *
 * @author LBZ
 * @date 2022-09-15
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("请假流程业务对象")
public class SysProcessBo extends BaseEntity {

    /**
     * 流程类型
     */
    @ApiModelProperty(value = "流程类型")
    private String processType;

    /**
     * 申请人
     */
    @ApiModelProperty(value = "申请人")
    private String userName;

    /**
     * 审批人
     */
    @ApiModelProperty(value = "审批人")
    private String approver;

    /**
     * 请假开始时间
     */
    @ApiModelProperty(value = "请假开始时间")
    private Date startTime;

    /**
     * 请假结束时间
     */
    @ApiModelProperty(value = "请假结束时间")
    private Date endTime;

    /**
     * 请假时长
     */
    @ApiModelProperty(value = "请假时长")
    private String processHours;

    /**
     * 流程状态（通过、未通过、删除）
     */
    @ApiModelProperty(value = "流程状态（通过、未通过、删除）")
    private String status;


}
