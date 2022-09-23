package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文件对象 sys_file
 *
 * @author lbzzz
 * @date 2022-09-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysFile extends BaseEntity {

    private static final long serialVersionUID=1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Integer id;
    /**
     * 编号
     */
    private String code;
    /**
     * 相对路径
     */
    private String pathName;
    /**
     * 名称
     */
    private String title;
    /**
     * 扩展名
     */
    private String extension;
    /**
     * 文件大小
     */
    @TableField("size")
    private Long fsize;
    /**
     * 对象类型
     */
    private String objectType;
    /**
     * 组织编码
     */
    private String organizationCode;
    /**
     * 任务编码
     */
    private String taskCode;
    /**
     * 项目编码
     */
    private String projectCode;
    /**
     * 下载次数
     */
    private Long downloads;
    /**
     * 额外信息
     */
    private String extra;
    /**
     * 删除标记
     */
    private Integer deleted;
    /**
     * 完整地址
     */
    private String fileUrl;
    /**
     * 文件类型
     */
    @TableField(exist = false)
    private String fileType;
    /**
     * 删除时间
     */
    @TableField(exist = false)
    private String deletedTime;

}
