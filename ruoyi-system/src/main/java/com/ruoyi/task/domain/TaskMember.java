package com.ruoyi.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_task_member")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskMember  extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String taskCode;
    private Integer isExecutor;
    private String memberCode;
    private String joinTime;
    private Integer isOwner;
}
