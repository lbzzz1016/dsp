package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 留言板对象 sys_message_board
 *
 * @author lbzzz
 * @date 2022-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message_board")
public class SysMessageBoard extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 留言ID
     */
    @TableId(value = "message_id")
    private Long messageId;
    /**
     * 留言标题
     */
    private String messageTitle;
    /**
     * 留言类型（1bug 2建议 3需求）
     */
    private String messageType;
    /**
     * 留言内容
     */
    private String messageContent;
    /**
     * 留言状态（0正常 1关闭）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;

}
