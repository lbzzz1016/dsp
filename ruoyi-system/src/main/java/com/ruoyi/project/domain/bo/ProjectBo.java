package com.ruoyi.project.domain.bo;

import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.*;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目业务对象 project
 *
 * @author lbzzz
 * @date 2022-09-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("项目业务对象")
public class ProjectBo extends BaseEntity {

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 封面
     */
    @ApiModelProperty(value = "封面", required = true)
    @NotBlank(message = "封面不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cover;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号", required = true)
    @NotBlank(message = "编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", required = true)
    @NotBlank(message = "描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String description;

    /**
     * 访问控制l类型
     */
    @ApiModelProperty(value = "访问控制l类型", required = true)
    @NotBlank(message = "访问控制l类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String accessControlType;

    /**
     * 可以访问项目的权限组（白名单）
     */
    @ApiModelProperty(value = "可以访问项目的权限组（白名单）", required = true)
    @NotBlank(message = "可以访问项目的权限组（白名单）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String whiteList;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", required = true)
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer order;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记", required = true)
    @NotNull(message = "删除标记不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deleted;

    /**
     * 项目类型
     */
    @ApiModelProperty(value = "项目类型", required = true)
    @NotBlank(message = "项目类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String templateCode;

    /**
     * 进度
     */
    @ApiModelProperty(value = "进度", required = true)
    @NotNull(message = "进度不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal schedule;

    /**
     * 组织id
     */
    @ApiModelProperty(value = "组织id", required = true)
    @NotBlank(message = "组织id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String organizationCode;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间", required = true)
    @NotBlank(message = "删除时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deletedTime;

    /**
     * 是否私有
     */
    @ApiModelProperty(value = "是否私有", required = true)
    @NotNull(message = "是否私有不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer privated;

    /**
     * 项目前缀
     */
    @ApiModelProperty(value = "项目前缀", required = true)
    @NotBlank(message = "项目前缀不能为空", groups = { AddGroup.class, EditGroup.class })
    private String prefix;

    /**
     * 是否开启项目前缀
     */
    @ApiModelProperty(value = "是否开启项目前缀", required = true)
    @NotNull(message = "是否开启项目前缀不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer openPrefix;

    /**
     * 是否归档
     */
    @ApiModelProperty(value = "是否归档", required = true)
    @NotNull(message = "是否归档不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer archive;

    /**
     * 归档时间
     */
    @ApiModelProperty(value = "归档时间", required = true)
    @NotBlank(message = "归档时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String archiveTime;

    /**
     * 是否开启任务开始时间
     */
    @ApiModelProperty(value = "是否开启任务开始时间", required = true)
    @NotNull(message = "是否开启任务开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer openBeginTime;

    /**
     * 是否开启新任务默认开启隐私模式
     */
    @ApiModelProperty(value = "是否开启新任务默认开启隐私模式", required = true)
    @NotNull(message = "是否开启新任务默认开启隐私模式不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer openTaskPrivate;

    /**
     * 看板风格
     */
    @ApiModelProperty(value = "看板风格", required = true)
    @NotBlank(message = "看板风格不能为空", groups = { AddGroup.class, EditGroup.class })
    private String taskBoardTheme;

    /**
     * 项目开始日期
     */
    @ApiModelProperty(value = "项目开始日期", required = true)
    @NotBlank(message = "项目开始日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private String beginTime;

    /**
     * 项目截止日期
     */
    @ApiModelProperty(value = "项目截止日期", required = true)
    @NotBlank(message = "项目截止日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private String endTime;

    /**
     * 自动更新项目进度
     */
    @ApiModelProperty(value = "自动更新项目进度", required = true)
    @NotNull(message = "自动更新项目进度不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer autoUpdateSchedule;


}
