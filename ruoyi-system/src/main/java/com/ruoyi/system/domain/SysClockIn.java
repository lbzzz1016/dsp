package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 补卡流程对象 sys_clock_in
 *
 * @author lbzzz
 * @date 2023-04-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_clock_in")
@Builder
public class SysClockIn extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 流程id
     */
    @TableId(value = "process_id")
    private Long processId;
    /**
     * 工作流id
     */
    private String taskId;
    /**
     * 流程创建时间
     */
    private Date processCtime;
    /**
     * 流程结束时间
     */
    private Date processEtime;
    /**
     * 申请人id
     */
    private Long userId;
    /**
     * 申请人
     */
    private String userName;
    /**
     * 工号
     */
    private String userNumber;
    /**
     * 缺卡原因
     */
    private String reason;
    /**
     * 审批人
     */
    private String approver;
    /**
     * 补卡日期
     */
    private Date checkDate;
    /**
     * 补卡时间（上午、下午）
     */
    private String checkTime;
    /**
     * 流程状态（通过、未通过、删除）
     */
    private String status;

}
