package com.ruoyi.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_task_to_tag")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskToTag extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String task_code;
    private String tag_code;
    private String create_time;
}
