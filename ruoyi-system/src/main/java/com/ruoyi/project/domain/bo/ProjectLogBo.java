package com.ruoyi.project.domain.bo;

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
 * 项目日志业务对象 project_log
 *
 * @author lbzzz
 * @date 2022-09-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("项目日志业务对象")
public class ProjectLogBo extends BaseEntity {

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 操作人id
     */
    @ApiModelProperty(value = "操作人id", required = true)
    @NotBlank(message = "操作人id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String memberCode;

    /**
     * 操作内容
     */
    @ApiModelProperty(value = "操作内容", required = true)
    @NotBlank(message = "操作内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型", required = true)
    @NotBlank(message = "操作类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String type;

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", required = true)
    @NotBlank(message = "任务id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String sourceCode;

    /**
     * 场景类型
     */
    @ApiModelProperty(value = "场景类型", required = true)
    @NotBlank(message = "场景类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String actionType;

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String toMemberCode;

    /**
     * 是否评论，0：否
     */
    @ApiModelProperty(value = "是否评论，0：否", required = true)
    @NotNull(message = "是否评论，0：否不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isComment;

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String projectCode;

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotBlank(message = "不能为空", groups = { AddGroup.class, EditGroup.class })
    private String icon;

    /**
     * 是否机器人
     */
    @ApiModelProperty(value = "是否机器人", required = true)
    @NotNull(message = "是否机器人不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isRobot;


}
