package com.ruoyi.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.common.annotation.ExcelDictFormat;
import com.ruoyi.common.convert.ExcelDictConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;



/**
 * 留言板视图对象 sys_message_board
 *
 * @author lbzzz
 * @date 2022-12-07
 */
@Data
@ApiModel("留言板视图对象")
@ExcelIgnoreUnannotated
public class SysMessageBoardVo {

    private static final long serialVersionUID = 1L;

    /**
     * 留言ID
     */
    @ExcelProperty(value = "留言ID")
    @ApiModelProperty("留言ID")
    private Long messageId;

    /**
     * 留言标题
     */
    @ExcelProperty(value = "留言标题")
    @ApiModelProperty("留言标题")
    private String messageTitle;

    /**
     * 留言类型（1bug 2建议 3需求）
     */
    @ExcelProperty(value = "留言类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_board_type")
    @ApiModelProperty("留言类型（1bug 2建议 3需求）")
    private String messageType;

    /**
     * 留言内容
     */
    @ExcelProperty(value = "留言内容")
    @ApiModelProperty("留言内容")
    private String messageContent;

    /**
     * 留言状态（0正常 1关闭）
     */
    @ExcelProperty(value = "留言状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_message_board_status")
    @ApiModelProperty("留言状态（0正常 1关闭）")
    private String status;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("创建者")
    private String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    @ApiModelProperty("备注")
    private String remark;


}
