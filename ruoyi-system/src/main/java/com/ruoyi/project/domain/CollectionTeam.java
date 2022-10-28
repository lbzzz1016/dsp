package com.ruoyi.project.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@TableName("team_collection")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CollectionTeam extends BaseDomain implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String type;
    private String sourceCode;
    private String memberCode;
    private String createTime;
}
