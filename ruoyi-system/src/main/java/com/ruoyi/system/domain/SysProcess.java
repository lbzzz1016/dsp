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
 * 请假流程对象 sys_process
 *
 * @author LBZ
 * @date 2022-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_process")
@Builder
public class SysProcess extends BaseEntity {

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
     * 流程类型
     */
    private String processType;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 申请人
     */
    private String userName;
    /**
     * 申请人部门
     */
    private String processDept;
    /**
     * 流程原因
     */
    private String processReason;
    /**
     * 审批人
     */
    private String approver;
    /**
     * 请假开始时间
     */
    private Date startTime;
    /**
     * 请假结束时间
     */
    private Date endTime;
    /**
     * 请假时长
     */
    private String processHours;
    /**
     * 流程状态（通过、未通过、删除）
     */
    private String status;

}
