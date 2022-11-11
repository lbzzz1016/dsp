package com.ruoyi.system.domain.bo;

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
 * 文件业务对象 sys_file
 *
 * @author lbzzz
 * @date 2022-09-23
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("文件业务对象")
public class SysFileBo extends BaseEntity {

    /**
     * 
     */
    @ApiModelProperty(value = "", required = true)
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号", required = true)
    @NotBlank(message = "编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 相对路径
     */
    @ApiModelProperty(value = "相对路径", required = true)
    @NotBlank(message = "相对路径不能为空", groups = { AddGroup.class, EditGroup.class })
    private String pathName;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 扩展名
     */
    @ApiModelProperty(value = "扩展名", required = true)
    @NotBlank(message = "扩展名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String extension;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小", required = true)
    @NotNull(message = "文件大小不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer size;

    /**
     * 对象类型
     */
    @ApiModelProperty(value = "对象类型", required = true)
    @NotBlank(message = "对象类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String objectType;

    /**
     * 组织编码
     */
    @ApiModelProperty(value = "组织编码", required = true)
    @NotBlank(message = "组织编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String organizationCode;

    /**
     * 任务编码
     */
    @ApiModelProperty(value = "任务编码", required = true)
    @NotBlank(message = "任务编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String taskCode;

    /**
     * 项目编码
     */
    @ApiModelProperty(value = "项目编码", required = true)
    @NotBlank(message = "项目编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String projectCode;

    /**
     * 下载次数
     */
    @ApiModelProperty(value = "下载次数", required = true)
    @NotNull(message = "下载次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer downloads;

    /**
     * 额外信息
     */
    @ApiModelProperty(value = "额外信息", required = true)
    @NotBlank(message = "额外信息不能为空", groups = { AddGroup.class, EditGroup.class })
    private String extra;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记", required = true)
    @NotNull(message = "删除标记不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer deleted;

    /**
     * 完整地址
     */
    @ApiModelProperty(value = "完整地址", required = true)
    @NotBlank(message = "完整地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fileUrl;

    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型", required = true)
    @NotBlank(message = "文件类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fileType;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间", required = true)
    @NotBlank(message = "删除时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deletedTime;


}
