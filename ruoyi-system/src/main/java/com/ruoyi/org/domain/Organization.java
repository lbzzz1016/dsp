package com.ruoyi.org.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.domain.BaseDomain;
import lombok.*;

import java.io.Serializable;

@TableName("team_organization")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String avatar;
    private String description;
    private String ownerCode;
    private String createTime;
    private Integer personal;
    private String code;
    private String address;
    private Integer province;
    private Integer city;
    private Integer area;
}
