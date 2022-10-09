package com.ruoyi.member.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @version V1.0
 * @program: teamwork
 * @package: com.projectm.vo
 * @description: 邀请用户-vo
 * @author: lzd
 * @create: 2020-07-01 11:28
 **/
@Builder
@Data
public class MemberVo {

    private String accountCode;
    private String avatar;
    private String name;
    private String email;
    private boolean joined;

}
