package com.ruoyi.system.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 留言板业务对象 sys_message_board
 *
 * @author lbzzz
 * @date 2022-12-07
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("留言板业务对象")
public class SysMessageBoardBo extends BaseEntity {


    /**
     * 留言ID
     */
    @ExcelProperty(value = "留言ID")
    @ApiModelProperty("留言ID")
    private Long messageId;

    /**
     * 留言标题
     */
    @ApiModelProperty(value = "留言标题", required = true)
    @NotBlank(message = "留言标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String messageTitle;

    /**
     * 留言类型（1bug 2建议 3需求）
     */
    @ApiModelProperty(value = "留言类型（1bug 2建议 3需求）", required = true)
    @NotBlank(message = "留言类型（1bug 2建议 3需求）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String messageType;

    /**
     * 留言内容
     */
    @ApiModelProperty(value = "留言内容", required = true)
    @NotBlank(message = "留言内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String messageContent;

    /**
     * 留言状态（0正常 1关闭）
     */
    @ApiModelProperty(value = "留言状态（0正常 1关闭）", required = true)
    @NotBlank(message = "留言状态（0正常 1关闭）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;


}
