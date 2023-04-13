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
 * 补卡流程业务对象 sys_clock_in
 *
 * @author lbzzz
 * @date 2023-04-12
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("补卡流程业务对象")
public class SysClockInBo extends BaseEntity {

    /**
     * 申请人
     */
    @ApiModelProperty(value = "申请人")
    private String userName;

    /**
     * 工号
     */
    @ApiModelProperty(value = "工号")
    private String userNumber;

    /**
     * 补卡日期
     */
    @ApiModelProperty(value = "补卡日期")
    private String checkDate;

    /**
     * 补卡时间（上午、下午）
     */
    @ApiModelProperty(value = "补卡时间（上午、下午）")
    private String checkTime;

    /**
     * 流程状态（通过、未通过、删除）
     */
    @ApiModelProperty(value = "流程状态（通过、未通过、删除）")
    private String status;


}
