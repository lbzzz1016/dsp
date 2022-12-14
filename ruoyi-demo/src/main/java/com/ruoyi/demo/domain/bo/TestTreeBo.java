package com.ruoyi.demo.domain.bo;

import com.ruoyi.common.core.domain.TreeEntity;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 测试树表业务对象 test_tree
 *
 * @author LBZ
 * @date 2021-07-26
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("测试树表业务对象")
public class TestTreeBo extends TreeEntity<TestTreeBo> {

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 部门id
     */
    @ApiModelProperty("部门id")
    @NotNull(message = "部门id不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long deptId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long userId;

    /**
     * 树节点名
     */
    @ApiModelProperty("树节点名")
    @NotBlank(message = "树节点名不能为空", groups = {AddGroup.class, EditGroup.class})
    private String treeName;

}
