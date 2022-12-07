package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.PageQuery;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.vo.SysMessageBoardVo;
import com.ruoyi.system.domain.bo.SysMessageBoardBo;
import com.ruoyi.system.service.ISysMessageBoardService;
import com.ruoyi.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
 * 留言板Controller
 *
 * @author lbzzz
 * @date 2022-12-07
 */
@Validated
@Api(value = "留言板控制器", tags = {"留言板管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/messageBoard")
public class SysMessageBoardController extends BaseController {

    private final ISysMessageBoardService iSysMessageBoardService;

    /**
     * 查询留言板列表
     */
    @ApiOperation("查询留言板列表")
    @SaCheckPermission("system:messageBoard:list")
    @GetMapping("/list")
    public TableDataInfo<SysMessageBoardVo> list(SysMessageBoardBo bo, PageQuery pageQuery) {
        return iSysMessageBoardService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出留言板列表
     */
    @ApiOperation("导出留言板列表")
    @SaCheckPermission("system:messageBoard:export")
    @Log(title = "留言板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysMessageBoardBo bo, HttpServletResponse response) {
        List<SysMessageBoardVo> list = iSysMessageBoardService.queryList(bo);
        ExcelUtil.exportExcel(list, "留言板", SysMessageBoardVo.class, response);
    }

    /**
     * 获取留言板详细信息
     */
    @ApiOperation("获取留言板详细信息")
    @SaCheckPermission("system:messageBoard:query")
    @GetMapping("/{messageId}")
    public R<SysMessageBoardVo> getInfo(@ApiParam("主键")
                                     @NotNull(message = "主键不能为空")
                                     @PathVariable("messageId") Long messageId) {
        return R.ok(iSysMessageBoardService.queryById(messageId));
    }

    /**
     * 新增留言板
     */
    @ApiOperation("新增留言板")
    @SaCheckPermission("system:messageBoard:add")
    @Log(title = "留言板", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysMessageBoardBo bo) {
        return toAjax(iSysMessageBoardService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改留言板
     */
    @ApiOperation("修改留言板")
    @SaCheckPermission("system:messageBoard:edit")
    @Log(title = "留言板", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysMessageBoardBo bo) {
        return toAjax(iSysMessageBoardService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除留言板
     */
    @ApiOperation("删除留言板")
    @SaCheckPermission("system:messageBoard:remove")
    @Log(title = "留言板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messageIds}")
    public R<Void> remove(@ApiParam("主键串")
                                       @NotEmpty(message = "主键不能为空")
                                       @PathVariable Long[] messageIds) {
        return toAjax(iSysMessageBoardService.deleteWithValidByIds(Arrays.asList(messageIds), true) ? 1 : 0);
    }
}
