package com.ruoyi.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@TableName("team_task_work_time")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskWorkTime  extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String taskCode;
    private String memberCode;
    private String createTime;
    private String content;
    private String beginTime;
    private Integer num;
    private String code;
}
