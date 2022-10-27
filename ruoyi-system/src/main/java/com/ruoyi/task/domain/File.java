package com.ruoyi.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_file")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String pathName;
    private String title;
    private String extension;
    @TableField("size")
    private Long fsize;
    private String objectType;
    private String organizationCode;
    private String taskCode;
    private String projectCode;
    private String createBy;
    private String createTime;
    private Long downloads;
    private String extra;
    private Integer deleted;
    private String fileUrl;
    private String fileType;
    private String deletedTime;
	
	
    @TableField(exist = false)
    private String creatorName;
    @TableField(exist = false)
    private String fullName;
}
